package game;

import network.DGServer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class Room {
    private static final String COMMA = ",";
    private long id;
    private List<Player> players;
    private Date createDate;
    private Date startGameDate;
    private Player roomOwner;
    private Player nowMovingPlayer;
    private List<Card> deck;
    private List<Card> tableTop;
    public int attackCardsCount;
    public int defenceCardsCount;
    private boolean isOpen;
    private List<ChatMsg> chat;
    private Suit trump;

    public Room() {
        id = DGServer.lastRoomId++;
        players = new ArrayList<>();
        createDate = new Date();
        isOpen = true;
        chat = new ArrayList<>();
        tableTop = new ArrayList<>();
        attackCardsCount = 0;
        defenceCardsCount = 0;

        //todo: check, is it works?
        DGServer.rooms.put(id, this);
    }


    public String getPlayersAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            sb.append(player.getName());
            sb.append(COMMA);
            sb.append(player.getId());
            sb.append(COMMA);
        }

        String str = sb.toString();

        return str.substring(0, str.length()-1);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStartGameDate() {
        return startGameDate;
    }

    public void setStartGameDate(Date startGameDate) {
        this.startGameDate = startGameDate;
    }

    public Player getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(Player roomOwner) {
        this.roomOwner = roomOwner;
    }

    public Player getNowMovingPlayer() {
        return nowMovingPlayer;
    }

    public void setNowMovingPlayer(Player nowMovingPlayer) {
        this.nowMovingPlayer = nowMovingPlayer;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public List<Card> getTableTop() {
        return tableTop;
    }

    public void setTableTop(List<Card> tableTop) {
        this.tableTop = tableTop;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public List<ChatMsg> getChat() {
        return chat;
    }

    public void setChat(List<ChatMsg> chat) {
        this.chat = chat;
    }

    public Suit getTrump() {
        return trump;
    }

    public void setTrump(Suit trump) {
        this.trump = trump;
    }
}
