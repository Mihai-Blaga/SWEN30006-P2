package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class SmartSelection implements CardSelector {
    @Override
    public Card select(ArrayList<Card> hand, Whist.Suit lead,
                       Whist.Suit trump) {
        return hand.get(hand.size() / 2);  // Works everytime
    }
}
