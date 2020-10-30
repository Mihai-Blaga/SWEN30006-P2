package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public interface CardFilter {
    public ArrayList<Card> filter(ArrayList<Card> hand, Whist.Suit lead, Whist.Suit trump);
}
