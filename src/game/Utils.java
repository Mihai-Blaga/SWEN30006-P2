package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Utils {
    private static Random random = ThreadLocalRandom.current();
    // return random Enum value
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
  
    // return random Card from Hand
    public static Card randomCard(Hand hand){
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }
      
    public static boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    public static String handToString(Hand h) {
        ArrayList<Card> cards=h.getCardList();
        String out = "";
        for(int i = 0; i < cards.size(); i++) {
            out += cards.get(i).toString();
            if(i < cards.size()-1) out += ",";
        }
        return(out);
    }


    /**
     * @param hand list of all cards in hand
     * @param rank Rank of card in Question
     * @param suit Suit of card in Question
     * @return true in card in hand, else false
     */
    public static boolean contains(ArrayList<Card> hand, Whist.Rank rank, Whist.Suit suit) {
        for (Card card: hand)
            if (card.getRank() == rank && card.getSuit() == suit)
                return true;
        return false;
    }
}
