package game.filters;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Random;

import game.Whist.Suit;

public class RandomSelection extends SingleResultFilter {
    public RandomSelection(CardFilter f) {
        super(f);
    }

    @Override
    public Card select(ArrayList<Card> hand, Suit lead, Suit trump) {
        return hand.get(new Random().nextInt(hand.size()));
    }
}
