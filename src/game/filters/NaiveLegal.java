package game.filters;

import ch.aplu.jcardgame.Card;

import game.Whist.Suit;

import java.util.ArrayList;

public class NaiveLegal implements CardFilter {

    @Override
    public ArrayList<Card> filter(ArrayList<Card> hand, Suit lead, Suit trump) {
        ArrayList<Card> filtered = new ArrayList<>();
        if (lead == null) return filtered;
        for (Card card: hand)
            if (card.getSuit() == lead || card.getSuit() == trump)
                filtered.add(card);
        return filtered;
    }
}
