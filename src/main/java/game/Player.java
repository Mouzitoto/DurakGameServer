package game;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class Player {
    String id;
    String name;
    List<Card> cards;
    Connection connection;

    public Player() {
        cards = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.replace(",", "");
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
