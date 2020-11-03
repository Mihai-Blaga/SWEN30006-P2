package game;

public class CardPickerFactory {
    public static SingleResultFilter getStrategy(String strat){
        SingleResultFilter out;
        BaseFilter filter = new BaseFilter();
        String[] strategy = strat.split(",");

        //setting the filters.
        switch(strategy[0]) {
            case "trump":
                filter.addFilter(new NaiveLegal());
                filter.addFilter(new TrumpSaving());
                break;
            
            case "legal":
                filter.addFilter(new NaiveLegal());
                break;
            default:
                filter.addFilter(new NaiveIllegal());
        }

        //setting the selectors.
        switch(strategy[1]) {
            case "smart":
                out = new SmartSelection(filter);
                break;
            
            case "high":
                out = new HighestRankSelection(filter);
                break;

            default:
                out = new RandomSelection(filter);
        }

        return out;
    }
}
