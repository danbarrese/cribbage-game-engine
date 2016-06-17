package net.pladform.cribbage.engine;

/**
 * @author Dan Barrese
 */
public class Score {

    public static final int LAST_CARD = 1;
    public static final int LAST_CARD_31 = 2;
    public static final int TWO_OF_A_KIND = 2;
    public static final int THREE_OF_A_KIND = 6;
    public static final int FOUR_OF_A_KIND = 12;
    public static final int FLUSH = 4;
    public static final int FLUSH_PLUS_START_CARD = 5;
    public static final int RUN_OF_3 = 3;
    public static final int RUN_OF_4 = 4;
    public static final int RUN_OF_5 = 5;
    public static final int JACK_SUIT_MATCHES_START_CARD = 1;
    public static final int SUM_15 = 2;

    public static int ofAKind(int count) {
        switch (count) {
            case 2:
                return 2;
            case 3:
                return 6;
            case 4:
                return 12;
            default:
                return 0;
        }
    }

}
