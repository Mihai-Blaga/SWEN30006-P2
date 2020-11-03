package game.filters;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import game.Whist.Suit;

public interface CardFilter {
    public ArrayList<Card> filter(ArrayList<Card> hand, Suit lead, Suit trump);
}
