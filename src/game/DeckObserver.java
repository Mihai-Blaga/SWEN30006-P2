package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.List;

public class DeckObserver {

    private static final int MAX_CARDS = 52;

    private List<Card> playedCards = new ArrayList<>();
    private List<Card> currentTrick = new ArrayList<>();

    private static DeckObserver instance = null;

    private DeckObserver() {}

    public static DeckObserver getDeckObserver() {
        if (instance == null) instance = new DeckObserver();
        return instance;
    }

    //TODO: ADD endTrick and addToTrick methods in Whist.
    public void endTrick() {
        playedCards.addAll(currentTrick);
        currentTrick.clear();
    }

    public void addCardToTrick(Card card) {
        currentTrick.add(card);
    }

    public boolean isPlayed(Card card) {
        return playedCards.contains(card);
    }

    public boolean isPlayed(Whist.Rank rank, Whist.Suit suit) {
        for (Card playedCard: playedCards)
            if (playedCard.getRank().equals(rank) &&
                    playedCard.getSuit().equals(suit))
                return true;
        return false;
    }

    public void newGame() {
        playedCards.clear();
    }

    public List<Card> getCurrentTrick() {
        return currentTrick;
    }

    public int cardsRemaining() {
        return MAX_CARDS - playedCards.size() - currentTrick.size();
    }
}
