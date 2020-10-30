package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class NaiveIllegal implements CardFilter{

    @Override
    public ArrayList<Card> filter(ArrayList<Card> hand, Whist.Suit lead, Whist.Suit trump) {
        return hand;
    }
}
