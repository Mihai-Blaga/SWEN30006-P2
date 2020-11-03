package game.filters;

import ch.aplu.jcardgame.Card;
import game.Whist.Suit;

import java.util.ArrayList;

public class SingleResultFilter {
    public CardFilter filter;

    public SingleResultFilter(CardFilter f) {
        this.filter = f;
    }
    
    public Card select(ArrayList<Card> hand,Suit trump,Suit lead) {
        ArrayList<Card> filteredHand = filter.filter(hand,trump,lead);

        return filteredHand.get(0);
    }
}