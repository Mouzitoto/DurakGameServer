package game;

/**
 * Created by ruslan.babich on 010 10.04.2017.
 */
public class RoomInfo {
    private String owner;
    private long id;
    private int playersCount;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
    }
}
