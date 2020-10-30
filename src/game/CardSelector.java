package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public interface CardSelector {

    public Card select(ArrayList<Card> hand, Whist.Suit lead, Whist.Suit trump);
}
