package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class NaiveLegal implements CardFilter {

    @Override
    public ArrayList<Card> filter(ArrayList<Card> hand, Whist.Suit lead, Whist.Suit trump) {
        ArrayList<Card> filtered = new ArrayList<>();
        if (lead == null) return filtered;
        for (Card card: hand)
            if (card.getSuit() == lead || card.getSuit() == trump)
                filtered.add(card);
        return filtered;
    }
}
