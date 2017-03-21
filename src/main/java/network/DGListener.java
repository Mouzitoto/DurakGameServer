package network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import game.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

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


            }
        }
    }

    private void attack(Connection connection, PrivateMsg privateMsg) {
        //remove card from player cards
        Player player = DGServer.players.get(connection);
        Room room = DGServer.rooms.get(privateMsg.getRoomId());

        Card currentCard = null;
        for (Card card : player.getCards())
            if(card.getId() == privateMsg.getCardId())
                currentCard = card;
        player.getCards().remove(currentCard);

        Iterator<Card> iterator = player.getCards().iterator();
        while(iterator.hasNext())
            if(iterator.next().getId() == privateMsg.getCardId())
                iterator.remove();

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



        room.setDeck(GameUtils.createShuffledDeck());
        room.setTableTop(new ArrayList<>());

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
