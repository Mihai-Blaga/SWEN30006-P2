package game;

import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.Card;

import game.Whist.Suit;

public class Player {
    //TODO:use is bot
    public Boolean isBot;
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
