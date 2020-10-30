package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Random;

public class RandomSelection implements CardSelector {
    @Override
    public Card select(ArrayList<Card> hand, Whist.Suit lead, Whist.Suit trump) {
        return hand.get(new Random().nextInt(hand.size()));
    }
}
