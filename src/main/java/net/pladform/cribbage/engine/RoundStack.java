package net.pladform.cribbage.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dan Barrese
 */
public class RoundStack {

    private static final int MAX_TURNS = 8;
    public Turn[] turns;
    public List<Card> cards;
    public int turnCount;
    public int cardCount;
    public int cardValueTotal;

    public RoundStack() {
        this.turns = new Turn[MAX_TURNS];
        this.cards = new ArrayList<>(MAX_TURNS);
        cardValueTotal = 0;
        turnCount = 0;
        cardCount = 0;
    }

    public int take(Turn turn) {
        turns[turnCount++] = turn;
        if (turn.card != Card.NIL) {
            cards.add(turn.card);
            cardValueTotal += turn.card.value;

            int points = scoreLastTurn(turn.player);

            // last card +1, got 31 +2
            if (isOver()) {
                if (cardValueTotal == 31) {
                    points += Score.LAST_CARD_31;
                } else {
                    points += Score.LAST_CARD;
                }
            }
            return points;
        } else {
            return 0;
        }
    }

    public int pointsRemaining() {
        return 31 - cardValueTotal;
    }

    public boolean isOver() {
        if (turnCount == MAX_TURNS) {
            return true;
        }
        if (pointsRemaining() == 0) {
            return true;
        }
        if (turnCount > 1 && lastTurn().player == secondToLastTurn().player) {
            return true;
        }
        if (turnCount > 1 && lastTurn().card == Card.NIL && secondToLastTurn().card == Card.NIL) {
            return true;
        }
        return false;
    }

    private Card cardFromLast(int distance) {
        return cards.get(cardCount - 1 - distance);
    }

    public int scoreLastTurn(Player player) {
        int points = 0;
        // 2..4-of-a-kind
        if (cardCount >= 2 && cardFromLast(0).isDouble(cardFromLast(1))) {
            if (cardCount >= 3 && cardFromLast(1).isDouble(cardFromLast(2))) {
                if (cardCount >= 4 && cardFromLast(2).isDouble(cardFromLast(3))) {
                    points += Score.FOUR_OF_A_KIND;
                } else {
                    points += Score.THREE_OF_A_KIND;
                }
            } else {
                points += Score.TWO_OF_A_KIND;
            }
        }

        // sum of 15, +2
        if (cardValueTotal == 15) {
            points += Score.SUM_15;
        }

        // run of 3..8
        for (int i = 8; i >= 3; i--) {
            if (cardCount >= i) {
                if (Card.isRun(cards.subList(cardCount - i, cardCount))) {
                    points += i;
                    break;
                }
            }
        }
        return points;
    }
    @Override
    public String toString() {
        return "Stack:" + cards;
    }

    // ---------------------------------
    // private methods
    // ---------------------------------

    private Turn lastTurn() {
        return turns[turnCount - 1];
    }

    private Turn secondToLastTurn() {
        return turns[turnCount - 2];
    }

}
