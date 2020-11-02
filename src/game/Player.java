package game;

import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.Card;

import game.Whist.Suit;

//TODO figure out what functionality can be brought into this class from whilst.java(from playRound probably) or if we really need it?
public class Player {
    public Boolean isBot;
    public int score;
    public Hand hand;
    private CardPicker selector; 

    public Player(Deck deck) {
        score= 0;
        hand = new Hand(deck);
    }
    
    public Card playCard(Suit lead, Suit trump) {
        return selector.pickCard(hand, lead, trump);
    }

    public void setStrategy(CardPicker strat){
        selector = strat;
    }
}
