package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class HighestRankSelection implements CardSelector {
    @Override
    public Card select(ArrayList<Card> hand, Whist.Suit lead, Whist.Suit trump) {
        Card highest = hand.get(0);
        for (Card card: hand)
            if (card.getRank().compareTo(highest.getRank()) < 0) //TODO: check logic
                highest = card;
        return highest;
    }
}
