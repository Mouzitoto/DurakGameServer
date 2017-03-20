package game;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class ChatMsg {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
    private static final String OPEN_SQUARE_BRACKET = "[";
    private static final String CLOSE_SQUARE_BRACKET = "] ";
    private static final String COLON = ": ";

    private Player player;
    private Date createDate;
    private String msg;

    public ChatMsg(Player player, String msg) {
        this.player = player;
        this.createDate = new Date();
        this.msg = msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OPEN_SQUARE_BRACKET);
        sb.append(sdf.format(createDate));
        sb.append(CLOSE_SQUARE_BRACKET);
        sb.append(player.getName());
        sb.append(COLON);
        sb.append(msg);

        return sb.toString();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
