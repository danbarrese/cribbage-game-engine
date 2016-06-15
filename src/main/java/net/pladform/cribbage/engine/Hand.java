package net.pladform.cribbage.engine;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Dan Barrese
 */
public class Hand {

    public Set<Card> cards;

    public Hand() {
        cards = new TreeSet<>();
    }

    public Hand(Card[] cards) {
        this.cards = new TreeSet<>();
        for (Card c : cards) {
            this.cards.add(c);
        }
    }

    public Hand(Set<Card> cards) {
        this.cards = cards;
    }

    public Card play(int maxValue) {
        Set<Card> possibleChoices = cards.stream()
                .filter(c -> c.value <= maxValue)
                .collect(Collectors.toSet());
        if (possibleChoices.isEmpty()) {
            return Card.NIL;
        }
        Card card = Random.GEN.choose(possibleChoices);
        cards.remove(card);
        return card;
    }

    public Set<Card> discard() {
        Set<Card> discard = Random.GEN.choose(cards, 2);
        discard.forEach(cards::remove);
        return discard;
    }

    public int size() {
        return cards.size();
    }

    public Set<Card> getJacks() {
        return cards.stream()
                .filter(Card::isJack)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return cards.toString();
    }

}
