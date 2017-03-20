package game;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class Card {
    int id;
    int value;
    Suit suit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }
}
