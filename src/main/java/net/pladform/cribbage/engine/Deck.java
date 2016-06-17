package net.pladform.cribbage.engine;

import net.pladform.perf.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Dan Barrese
 */
public class Deck {

    public static final int DECK_SIZE = 52;
    public final List<Card> cards;
    private Set<Integer> remainingIndexes;

    public Deck() {
        remainingIndexes = new HashSet<>();

        cards = new ArrayList<>(DECK_SIZE);
        cards.add(new Card(1, Card.Type.ACE, Suit.CLUBS));
        cards.add(new Card(2, Card.Type._2, Suit.CLUBS));
        cards.add(new Card(3, Card.Type._3, Suit.CLUBS));
        cards.add(new Card(4, Card.Type._4, Suit.CLUBS));
        cards.add(new Card(5, Card.Type._5, Suit.CLUBS));
        cards.add(new Card(6, Card.Type._6, Suit.CLUBS));
        cards.add(new Card(7, Card.Type._7, Suit.CLUBS));
        cards.add(new Card(8, Card.Type._8, Suit.CLUBS));
        cards.add(new Card(9, Card.Type._9, Suit.CLUBS));
        cards.add(new Card(10, Card.Type.TEN, Suit.CLUBS));
        cards.add(new Card(10, Card.Type.JACK, Suit.CLUBS));
        cards.add(new Card(10, Card.Type.QUEEN, Suit.CLUBS));
        cards.add(new Card(10, Card.Type.KING, Suit.CLUBS));

        cards.add(new Card(1, Card.Type.ACE, Suit.DIAMONS));
        cards.add(new Card(2, Card.Type._2, Suit.DIAMONS));
        cards.add(new Card(3, Card.Type._3, Suit.DIAMONS));
        cards.add(new Card(4, Card.Type._4, Suit.DIAMONS));
        cards.add(new Card(5, Card.Type._5, Suit.DIAMONS));
        cards.add(new Card(6, Card.Type._6, Suit.DIAMONS));
        cards.add(new Card(7, Card.Type._7, Suit.DIAMONS));
        cards.add(new Card(8, Card.Type._8, Suit.DIAMONS));
        cards.add(new Card(9, Card.Type._9, Suit.DIAMONS));
        cards.add(new Card(10, Card.Type.TEN, Suit.DIAMONS));
        cards.add(new Card(10, Card.Type.JACK, Suit.DIAMONS));
        cards.add(new Card(10, Card.Type.QUEEN, Suit.DIAMONS));
        cards.add(new Card(10, Card.Type.KING, Suit.DIAMONS));

        cards.add(new Card(1, Card.Type.ACE, Suit.SPADES));
        cards.add(new Card(2, Card.Type._2, Suit.SPADES));
        cards.add(new Card(3, Card.Type._3, Suit.SPADES));
        cards.add(new Card(4, Card.Type._4, Suit.SPADES));
        cards.add(new Card(5, Card.Type._5, Suit.SPADES));
        cards.add(new Card(6, Card.Type._6, Suit.SPADES));
        cards.add(new Card(7, Card.Type._7, Suit.SPADES));
        cards.add(new Card(8, Card.Type._8, Suit.SPADES));
        cards.add(new Card(9, Card.Type._9, Suit.SPADES));
        cards.add(new Card(10, Card.Type.TEN, Suit.SPADES));
        cards.add(new Card(10, Card.Type.JACK, Suit.SPADES));
        cards.add(new Card(10, Card.Type.QUEEN, Suit.SPADES));
        cards.add(new Card(10, Card.Type.KING, Suit.SPADES));

        cards.add(new Card(1, Card.Type.ACE, Suit.HEARTS));
        cards.add(new Card(2, Card.Type._2, Suit.HEARTS));
        cards.add(new Card(3, Card.Type._3, Suit.HEARTS));
        cards.add(new Card(4, Card.Type._4, Suit.HEARTS));
        cards.add(new Card(5, Card.Type._5, Suit.HEARTS));
        cards.add(new Card(6, Card.Type._6, Suit.HEARTS));
        cards.add(new Card(7, Card.Type._7, Suit.HEARTS));
        cards.add(new Card(8, Card.Type._8, Suit.HEARTS));
        cards.add(new Card(9, Card.Type._9, Suit.HEARTS));
        cards.add(new Card(10, Card.Type.TEN, Suit.HEARTS));
        cards.add(new Card(10, Card.Type.JACK, Suit.HEARTS));
        cards.add(new Card(10, Card.Type.QUEEN, Suit.HEARTS));
        cards.add(new Card(10, Card.Type.KING, Suit.HEARTS));

        shuffle();
    }

    public Deck shuffle() {
        Timer.start("reset dealt cards");
        resetDealtCards();
        Timer.stop("reset dealt cards");
        Collections.shuffle(cards);
        return this;
    }

    public Card chooseOne() {
        return deal(1).stream().findFirst().get();
    }

    public Set<Card> deal(int count) {
        if (count > remainingIndexes.size()) {
            throw new IllegalStateException("no cards left in deck, cannot deal");
        }
        Set<Integer> indexes = Random.GEN.choose(remainingIndexes, count);
        Set<Card> hand = new TreeSet<>();
        for (Integer index : indexes) {
            hand.add(cards.get(index));
            remainingIndexes.remove(index);
        }
        return hand;
    }

    @Override
    public String toString() {
        return String.format("Deck: %s", cards);
    }

    // ---------------------------------
    // private methods
    // ---------------------------------

    private void resetDealtCards() {
        remainingIndexes.clear();
        for (int i = 0; i < DECK_SIZE; i++) {
            remainingIndexes.add(i);
        }
    }

}
