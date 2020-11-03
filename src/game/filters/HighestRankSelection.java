package game.filters;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import game.Whist.Suit;
import static game.Utils.rankGreater;

public class HighestRankSelection extends SingleResultFilter {
    public HighestRankSelection(CardFilter f) {
        super(f);
    }

    @Override
    public Card select(ArrayList<Card> hand, Suit lead, Suit trump) {
        Card highest = hand.get(0);
        for (Card card: hand)
            if (rankGreater(highest,card)) //TODO: check logic
                highest = card;
        return highest;
    }
}
