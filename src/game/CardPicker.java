package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class CardPicker {

    private CardFilterer cardFilter = new CardFilterer();
    private CardSelector cardSelector = null;

    public void addFilters(CardFilter newFilter) {
        cardFilter.addFilter(newFilter);
    }

    public void setCardSelector(CardSelector cardSelector) {
        this.cardSelector = cardSelector;
    }

    public Card pickCard(ArrayList<Card> hand, Whist.Suit lead, Whist.Suit trump) {

        assert (cardSelector != null);
        hand = cardFilter.filter(hand, lead, trump);
        return cardSelector.select(hand, lead, trump);
    }
}
