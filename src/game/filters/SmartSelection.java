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

import org.apache.commons.math3.distribution.HypergeometricDistribution;

public class SmartSelection extends SingleResultFilter {

    public SmartSelection(CardFilter f) {
        super(f);
    }
    
    private static final double UPPER_THRESHOLD = 0.85;
    private static final double LOWER_THRESHOLD = 0.1;

    HashMap<Card, Double> cardProbabilities = new HashMap<>();

    @Override
    public Card select(ArrayList<Card> hand, Suit lead,Suit trump) {

        // calculates the probabilities for each card in the hand
        calculateProbabilities(hand, lead, trump);

        Card bestCard = hand.get(0), worstCard = hand.get(0), minOverThreshold = hand.get(0);
        for (Card card: cardProbabilities.keySet()) {

            //bestCard in case we don't find anything over Threshold
            if (cardProbabilities.get(card) >= cardProbabilities.get(bestCard)) {
                if (cardProbabilities.get(card) > cardProbabilities.get(bestCard)
                        || rankGreater(bestCard, card))
                    bestCard = card;
            }

            // worst card in case of negligible chances of winning
            if (worstCard.getSuit() == trump || (Utils.rankGreater(worstCard, card)
                    && card.getSuit() != trump)) {
                worstCard = card;
            }

            // minOverThreshold in case, multiple cards over Upper Threshold
            if (cardProbabilities.get(card) >= UPPER_THRESHOLD) {
                if (card.getSuit() == trump && (cardProbabilities.get(minOverThreshold) < UPPER_THRESHOLD ||
                        (minOverThreshold.getSuit() == trump && Utils.rankGreater(minOverThreshold, card))))
                    minOverThreshold = card;
                else if (card.getSuit() != trump && (cardProbabilities.get(minOverThreshold) < UPPER_THRESHOLD ||
                        Utils.rankGreater(minOverThreshold, card) || minOverThreshold.getRank() == trump))
                    minOverThreshold = card;
            }
        }
        // System.out.println(worstCard.toString() + " " + minOverThreshold.toString() + " " +
        // bestCard.toString());
        if (cardProbabilities.get(bestCard) <= LOWER_THRESHOLD)
            return worstCard;
        return (cardProbabilities.get(minOverThreshold) >= UPPER_THRESHOLD) ?
                minOverThreshold : bestCard;
    }

    /**
     * Calculates win probabilities for all cards
     * @param hand List of all cards
     * @param lead lead suit of the current trick
     * @param trump trump suit of the current trick
     */
    private void calculateProbabilities(ArrayList<Card> hand, Suit lead,Suit trump) {
        cardProbabilities.clear();
        for (Card card: hand) {
            cardProbabilities.put(card, getProbabilities(hand, card, lead, trump));
        }
    }

    /**
     * @param hand List of all cards
     * @param card Current card
     * @param lead lead suit of the current trick
     * @param trump trump suit of the current trick
     * @return win probability of the current card
     */
    private double getProbabilities(ArrayList<Card> hand, Card card, Suit lead, Suit trump) {
        
        if (!(lead == null || card.getSuit() == lead || card.getSuit() == trump)) return 0;
        if (!canWin(card, trump)) return 0;

        int numCardsGreater = 0;

        // Add all cards that can beat current card
        numCardsGreater += getNumCardsGreater(hand, card, (Suit) card.getSuit());

        if (card.getSuit() != trump) // Add all trump cards that can beat current card
            numCardsGreater += getNumCardsGreater(hand, card, trump);

//        BinomialDistribution distribution = new BinomialDistribution(numCardsGreater,
//                1 - ((double)DeckObserver.getDeckObserver().getCurrentTrick().size()/
//                        (game.Whist.getNumPlayers()  - 1.0)));

        HypergeometricDistribution distribution = new HypergeometricDistribution(
                DeckObserver.getDeckObserver().cardsRemaining() - hand.size(),
                numCardsGreater, Whist.getNumPlayers() -
                DeckObserver.getDeckObserver().getCurrentTrick().size() - 1);

//       System.out.println(card.toString() + " " + distribution.cumulativeProbability(0) +  " "
//               + numCardsGreater);
        return (DeckObserver.getDeckObserver().getCurrentTrick().size() > 0 ||
                distribution.cumulativeProbability(0) > UPPER_THRESHOLD || card.getSuit() != trump) ?
                distribution.cumulativeProbability(0) : 0;
    }

    /**
     *
     * @param card Card in Question
     * @param trump trump suit of the current trick
     * @return true if card can win current trick, else false.
     */
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

    /**
     *
     * @param hand List of all cards
     * @param card Current card
     * @param suit Suit of cards to calculate
     * @return number of cards that can beat current card of given suit
     */
    private int getNumCardsGreater(ArrayList<Card> hand, Card card, Suit suit) {
        int numCardsGreater = 0;
        for (Rank rank: Rank.values()) {
            // Only iterate for ranks greater than you, unless trump...
            if (suit == card.getSuit() && rank == card.getRank()) // Keep going for trump cards.
                break;
            if (!DeckObserver.getDeckObserver().isPlayed(rank, suit)
                    && !Utils.contains(hand, rank, suit)) {
                numCardsGreater++;
            }
        }
        return numCardsGreater;
    }
}
