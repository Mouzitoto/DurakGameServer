package game;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public enum MsgState {
    NEW_PLAYER,
    HANDSHAKE,
    CREATE_ROOM,
    JOIN_ROOM,
    SUCCESSFULLY_JOINED_ROOM,
    ROOM_IS_CLOSED,
    SEND_CHAT_MSG,
    START_GAME,
    GET_CARD,
    SET_TRUMP,
    NOW_MOVING_PLAYER,
    ATTACK,
    PLAYER_CARDS_COUNT,
    DEFENCE,
    END_MOVE
}
