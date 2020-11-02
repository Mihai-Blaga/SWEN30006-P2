package game;

public class CardPickerFactory {
    public static CardPicker getStrategy(String strat){
        CardPicker out = new CardPicker();

        String[] strategy = strat.split(",");

        switch(strategy[0]){
            case "trump":
                out.addFilters(new NaiveLegal());
                out.addFilters(new TrumpSaving());
                break;
            
            case "legal":
                out.addFilters(new NaiveLegal());
                break;
        }

        switch(strategy[1]){
            case "smart":
                out.setCardSelector(new SmartSelection());
                break;
            
            case "high":
                out.setCardSelector(new HighestRankSelection());
                break;

            default:
                out.setCardSelector(new RandomSelection());
        }

        return out;
    }
}
