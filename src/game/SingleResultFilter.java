package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Whist.Suit;

import java.util.ArrayList;

class SingleResultFilter {
    public CardFilter filter;

    public SingleResultFilter(CardFilter f) {
        this.filter = f;
    }

    public Card select(ArrayList<Card> hand,Suit trump,Suit lead) {
        ArrayList<Card> filteredHand = filter.filter(hand,trump,lead);

        return filteredHand.get(0);
    }
}