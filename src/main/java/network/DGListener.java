package network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import game.ChatMsg;
import game.MsgState;
import game.Player;
import game.Room;

import java.util.Date;
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


            }
        }
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
