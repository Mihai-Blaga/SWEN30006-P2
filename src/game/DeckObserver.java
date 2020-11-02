package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.List;

public class DeckObserver {

    private List<Card> playedCards = new ArrayList<>();
    private List<Card> currentTrick = new ArrayList<>();

    private static DeckObserver instance = null;

    private DeckObserver() {}

    public static DeckObserver getDeckObserver() {
        if (instance == null) instance = new DeckObserver();
        return instance;
    }

    //TODO: ADD endTrick and addToTrick methods in Whist.
    public  void endTrick(Card playedCard) {
        playedCards.addAll(currentTrick);
    }

    public  void addCardToTrick(Card card) {
        currentTrick.add(card);
    }

    public  boolean isPlayed(Card card) {
        return playedCards.contains(card);
    }

    public  boolean isPlayed(Whist.Rank rank, Whist.Suit suit) {
        for (Card playedCard: playedCards)
            if (playedCard.getRank().equals(rank) &&
                    playedCard.getSuit().equals(suit))
                return true;
        return false;
    }

    public  List<Card> getCurrentTrick() {
        return currentTrick;
    }
}
