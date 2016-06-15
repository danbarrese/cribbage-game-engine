package net.pladform.cribbage.engine;

/**
 * @author Dan Barrese
 */
public class Score {

    public static final Score LAST_CARD = new Score(1, "GO");
    public static final Score LAST_CARD_31 = new Score(2, "GO31");
    public static final Score TWO_OF_A_KIND = new Score(2, "KIND2");
    public static final Score THREE_OF_A_KIND = new Score(6, "KIND3");
    public static final Score FOUR_OF_A_KIND = new Score(12, "KIND4");
    public static final Score FLUSH = new Score(4, "FLUSH");
    public static final Score FLUSH_PLUS_START_CARD = new Score(5, "FLUSH+");
    public static final Score RUN_OF_3 = new Score(3, "RUN3");
    public static final Score RUN_OF_4 = new Score(4, "RUN4");
    public static final Score RUN_OF_5 = new Score(5, "RUN5");
    public static final Score JACK_SUIT_MATCHES_START_CARD = new Score(1, "JACK");
    public static final Score SUM_15 = new Score(2, "SUM15");

    public final int points;
    public final String name;

    public Score(int points) {
        this.points = points;
        this.name = null;
    }

    public Score(int points, String name) {
        this.points = points;
        this.name = name;
    }

    @Override
    public String toString() {
        String s = String.valueOf(points);
        if (name != null) {
            s += ":" + name;
        }
        return s;
    }

}
