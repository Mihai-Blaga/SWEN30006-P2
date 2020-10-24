package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static game.utils.*;

@SuppressWarnings("serial")
public class Whist extends CardGame {

    static final Random random = ThreadLocalRandom.current();

	public enum Suit
	{
		SPADES, HEARTS, DIAMONDS, CLUBS
	}

	public enum Rank
	{
		// Reverse order of rank importance (see rankGreater() in utils.java)
		// Order of cards is tied to card images
		ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
	}

	final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

	
	private final String version = "1.0";
	private final int handWidth = 400;
	private final int trickWidth = 40;

	PropertyLoader config;

	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
	private int[] scores;
	private Hand[] hands;
	private final Location[] handLocations = {
		new Location(350, 625),
		new Location(75, 350),
		new Location(350, 75),
		new Location(625, 350)
	};

	private TextActor[] scoreActors = {null, null, null, null };
	private final Location[] scoreLocations = {
		new Location(575, 675),
		new Location(25, 575),
		new Location(575, 25),
		new Location(650, 575)
	};
	private final Location trickLocation = new Location(350, 350);
	private final Location textLocation = new Location(350, 450);
	private final Location hideLocation = new Location(-500, - 500);
	private final Location trumpsActorLocation = new Location(50, 50);
	private boolean enforceRules=false;


	Font bigFont = new Font("Serif", Font.BOLD, 36);

	private void updateScoreGraphics(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}
	

	private Card selected;
	
	//TODO abstract away some of the code in this method to others bc its a chonky boi
	/**
	 * plays a round
	 * @return the Winner number if there is one
	 */
	private Optional<Integer> playRound() {
		// Select and display trump suit
		final Suit trumps = randomEnum(Suit.class);
		final Actor trumpsActor = new Actor("sprites/"+trumpImage[trumps.ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);
		// End trump suit

		Hand trick;
		int winner;
		Card winningCard;
		Suit lead;

		int nextPlayer = random.nextInt(config.nbPlayers); // randomly select player to lead for this round
		for (int i = 0; i < config.nbStartCards; i++) {
			trick = new Hand(deck);
			selected = null;
			if (0 == nextPlayer) {  // Select lead depending on player type
				hands[0].setTouchEnabled(true);
				setStatusText("Player 0 double-click on card to lead.");
				while (null == selected) delay(100);
			} else {
				setStatusText("Player " + nextPlayer + " thinking...");
				delay(config.thinkingTime);
				selected = randomCard(hands[nextPlayer]);
			}

			// Lead with selected card
			trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
			trick.draw();
			selected.setVerso(false);
			// No restrictions on the card being lead
			lead = (Suit) selected.getSuit();
			selected.transfer(trick, true); // transfer to trick (includes graphic effect)
			winner = nextPlayer;
			winningCard = selected;
			System.out.println("New trick: Lead Player = "+nextPlayer+", Lead suit = "+selected.getSuit()+", Trump suit = "+trumps);
			System.out.println("Player "+nextPlayer+" play: "+selected.toString()+" from ["+handToString(hands[nextPlayer])+"]");
			// End Lead

			for (int j = 1; j < config.nbPlayers; j++) {
				if (++nextPlayer >= config.nbPlayers) nextPlayer = 0;  // From last back to first
				selected = null;
				if (0 == nextPlayer) {
					hands[0].setTouchEnabled(true);
					setStatusText("Player 0 double-click on card to follow.");
					while (null == selected) delay(100);
				} else {
					setStatusText("Player " + nextPlayer + " thinking...");
					delay(config.thinkingTime);
					selected = randomCard(hands[nextPlayer]);
				}
				// Follow with selected card
				trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
				trick.draw();
				selected.setVerso(false);  // In case it is upside down
				// Check: Following card must follow suit if possible
				if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
						// Rule violation
					String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
						//System.out.println(violation);
					if (enforceRules) {
						new BrokeRuleException(violation).printStackTrace();
						System.out.println("A cheating player spoiled the game!");
						System.exit(0);
						// try {
						// 	throw(new BrokeRuleException(violation));
						// } 
						// catch (BrokeRuleException e) {
						// 	e.printStackTrace();
						// 	System.out.println("A cheating player spoiled the game!");
						// 	System.exit(0);
						// }
					}
				}
				// End Check
				selected.transfer(trick, true); // transfer to trick (includes graphic effect)
				System.out.println("Winning card: "+winningCard.toString());
				System.out.println("Player "+nextPlayer+" play: "+selected.toString()+" from ["+handToString(hands[nextPlayer])+"]");
				if ( // beat current winner with higher card
					(selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
						// trumped when non-trump was winning
					(selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
					winner = nextPlayer;
					winningCard = selected;
				}
				// End Follow
			}
			//Win Round
			delay(600);
			trick.setView(this, new RowLayout(hideLocation, 0));
			trick.draw();		
			nextPlayer = winner;
			System.out.println("Winner: "+ winner);
			setStatusText("Player " + nextPlayer + " wins trick.");
			scores[nextPlayer]++;
			updateScoreGraphics(nextPlayer);
			if (config.winningScore == scores[nextPlayer]) return Optional.of(nextPlayer);
		}
		removeActor(trumpsActor);
		return Optional.empty();
	}

	public Whist() {
		super(700, 700, 30);
		setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
		config = new PropertyLoader();
		initConfig();
		initScore();
		playGame();
	}

	private void initConfig() {
		assert(config != null);

		scores = new int[config.nbPlayers];
	}

	private void initScore() {
		for (int i = 0; i < config.nbPlayers; i++) {
			scores[i] = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
	}

	private void initRound() {
		hands = deck.dealingOut(config.nbPlayers, config.nbStartCards); // Last element of hands is leftover cards; these are ignored
		for (int i = 0; i < config.nbPlayers; i++) {
			hands[i].sort(Hand.SortType.SUITPRIORITY, true);
		}
		// Set up human player for interaction
		CardListener cardListener = new CardAdapter()  // Human Player plays card
			{
				@Override
				public void leftDoubleClicked(Card card) { selected = card; hands[0].setTouchEnabled(false); }
			};
		hands[0].addCardListener(cardListener);
		// graphics
		RowLayout[] layouts = new RowLayout[config.nbPlayers];
		for (int i = 0; i < config.nbPlayers; i++) {
			layouts[i] = new RowLayout(handLocations[i], handWidth);
			layouts[i].setRotationAngle(90 * i);
			// layouts[i].setStepDelay(10);
			hands[i].setView(this, layouts[i]);
			hands[i].setTargetArea(new TargetArea(trickLocation));
			hands[i].draw();
		}

		//for (int i = 1; i < nbPlayers; i++)  // This code can be used to visually hide the cards in a hand (make them face down)
		//hands[i].setVerso(true);
		// End graphics
	}

	private void playGame() {
		Optional<Integer> winner;
		do { 
			initRound();
			winner = playRound();
		} while (!winner.isPresent());

		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over. Winner is player: " + winner.get());
		refresh();
	}
	public static void main(String[] args)
	{
    new Whist();
	}

}
