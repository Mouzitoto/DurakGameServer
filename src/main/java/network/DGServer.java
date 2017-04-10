package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import game.MsgState;
import game.Player;
import game.Room;
import game.RoomInfo;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class DGServer {
    private static Logger logger = Logger.getLogger(DGServer.class);
    //todo: if we will have a lot of players, search will be slow, think about it
    public static HashMap<Connection, Player> players = new HashMap<>();
    public static HashMap<Long, Room> rooms = new HashMap<>();
    public static long lastRoomId = 0;

    public static void main(String[] args) {
        try {
            DGServer dgServer = new DGServer();
            dgServer.start();

        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    private void start() throws IOException {
        Server server = new Server();
        server.start();
        server.bind(27015);
        server.addListener(new DGListener());

        Kryo kryo = server.getKryo();
        kryo.register(PrivateMsg.class);
        kryo.register(BroadCastMsg.class);
        kryo.register(MsgState.class);
    }

    public static List<RoomInfo> getRoomsInfo() {
        List<RoomInfo> roomsInfo = new ArrayList<>();

        for (Room room : rooms.values()) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setId(room.getId());
            roomInfo.setOwner(room.getRoomOwner().getName());
            roomInfo.setPlayersCount(room.getPlayers().size());

            roomsInfo.add(roomInfo);
        }

        return roomsInfo;
    }

    //todo: we need to monitor rooms. if it hasnt any players inside = delete it
}
