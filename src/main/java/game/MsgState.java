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
    WRONG_PLAYER_CARD,
    WRONG_TARGET_CARD,
    NOT_EQUAL_CARD,
    DEFENCE,
    INVALID_DEFENCE_CARD,
    END_MOVE,
    INVALID_PLAYER_FOR_END_MOVE,
    INVALID_PLAYER_FOR_LOOSE_ROUND,
    ERROR,
    LOOSE_ROUND,
    DECK_END
}
