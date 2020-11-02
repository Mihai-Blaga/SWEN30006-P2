package game;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Hashtable;

public class SmartSelection implements CardSelector {

    private static final double THRESHOLD = 0.8;
    Hashtable<Card, Double> hashtable = new Hashtable<>();

    @Override
    public Card select(ArrayList<Card> hand, Whist.Suit lead,
                       Whist.Suit trump) {

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
                    !Utils.rankGreater(card, minOverThreshold)) {
                if (card.getSuit() == lead || card.getSuit() == minOverThreshold.getSuit())
                    minOverThreshold = card;
            }
        }

        return (hashtable.get(minOverThreshold) >= THRESHOLD) ?
                minOverThreshold : bestCard;
    }

    private void calculateProbabilities(ArrayList<Card> hand, Whist.Suit lead,
                                                           Whist.Suit trump) {
        hashtable.clear();
        for (Card card: hand) {
            hashtable.put(card, getProbabilities(card, lead, trump));
        }
    }

    private double getProbabilities(Card card, Whist.Suit lead, Whist.Suit trump) {

        //TODO: getSuit() returns type Enum and not Suit.
        // Is comparison still valid? Try .equals maybe?
        if (card.getSuit() != lead && card.getSuit() != trump) return 0;
        if (!canWin(card, trump)) return 0;

        int numCards = 0;

        // Add all cards that can beat current card
        numCards += numCardsGreater(card, (Whist.Suit) card.getSuit());

        if (card.getSuit() != trump) // Add all trump cards that can beat current card
            numCards += numCardsGreater(card, trump);

        //Bi(numCards, (1 - (DeckObserver.getCurrentTrick().size() + 1)/ tot_players))
        return numCards;  //TODO: Change to cdf (pdf? because we only doing F(0)) of ^
    }

    private boolean canWin(Card card, Whist.Suit trump) {
        boolean cardIsTrump = card.getSuit() == trump;

        for (Card playedCard: DeckObserver.getCurrentTrick()) {
            if (playedCard.getRank() == trump && !cardIsTrump)
                return false;
            //TODO: double check logic (Note: card will always be either lead or trump)
            if (playedCard.getSuit() == card.getSuit()
                    && Utils.rankGreater(playedCard, card))
                return false;
        }
        return true;
    }
    
    private int numCardsGreater(Card card, Whist.Suit suit) {
        int numCardsGreater = 0;
        for (Whist.Rank rank: Whist.Rank.values()) {
            //TODO: Double check Whist.Rank.values() gives values in order.

            // Only iterate for ranks greater than you, unless trump...
            if (suit == card.getSuit() && rank == card.getRank()) // Keep going for trump cards.
                break;
            if (!DeckObserver.isPlayed(rank, suit))
                numCardsGreater++;
        }
        return numCardsGreater;
    }
}
