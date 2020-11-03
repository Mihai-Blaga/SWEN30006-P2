package game.filters;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import game.Whist.Suit;

public class TrumpSaving implements CardFilter {
    @Override
    public ArrayList<Card> filter(ArrayList<Card> hand, Suit lead, Suit trump) {
        ArrayList<Card> leads = new ArrayList<>();
        ArrayList<Card> trumps = new ArrayList<>();
        for (Card card: hand) {
            if (card.getSuit() == trump)
                trumps.add(card);
            else if (card.getSuit() == lead || lead == null)
                leads.add(card); 
        }
        return (leads.size() > 0) ? leads : trumps;
    }
}
