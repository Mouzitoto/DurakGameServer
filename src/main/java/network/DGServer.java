package network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import game.Player;
import game.Room;
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
        server.bind(25015);
        server.addListener(new DGListener());
    }

    //todo: we need to monitor rooms. if it hasnt any players inside = delete it
}
