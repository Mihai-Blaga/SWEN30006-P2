package game.filters;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import game.Whist.Suit;

public class CompositeFilter implements CardFilter {

    private ArrayList<CardFilter> filters = new ArrayList<>();

    public CompositeFilter() {
        this.filters.add(new NaiveIllegal());
    }

    public void addFilter(CardFilter newFilter) {
        filters.add(newFilter);
    }

    @Override
    public ArrayList<Card> filter(ArrayList<Card> hand, Suit lead, Suit trump) {
        ArrayList<Card> temp;
        // TODO: Importance of order of Filtering
        for (CardFilter cardFilter: filters) {
            hand = ((temp = cardFilter.filter(hand, lead, trump)).size() > 0) ?
                    temp : hand;
        }
        return hand;
    }
}
