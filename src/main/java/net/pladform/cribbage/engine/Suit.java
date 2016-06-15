package net.pladform.cribbage.engine;

/**
 * @author Dan Barrese
 */
public enum Suit {

    CLUBS("\u2663"),
    DIAMONS("\u2666"),
    SPADES("\u2660"),
    HEARTS("\u2665");

    private String unicodeIcon;

    Suit(String unicodeIcon) {
        this.unicodeIcon = unicodeIcon;
    }

    @Override
    public String toString() {
        return unicodeIcon;
    }

    public static Suit fromString(String s) {
        switch (s.substring(0, 1).toUpperCase()) {
            case "C":
                return CLUBS;
            case "D":
                return DIAMONS;
            case "S":
                return SPADES;
            case "H":
                return HEARTS;
            default:
                throw new IllegalStateException();
        }
    }

}
