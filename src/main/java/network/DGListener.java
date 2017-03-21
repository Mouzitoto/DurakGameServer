package network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import game.*;

import java.util.*;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class DGListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PrivateMsg) {
            PrivateMsg privateMsg = (PrivateMsg) object;

            switch (privateMsg.getMsgState()) {
                case NEW_PLAYER:
                    newPlayer(connection, privateMsg);
                    break;

                case CREATE_ROOM:
                    createRoom(connection, privateMsg);
                    break;

                case JOIN_ROOM:
                    joinRoom(connection, privateMsg);
                    break;

                case SEND_CHAT_MSG:
                    sendChatMsg(connection, privateMsg);
                    break;

                case START_GAME:
                    startGame(privateMsg);
                    break;

                case ATTACK:
                    attack(connection, privateMsg);
                    break;

                case DEFENCE:
                    defence(connection, privateMsg);
                    break;


            }
        }
    }

    private void defence(Connection connection, PrivateMsg privateMsg) {
        Player player = DGServer.players.get(connection);
        Room room = DGServer.rooms.get(privateMsg.getRoomId());

        Card defenceCard = null;
        for (Card card : player.getCards())
            if (card.getId() == privateMsg.getCardId()) {
                defenceCard = card;
                break;
            }

        //validation for cheating
        if (defenceCard == null) {
            privateMsg.setMsgState(MsgState.WRONG_PLAYER_CARD);
            connection.sendTCP(privateMsg);
            return;
        }

        Card attackCard = null;
        for (Card card : room.getTableTop())
            if (card.getId() == privateMsg.getTargetCardId()) {
                attackCard = card;
                break;
            }

        //validation for cheating
        if (attackCard == null) {
            privateMsg.setMsgState(MsgState.WRONG_TARGET_CARD);
            connection.sendTCP(privateMsg);
            return;
        }

        //todo: defence validation
        boolean isCardValid = false;
        if (attackCard.getSuit().equals(room.getTrump())) {
            if (defenceCard.getSuit().equals(room.getTrump()))
                if (defenceCard.getValue() > attackCard.getValue())
                    isCardValid = true;
        } else { //attack card is not a trump
            if (defenceCard.getSuit().equals(attackCard.getSuit()))
                if (defenceCard.getValue() > attackCard.getValue())
                    isCardValid = true;

            if (defenceCard.getSuit().equals(room.getTrump()))
                isCardValid = true;
        }

        if (!isCardValid) {
            privateMsg.setMsgState(MsgState.INVALID_DEFENCE_CARD);
            connection.sendTCP(privateMsg);
            return;
        }

        //remove card from player cards
        player.getCards().remove(defenceCard);

        //broadcast defence cardId
        BroadCastMsg broadCastMsg = new BroadCastMsg();
        broadCastMsg.setMsgState(MsgState.DEFENCE);
        broadCastMsg.setMsg(player.getId());
        broadCastMsg.setCardId(privateMsg.getCardId());
        broadCastMsg.setTargetCardId(privateMsg.getTargetCardId());

        broadCastToRoom(room, broadCastMsg);

        //add card to tabletop
        room.getTableTop().add(defenceCard);
    }

    private void attack(Connection connection, PrivateMsg privateMsg) {
        Player player = DGServer.players.get(connection);
        Room room = DGServer.rooms.get(privateMsg.getRoomId());

        Card currentCard = null;
        for (Card card : player.getCards())
            if (card.getId() == privateMsg.getCardId()) {
                currentCard = card;
                break;
            }

        //validation for cheating
        if (currentCard == null) {
            privateMsg.setMsgState(MsgState.WRONG_PLAYER_CARD);
            connection.sendTCP(privateMsg);
            return;
        }

        //validation for repeating attack
        boolean isCardValid = false;
        if (room.getTableTop().size() > 0)
            for (Card card : room.getTableTop())
                if (card.getValue() == currentCard.getValue()) {
                    isCardValid = true;
                    break;
                }

        if (!isCardValid) {
            privateMsg.setMsgState(MsgState.NOT_EQUAL_CARD);
            connection.sendTCP(privateMsg);
            return;
        }

        //remove card from player cards
        player.getCards().remove(currentCard);

        //broadcast attacker cardId
        BroadCastMsg broadCastMsg = new BroadCastMsg();
        broadCastMsg.setMsgState(MsgState.ATTACK);
        broadCastMsg.setMsg(player.getId());
        broadCastMsg.setCardId(privateMsg.getCardId());

        broadCastToRoom(room, broadCastMsg);

        //add card to tabletop
        room.getTableTop().add(currentCard);

    }

    private void startGame(PrivateMsg privateMsg) {
        Room room = DGServer.rooms.get(privateMsg.getRoomId());
        room.setIsOpen(false);
        room.setStartGameDate(new Date());

        BroadCastMsg broadCastMsg = new BroadCastMsg();
        broadCastMsg.setMsgState(MsgState.START_GAME);

        broadCastToRoom(room, broadCastMsg);


        //create deck and tableTop
        room.setDeck(GameUtils.createShuffledDeck());

        privateMsg.setMsgState(MsgState.GET_CARD);

        //send 6 cards each player in the room
        for (Player player : room.getPlayers())
            for (int i = 0; i < 6; i++) {
                Card card = GameUtils.getFirstCardFromTheDeck(room.getDeck());
                player.getCards().add(card);
                privateMsg.setCardId(card.getId());
                player.getConnection().sendTCP(privateMsg);
            }

        //send trump card
        Card trumpCard = room.getDeck().get(0);
        broadCastMsg.setMsgState(MsgState.SET_TRUMP);
        broadCastMsg.setCardId(trumpCard.getId());

        broadCastToRoom(room, broadCastMsg);

        //send first mover
        room.setNowMovingPlayer(GameUtils.findFirstMover(room.getPlayers(), trumpCard.getSuit()));
        broadCastMsg.setMsgState(MsgState.NOW_MOVING_PLAYER);
        broadCastMsg.setMsg(room.getNowMovingPlayer().getId());

        broadCastToRoom(room, broadCastMsg);
    }

    private void sendChatMsg(Connection connection, PrivateMsg privateMsg) {
        Player player = DGServer.players.get(connection);

        ChatMsg chatMsg = new ChatMsg(player, privateMsg.getMsg());

        Room room = DGServer.rooms.get(privateMsg.getRoomId());
        room.getChat().add(chatMsg);

        BroadCastMsg broadCastMsg = new BroadCastMsg();
        broadCastMsg.setMsgState(MsgState.SEND_CHAT_MSG);
        broadCastMsg.setMsg(chatMsg.toString());

        broadCastToRoom(room, broadCastMsg);

    }

    private void broadCastToRoom(Room room, BroadCastMsg broadCastMsg) {
        for (Player player : room.getPlayers()) {
            Connection connection = player.getConnection();
            connection.sendTCP(broadCastMsg);
        }

    }

    private void joinRoom(Connection connection, PrivateMsg privateMsg) {
        Player player = DGServer.players.get(connection);
        Room room = DGServer.rooms.get(privateMsg.getRoomId());

        if (room.isOpen()) {
            if (room.getRoomOwner() == null)
                room.setRoomOwner(player);

            room.getPlayers().add(player);

            //send room player names back
            privateMsg.setMsgState(MsgState.SUCCESSFULLY_JOINED_ROOM);
            privateMsg.setMsg(room.getPlayersAsString());

            connection.sendTCP(privateMsg);
        } else {
            privateMsg.setMsg(null);
            privateMsg.setMsgState(MsgState.ROOM_IS_CLOSED);

            connection.sendTCP(privateMsg);
        }
    }

    private void newPlayer(Connection connection, PrivateMsg privateMsg) {
        //register new player
        Player player = new Player();
        player.setName(privateMsg.getMsg());
        player.setId(UUID.randomUUID().toString());
        player.setConnection(connection);

        DGServer.players.put(connection, player);

        //send playerId back
        privateMsg.setMsgState(MsgState.HANDSHAKE);
        privateMsg.setMsg(player.getId());

        connection.sendTCP(privateMsg);
    }

    private void createRoom(Connection connection, PrivateMsg privateMsg) {
        Player player = DGServer.players.get(connection);

        Room room = new Room();

        privateMsg.setMsg(String.valueOf(room.getId()));

        connection.sendTCP(privateMsg);
    }
}
