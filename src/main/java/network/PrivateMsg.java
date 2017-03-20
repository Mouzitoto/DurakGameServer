package network;

import game.MsgState;

import java.util.UUID;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class PrivateMsg {
    MsgState msgState;
    String msg;
    long roomId;
    int cardId;
    int targetCardId;


    public MsgState getMsgState() {
        return msgState;
    }

    public void setMsgState(MsgState msgState) {
        this.msgState = msgState;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getTargetCardId() {
        return targetCardId;
    }

    public void setTargetCardId(int targetCardId) {
        this.targetCardId = targetCardId;
    }
}
