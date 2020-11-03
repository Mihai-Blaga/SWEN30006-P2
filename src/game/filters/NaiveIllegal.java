package game.filters;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

import game.Whist.Suit;

public class NaiveIllegal implements CardFilter{

    @Override
    public ArrayList<Card> filter(ArrayList<Card> hand, Suit lead, Suit trump) {
        return hand;
    }
}
