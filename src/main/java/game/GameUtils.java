package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mouzitoto on 20.03.2017.
 */
public class GameUtils {
    public static List<Card> createShuffledDeck() {
        List<Card> deck = new ArrayList<>();

        int cardId = 0;
        int cardStartValue = 6;

        for (int i = cardStartValue; i < 15; i++) {
            Card card = new Card();
            card.setId(cardId++);
            card.setValue(i);
            card.setSuit(Suit.DIAMONDS);

            deck.add(card);
        }

        for (int i = cardStartValue; i < 15; i++) {
            Card card = new Card();
            card.setId(cardId++);
            card.setValue(i);
            card.setSuit(Suit.HEARTS);

            deck.add(card);
        }

        for (int i = cardStartValue; i < 15; i++) {
            Card card = new Card();
            card.setId(cardId++);
            card.setValue(i);
            card.setSuit(Suit.CLUBS);

            deck.add(card);
        }

        for (int i = cardStartValue; i < 15; i++) {
            Card card = new Card();
            card.setId(cardId++);
            card.setValue(i);
            card.setSuit(Suit.SPADES);

            deck.add(card);
        }

        Collections.shuffle(deck);

        return deck;
    }

    public static Card getFirstCardFromTheDeck(List<Card> deck) {
        Card card = deck.get(deck.size());

        deck.remove(deck.size());

        return card;
    }

    public static Player findFirstMover(List<Player> players, Suit trump) {
        Player firstMover = players.get(0);

        int minCardValue = 15;

        for (Player player : players)
            for (Card card : player.getCards())
                if (card.getSuit().equals(trump) && card.getValue() < minCardValue) {
                    firstMover = player;
                    minCardValue = card.getValue();
                }

        return firstMover;
    }

}
