package net.pladform.cribbage.engine;

import org.apache.commons.lang3.Validate;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Dan Barrese
 */
public class ScoringTester {

    @Test
    public void test() throws Exception {
        String hand = "jh 5c 5d 5s 5h"; // STARTER CARD LAST

        String[] tokens = hand.split(" ");
        Validate.isTrue(tokens.length == 5);
        Set<Card> cards = new TreeSet<>();
        Card starterCard = null;
        for (String token : tokens) {
            String[] subTokens = token.split("");
            Validate.isTrue(subTokens.length == 2);
            int value;
            try {
                value = Integer.valueOf(subTokens[0]);
            } catch (Exception e) {
                value = 10;
            }
            Card card = new Card(value,
                    Card.Type.fromString(subTokens[0]),
                    Suit.fromString(subTokens[1]));
            cards.add(card);
            starterCard = card;
        }
        System.out.println(String.format("STARTER: %s", starterCard));
        int points = Round.scoreEndOfRound(cards, starterCard, false);
        System.out.println(cards);
        System.out.println(points);
    }

}