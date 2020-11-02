package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.List;

public class DeckObserver {

    private static List<Card> playedCards = new ArrayList<>();
    private static List<Card> currentTrick = new ArrayList<>();

    //TODO: ADD endTrick and addToTrick methods in Whist.
    public static void endTrick(Card playedCard) {
        playedCards.addAll(currentTrick);
    }

    public static void addCardToTrick(Card card) {
        currentTrick.add(card);
    }

    public static boolean isPlayed(Card card) {
        return playedCards.contains(card);
    }

    public static boolean isPlayed(Whist.Rank rank, Whist.Suit suit) {
        for (Card playedCard: playedCards)
            if (playedCard.getRank().equals(rank) &&
                    playedCard.getSuit().equals(suit))
                return true;
        return false;
    }

    public static List<Card> getCurrentTrick() {
        return currentTrick;
    }
}
