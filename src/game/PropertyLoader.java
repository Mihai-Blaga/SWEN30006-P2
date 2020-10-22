package game;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class PropertyLoader {
    public static int seed;
    public static int nbPlayers;
    
    public class Player {
        public String filter;
        public String selection;

        public Player(){
            filter = "none";
            selection = "random";
        }

        public Player(String filter, String selection){
            this.filter = filter;
            this.selection = selection;
        }

    }

    public static ArrayList<Player> players;
    
    public static int nbStartCards;
    public static int winningScore;
    public static boolean enforceRules;
    public static int thinkingTime;

    Properties prop;

    public PropertyLoader(){
        try{
            prop = setUpProperties();
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }
 
    //Repurposed from SWEN30006 Project 1 - Automail
    private Properties setUpProperties() throws IOException {
        Properties whistProperties = new Properties();
        
        // Default properties
        whistProperties.setProperty("Seed", "30006");
        whistProperties.setProperty("nbPlayers", "4");
        whistProperties.setProperty("player_1", "human,human");
        whistProperties.setProperty("player_2", "none,random");
        whistProperties.setProperty("player_3", "none,random");
        whistProperties.setProperty("player_4", "none,random");
        whistProperties.setProperty("nbStartCards", "13");
        whistProperties.setProperty("winningScore", "24");
        whistProperties.setProperty("enforceRules", "false");
        whistProperties.setProperty("thinkingTime", "2000");

        // Read properties
        FileReader inStream = null;
        try {
            //order of file priority: whist > legal > smart.
            int i = 0;
            String[] fileNames = {"whist.properties", "legal.properties", "smart.properties"};

            inStream = new FileReader(fileNames[i++]);
            whistProperties.load(inStream);

            while (!Boolean.parseBoolean(whistProperties.getProperty("Enable"))){
                inStream.close();
                inStream = new FileReader(fileNames[i++]);
                whistProperties.load(inStream);
            }

        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        seed = Integer.parseInt(whistProperties.getProperty("Seed"));
        System.out.println("Seed: " + seed);

        nbPlayers = Integer.parseInt(whistProperties.getProperty("nbPlayers"));
        System.out.println("#Players: " + nbPlayers);
        assert(nbPlayers > 1 && nbPlayers < 5);

        //makes sure there are enough cards for each player to have the same amount
        nbStartCards = Math.min(Integer.parseInt(whistProperties.getProperty("nbStartCards")), 52/nbPlayers);
        System.out.println("#Starting cards: " + nbStartCards);

        winningScore = Integer.parseInt(whistProperties.getProperty("winningScore"));
        System.out.println("Score needed to win: " + winningScore);

        enforceRules = Boolean.parseBoolean(whistProperties.getProperty("enforeRules"));
        System.out.println("Rules enforced: " + enforceRules);

        thinkingTime = Integer.parseInt(whistProperties.getProperty("thinkingTime"));
        System.out.println("Time to think: " + thinkingTime);

        players = new ArrayList<>();
        for (int i = 0; i < nbPlayers; i++){
            String[] playerLogic = whistProperties.getProperty(String.format("player_%d", i + 1)).split(",",2);

            Player p = this.new Player(playerLogic[0], playerLogic[1]);

            players.add(p);

            System.out.printf("Player %d has %s filter and %s selector\n", i, p.filter, p.selection);
        }

        return whistProperties;
    }

}