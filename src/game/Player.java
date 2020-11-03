package game;

import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.Card;

import game.Whist.Suit;
import game.filters.SingleResultFilter;

public class Player {
    public Boolean isHuman;
    public int score;
    public Hand hand;
    private SingleResultFilter selector; 

    public Player(Deck deck) {
        score= 0;
        hand = new Hand(deck);
    }
    
    public Card playCard(Suit lead, Suit trump) {
        return selector.select(hand.getCardList(), lead, trump);
    }

    public void setStrategy(SingleResultFilter strat){
        selector = strat;
    }
}
