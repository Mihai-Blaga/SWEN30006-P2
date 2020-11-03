package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static game.Utils.*;
import static game.CardSelectorFactory.*;

@SuppressWarnings("serial")
public class Whist extends CardGame {

    static final Random random = ThreadLocalRandom.current();

	public enum Suit
	{
		SPADES, HEARTS, DIAMONDS, CLUBS
	}

	public enum Rank
	{
		// Reverse order of rank importance (see rankGreater() in Utils.java)
		// Order of cards is tied to card images
		ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
	}

	final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

	
	private final String version = "1.0";
	private final int handWidth = 400;
	private final int trickWidth = 40;

	PropertyLoader config;

	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
	private Player[] players;
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
	private final Location hideLocation = new Location(-500, -500);
	private final Location trumpsActorLocation = new Location(50, 50);


	Font bigFont = new Font("Serif", Font.BOLD, 36);

	private void updateScoreGraphics(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(players[player].score), Color.WHITE, bgColor, bigFont);
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
		Suit lead;
		//set these to meaningless values since java doesn't believe they are intialised always :/
		int winner = -1;
		Card winningCard = null;

		int nextPlayer = random.nextInt(config.nbPlayers); // randomly select player to lead for this round
		for (int i = 0; i < config.nbStartCards; i++) {
			//Setup trick
			trick = new Hand(deck);
			lead = null;
			for (int j = 0; j < config.nbPlayers; j++) {
				selected = null;
				if (players[nextPlayer].isHuman) {
					players[nextPlayer].hand.setTouchEnabled(true);
					setStatusText("Player " + nextPlayer + " double-click on card to follow.");
					while (null == selected) delay(100);
				} else {
					setStatusText("Player " + nextPlayer + " thinking...");
					delay(config.thinkingTime);
					selected = players[nextPlayer].playCard(lead, trumps);
				}
				// Follow with selected card
				trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
				trick.draw();
				selected.setVerso(false);  // In case it is upside down
				//Set lead
				if (j == 0) {
					lead = (Suit) selected.getSuit();
					winner = nextPlayer;
					winningCard = selected;
					System.out.println("New trick: Lead Player = "+nextPlayer+", Lead suit = "+selected.getSuit()+", Trump suit = "+trumps);
				}
				// Check: Following card must follow suit if possible
				else if (selected.getSuit() != lead && players[nextPlayer].hand.getNumberOfCardsWithSuit(lead) > 0) {
					// Rule violation
					String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
					setStatusText(violation);
					if (config.enforceRules) {
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
				System.out.println("Player "+nextPlayer+" play: "+selected.toString()+" from ["+handToString(players[nextPlayer].hand)+"]");
				if ( // beat current winner with higher card
					(selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
						// trumped when non-trump was winning
					(selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
					winner = nextPlayer;
					winningCard = selected;
				}
				// End Follow
				if (++nextPlayer >= config.nbPlayers) nextPlayer = 0;  // From last back to first
			}
			//Win Round
			delay(600);
			trick.setView(this, new RowLayout(hideLocation, 0));
			trick.draw();		
			nextPlayer = winner;
			System.out.println("Winner: "+ winner);
			setStatusText("Player " + nextPlayer + " wins trick.");
			players[nextPlayer].score++;
			updateScoreGraphics(nextPlayer);
			if (config.winningScore == players[nextPlayer].score) {
				removeActor(trumpsActor);
				return Optional.of(nextPlayer);
			}
		}
		removeActor(trumpsActor);
		return Optional.empty();
	}

	public Whist() {
		super(700, 700, 30);
		setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
		config = new PropertyLoader();
		players = new Player[config.nbPlayers];
		for (int i = 0; i < config.nbPlayers; i++) {
			players[i] = new Player(deck);
			//set the CardPicker of each player.
			players[i].setStrategy(getStrategy(config.playerLogic[i]));
			players[i].isHuman = config.playerLogic[i].contains("human");
		}
		initScore();
		playGame();
	}

	private void initScore() {
		for (int i = 0; i < config.nbPlayers; i++) {
			players[i].score = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
	}

	private void initRound() {
		Hand[] hands = deck.dealingOut(config.nbPlayers, config.nbStartCards); // Last element of hands is leftover cards; these are ignored
		for (int i = 0; i < config.nbPlayers; i++) {
			players[i].hand = hands[i];
			players[i].hand.sort(Hand.SortType.SUITPRIORITY, true);
		}
		// Set up human player for interaction
		CardListener cardListener = new CardAdapter()  // Human Player plays card
			{
				@Override
				public void leftDoubleClicked(Card card) { selected = card; players[0].hand.setTouchEnabled(false); }
			};
		players[0].hand.addCardListener(cardListener);
		// graphics
		RowLayout[] layouts = new RowLayout[config.nbPlayers];
		for (int i = 0; i < config.nbPlayers; i++) {
			layouts[i] = new RowLayout(handLocations[i], handWidth);
			layouts[i].setRotationAngle(90 * i);
			// layouts[i].setStepDelay(10);
			players[i].hand.setView(this, layouts[i]);
			players[i].hand.setTargetArea(new TargetArea(trickLocation));
			players[i].hand.draw();
		}

		//for (int i = 1; i < nbPlayers; i++)  // This code can be used to visually hide the cards in a hand (make them face down)
		//players[i].hand.setVerso(true);
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

	public int getNumPlayers() {
		return config.nbPlayers;
	}

}
