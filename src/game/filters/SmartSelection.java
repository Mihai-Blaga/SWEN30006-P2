package game.filters;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.HashMap;

import game.Utils;
import game.Whist;
import game.Whist.Suit;
import game.Whist.Rank;
import game.DeckObserver;
import static game.Utils.*;

import org.apache.commons.math3.distribution.BinomialDistribution;

public class SmartSelection extends SingleResultFilter {

    public SmartSelection(CardFilter f) {
        super(f);
    }
    
    private static final double UPPER_THRESHOLD = 0.8;
    private static final double LOWER_THRESHOLD = 0.1;

    HashMap<Card, Double> cardProbabilities = new HashMap<>();

    @Override
    public Card select(ArrayList<Card> hand, Suit lead,Suit trump) {

        // calculates the probabilities for each card in the hand
        calculateProbabilities(hand, lead, trump);

        Card bestCard = hand.get(0), worstCard = hand.get(0), minOverThreshold = hand.get(0);
        for (Card card: cardProbabilities.keySet()) {
            //bestCard is important in case we don't find anything over Threshold
            if (cardProbabilities.get(card) >= cardProbabilities.get(bestCard)) {
                if (cardProbabilities.get(card) > cardProbabilities.get(bestCard)
                        || rankGreater(bestCard, card))
                    bestCard = card;
            }
            if (worstCard.getSuit() == trump || (Utils.rankGreater(worstCard, card)
                    && card.getSuit() != trump)) {
                worstCard = card;
            }
            if (cardProbabilities.get(card) >= UPPER_THRESHOLD) {
                if (card.getSuit() == trump && (cardProbabilities.get(minOverThreshold) < UPPER_THRESHOLD ||
                        (minOverThreshold.getSuit() == trump && Utils.rankGreater(minOverThreshold, card))))
                    minOverThreshold = card;
                else if (card.getSuit() != trump && (cardProbabilities.get(minOverThreshold) < UPPER_THRESHOLD ||
                        Utils.rankGreater(minOverThreshold, card)))
                    minOverThreshold = card;
            }
        }
        System.out.println(worstCard.toString() + " " + minOverThreshold.toString() + " " + bestCard.toString());
        if (cardProbabilities.get(bestCard) <= LOWER_THRESHOLD)
            return worstCard;
        return (cardProbabilities.get(minOverThreshold) >= UPPER_THRESHOLD) ?
                minOverThreshold : bestCard;
    }

    private void calculateProbabilities(ArrayList<Card> hand, Suit lead,Suit trump) {
        cardProbabilities.clear();
        for (Card card: hand) {
            cardProbabilities.put(card, getProbabilities(hand, card, lead, trump,
                    (DeckObserver.getDeckObserver().getCurrentTrick().size() == 0) ? 1 : 0));
        }
    }

    private double getProbabilities(ArrayList<Card> hand, Card card, Suit lead, Suit trump, int quantile) {

        
        if (!(lead == null || card.getSuit() == lead || card.getSuit() == trump)) return 0;
        if (!canWin(card, trump)) return 0;

        int numCards = 0;

        // Add all cards that can beat current card
        numCards += numCardsGreater(hand, card, (Suit) card.getSuit());

        if (card.getSuit() != trump) // Add all trump cards that can beat current card
            numCards += numCardsGreater(hand, card, trump);
        //Bi(numCards, (1 - (DeckObserver.getCurrentTrick().size() + 1)/ tot_players))
        BinomialDistribution distribution = new BinomialDistribution(numCards,
                1 - ((double)DeckObserver.getDeckObserver().getCurrentTrick().size()/(game.Whist.getNumPlayers()  - 1.0)));
       System.out.println(card.toString() + " " + distribution.cumulativeProbability(quantile) +  " "
               + numCards + " "  + distribution.getProbabilityOfSuccess());
        return distribution.cumulativeProbability(quantile);
    }

    private boolean canWin(Card card, Suit trump) {
        boolean cardIsTrump = card.getSuit() == trump;

        for (Card playedCard: DeckObserver.getDeckObserver().getCurrentTrick()) {
            if (playedCard.getSuit() == trump && !cardIsTrump)
                return false;
            if (playedCard.getSuit() == card.getSuit()
                    && rankGreater(playedCard, card))
                return false;
        }
        return true;
    }
    
    //TODO:Double check this logic, what is suit meant to represent? is it the trump suit?
    private int numCardsGreater(ArrayList<Card> hand, Card card, Suit suit) {
        int numCardsGreater = 0;
        for (Rank rank: Rank.values()) {
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
    //TODO:can this be moved to utils?
    private boolean contains(ArrayList<Card> hand, Rank rank, Suit suit) {
        for (Card card: hand)
            if (card.getRank() == rank && card.getSuit() == suit)
                return true;
        return false;
    }
}
