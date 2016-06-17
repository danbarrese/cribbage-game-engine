package net.pladform.cribbage.engine;

import java.util.Arrays;

/**
 * @author Dan Barrese
 */
public class Card implements Comparable<Card> {

    public static final Card NIL = new Card(0, Type.ACE, Suit.SPADES) {
        @Override
        public String toString() {
            return "NIL";
        }
    };

    public final int value;
    public final Type type;
    public final Suit suit;
    public final String formattedName;

    public Card(int value, Type type, Suit suit) {
        this.value = value;
        this.type = type;
        this.suit = suit;
        this.formattedName = (type.name().startsWith("_")
                ? type.name().substring(1)
                : type.name().substring(0, 1))
                + suit;
    }

    public boolean isDouble(Card other) {
        return this.type == other.type && this != NIL && other != NIL;
    }

    public boolean isAdjacent(Card other) {
        return Math.abs(this.type.ordinal() - other.type.ordinal()) == 1;
    }

    public static boolean isRun(Card... coll) {
        Arrays.sort(coll);
        for (int i = 0; i < coll.length - 1; i++) {
            if (!coll[i].isAdjacent(coll[i + 1])) {
                return false;
            }
        }
        return true;
    }

    public boolean isJack() {
        return type == Type.JACK;
    }

    public String getFormattedValue() {
        return value == 10 ? "T" : String.valueOf(value);
    }

    @Override
    public int compareTo(Card o) {
        int c = Integer.compare(this.value, o.value);
        if (c != 0) {
            return c;
        }
        return this.formattedName.compareTo(o.formattedName);
    }

    public enum Type {
        ACE, _2, _3, _4, _5, _6, _7, _8, _9, TEN, JACK, QUEEN, KING;

        public static Type fromString(String s) {
            switch (s.substring(0, 1).toUpperCase()) {
                case "A": return ACE;
                case "1": return ACE;
                case "2": return _2;
                case "3": return _3;
                case "4": return _4;
                case "5": return _5;
                case "6": return _6;
                case "7": return _7;
                case "8": return _8;
                case "9": return _9;
                case "10": return TEN;
                case "T": return TEN;
                case "J": return JACK;
                case "Q": return QUEEN;
                case "K": return KING;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override
    public String toString() {
        return formattedName;
    }

}
