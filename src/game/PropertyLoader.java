package game;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {
    public int seed;
    public int nbPlayers;
    
    public int nbStartCards;
    public int winningScore;
    public boolean enforceRules;
    public int thinkingTime;

    public String[] playerLogic;

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
            String[] fileNames = {"whist", "legal", "smart"};

            //parsing through file and loading the file in priority with enable flag.
            inStream = new FileReader(String.format("src/properties/%s.properties", fileNames[i++]));
            whistProperties.load(inStream);

            while (!Boolean.parseBoolean(whistProperties.getProperty("Enable"))){
                inStream.close();
                inStream = new FileReader(String.format("src/properties/%s.properties", fileNames[i++]));
                whistProperties.load(inStream);
            }

        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        //Loading properties
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

        playerLogic = new String[nbPlayers];

        //defining the strategies used by each player.
        for (int i = 0; i < nbPlayers; i++){
            playerLogic[i] = whistProperties.getProperty(String.format("player_%d", i + 1));
        }

        return whistProperties;
    }

}