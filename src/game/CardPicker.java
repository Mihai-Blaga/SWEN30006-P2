package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

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

    public Card pickCard(Hand hand, Whist.Suit lead, Whist.Suit trump) {
        ArrayList<Card> handList = hand.getCardList();
        assert (cardSelector != null);
        handList = cardFilter.filter(handList, lead, trump);
        return cardSelector.select(handList, lead, trump);
    }
}
