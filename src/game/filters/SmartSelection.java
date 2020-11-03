package game.filters;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Hashtable;

import game.Whist.Suit;
import game.Whist.Rank;
import game.DeckObserver;
import static game.Utils.*;

import org.apache.commons.math3.distribution.BinomialDistribution;

public class SmartSelection extends SingleResultFilter {

    public SmartSelection(CardFilter f) {
        super(f);
    }

    private static final double THRESHOLD = 0.8;
    Hashtable<Card, Double> hashtable = new Hashtable<>();

    @Override
    public Card select(ArrayList<Card> hand, Suit lead,
                       Suit trump) {

        // calculates the probabilities for each card in the hand
        calculateProbabilities(hand, lead, trump);

        Card bestCard = hand.get(0), minOverThreshold = hand.get(0);
        for (Card card: hashtable.keySet()) {
            //bestCard is important in case we don't find anything over Threshold
            if (hashtable.get(card) > hashtable.get(bestCard))
                bestCard = card;

            // Prioritising saving of trump cards
            //TODO: Revisit the Prioritised saving of trump cards
            if (hashtable.get(card) >= THRESHOLD &&
                    !rankGreater(card, minOverThreshold)) {
                if (card.getSuit() != trump || card.getSuit() == minOverThreshold.getSuit())
                    minOverThreshold = card;
            }
        }

        return (hashtable.get(minOverThreshold) >= THRESHOLD) ?
                minOverThreshold : bestCard;
    }

    private void calculateProbabilities(ArrayList<Card> hand, Suit lead,
                                                           Suit trump) {
        hashtable.clear();
        for (Card card: hand) {
            hashtable.put(card, getProbabilities(hand, card, lead, trump));
        }
    }

    private double getProbabilities(ArrayList<Card> hand, Card card, Suit lead, Suit trump) {

        //TODO: getSuit() returns type Enum and not Suit.
        // Is comparison still valid? Try .equals maybe?
        if ((lead != null && card.getSuit() != lead) && card.getSuit() != trump) return 0;
        if (!canWin(card, trump)) return 0;

        int numCards = 0;

        // Add all cards that can beat current card
        numCards += numCardsGreater(hand, card, (Suit) card.getSuit());

        if (card.getSuit() != trump) // Add all trump cards that can beat current card
            numCards += numCardsGreater(hand, card, trump);
        //TODO:deal with static error by increasing coupling or making whist a singleton
        //Bi(numCards, (1 - (DeckObserver.getCurrentTrick().size() + 1)/ tot_players))
        BinomialDistribution distribution = new BinomialDistribution(numCards,
                1 - ((double)DeckObserver.getDeckObserver().getCurrentTrick().size()/(game.Whist.getNumPlayers()  - 1.0)));
//        System.out.println(card.toString() + " " + distribution.cumulativeProbability(0) +  " "
//                + numCards + " "  + distribution.getProbabilityOfSuccess());
        return distribution.cumulativeProbability(0);
    }

    private boolean canWin(Card card, Suit trump) {
        boolean cardIsTrump = card.getSuit() == trump;

        for (Card playedCard: DeckObserver.getDeckObserver().getCurrentTrick()) {
            if (playedCard.getRank() == trump && !cardIsTrump)
                return false;
            //TODO: double check logic (Note: card will always be either lead or trump)
            if (playedCard.getSuit() == card.getSuit()
                    && rankGreater(playedCard, card))
                return false;
        }
        return true;
    }
    
    private int numCardsGreater(ArrayList<Card> hand, Card card, Suit suit) {
        int numCardsGreater = 0;
        for (Rank rank: Rank.values()) {
            //TODO: Double check Whist.Rank.values() gives values in order.

            // Only iterate for ranks greater than you, unless trump...
            if (suit == card.getSuit() && rank == card.getRank()) // Keep going for trump cards.
                break;
            if (!DeckObserver.getDeckObserver().isPlayed(rank, suit)
                    && !contains(hand, rank, suit)) {
                numCardsGreater++;
            }
        }
        return numCardsGreater;
    }

    private boolean contains(ArrayList<Card> hand, Rank rank, Suit suit) {
        for (Card card: hand)
            if (card.getRank() == rank && card.getSuit() == suit)
                return true;
        return false;
    }
}
