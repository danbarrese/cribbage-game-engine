package net.pladform.cribbage.engine;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dan Barrese
 */
public class DeckTest {

    @Test
    public void testShuffle() throws Exception {
        Deck deck = new Deck();
        System.out.println(deck);
        for (int i = 0; i < 100; i++) {
            System.out.println(deck.shuffle());
        }
    }

}