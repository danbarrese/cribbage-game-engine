package net.pladform.cribbage.engine;

import net.pladform.cribbage.engine.util.Sum15;

import java.util.*;

/**
 * @author Dan Barrese
 */
public class Round {

    public Player[] players;
    public Turn[] turns;
    public Card[] cards;
    public int turnCount;
    public int cardCount;
    public int cardValueTotal;
    public int maxTurns;
    public Card startCard;

    public Round(Player[] players, Card startCard) {
        this.startCard = startCard;
        this.maxTurns = players.length * 4; // magic number
        this.players = players;
        this.turns = new Turn[maxTurns];
        this.cards = new Card[maxTurns];
        cardValueTotal = 0;
        turnCount = 0;
        cardCount = 0;
    }

    public List<Score> take(Turn turn) {
        turns[turnCount++] = turn;
        if (turn.card != Card.NIL) {
            cards[cardCount++] = turn.card;
            cardValueTotal += turn.card.value;

            List<Score> scores = scoreLastTurn(turn.player);

            // last card +1, got 31 +2
            if (isOver()) {
                if (cardValueTotal == 31) {
                    scores.add(Score.LAST_CARD_31);
                } else {
                    scores.add(Score.LAST_CARD);
                }
            }
            return scores;
        } else {
            return Collections.emptyList();
        }
    }

    public int pointsRemaining() {
        return 31 - cardValueTotal;
    }

    public boolean isOver() {
        if (turnCount == maxTurns) {
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
        return cards[cardCount - 1 - distance];
    }

    public List<Score> scoreEndOfRound(Player player) {
        List<Score> scores = new ArrayList<>();
        Set<Card> finalCards = new HashSet<>();
        for (Turn t : turns) {
            if (t != null && t.player == player && t.card != Card.NIL) {
                finalCards.add(t.card);
            }
        }
        finalCards.add(startCard);
        scores.addAll(scoreEndOfRound(finalCards, startCard, false));
        if (player.dealer) {
            scores.addAll(scoreCribHand(player));
        }
        return scores;
    }

    public List<Score> scoreLastTurn(Player player) {
        List<Score> scores = new ArrayList<>();
        // 2..4-of-a-kind
        if (cardCount >= 2 && cardFromLast(0).isDouble(cardFromLast(1))) {
            if (cardCount >= 3 && cardFromLast(1).isDouble(cardFromLast(2))) {
                if (cardCount >= 4 && cardFromLast(2).isDouble(cardFromLast(3))) {
                    scores.add(Score.FOUR_OF_A_KIND);
                } else {
                    scores.add(Score.THREE_OF_A_KIND);
                }
            } else {
                scores.add(Score.TWO_OF_A_KIND);
            }
        }

        // sum of 15, +2
        if (cardValueTotal == 15) {
            scores.add(Score.SUM_15);
        }

        // run of 3..8
        for (int i = 8; i >= 3; i--) {
            if (cardCount >= i) {
                if (Card.isRun(Arrays.copyOfRange(cards, cardCount - i, cardCount))) {
                    scores.add(new Score(i));
                    break;
                }
            }
        }
        return scores;
    }

    @Override
    public String toString() {
        return String.format("Round: %s -- StartCard: %s", Arrays.toString(turns), startCard);
    }

    public static List<Score> scoreEndOfRound(Set<Card> finalCards, Card startCard, boolean isCribHand) {
        List<Score> scores = new ArrayList<>();
        Hand hand = new Hand(finalCards);

        // SUM_15 = new Score(2);
        int sum15s = Sum15.countSum15sInHand(hand);
        for (int i = 0; i < sum15s; i++) {
            scores.add(Score.SUM_15);
        }

        // JACK_SUIT_MATCHES_START_CARD = new Score(1);
        for (Card jack : hand.getJacks()) {
            if (startCard.suit == jack.suit) {
                scores.add(Score.JACK_SUIT_MATCHES_START_CARD);
            }
        }

        // RUN_OF_3 = new Score(3);
        // RUN_OF_4 = new Score(4);
        // RUN_OF_5 = new Score(5);
        Card[] handCards = hand.cards.toArray(new Card[hand.cards.size()]);
        for (int i = 0; i < handCards.length - 3; i++) {
            if (i + 2 < handCards.length && Card.isRun(Arrays.copyOfRange(handCards, i, i + 3))) {
                if (i + 3 < handCards.length && Card.isRun(Arrays.copyOfRange(handCards, i, i + 4))) {
                    if (i + 4 < handCards.length && Card.isRun(Arrays.copyOfRange(handCards, i, i + 5))) {
                        scores.add(Score.RUN_OF_5);
                        break;
                    } else {
                        scores.add(Score.RUN_OF_4);
                        break;
                    }
                } else {
                    scores.add(Score.RUN_OF_3);
                    break;
                }
            }
        }

        // TWO_OF_A_KIND = new Score(2);
        // THREE_OF_A_KIND = new Score(3);
        // FOUR_OF_A_KIND = new Score(4);
        Map<Card.Type, Integer> cardCounts = new HashMap<>();
        for (Card card : hand.cards) {
            cardCounts.compute(card.type, (cardd, count) -> count == null ? 1 : count + 1);
        }
        cardCounts.forEach((card, count) -> {
            switch (count) {
                case 2:
                    scores.add(Score.TWO_OF_A_KIND);
                    break;
                case 3:
                    scores.add(Score.THREE_OF_A_KIND);
                    break;
                case 4:
                    scores.add(Score.FOUR_OF_A_KIND);
                    break;
                default:
                    break;
            }
        });

        // FLUSH = new Score(4);
        // FLUSH_PLUS_START_CARD = new Score(5);
        Suit suit = null;
        boolean flush = true; // prove me wrong
        for (Card card : hand.cards) {
            if (card == startCard) {
                continue;
            }
            if (suit == null) {
                suit = card.suit;
            }
            if (card.suit != suit) {
                flush = false;
                break;
            }
        }
        if (flush && isCribHand && startCard.suit == suit) {
            // TODO: crib hand flush is 4 points or 5?
            scores.add(Score.FLUSH_PLUS_START_CARD);
        } else if (flush) {
            if (startCard.suit == suit) {
                scores.add(Score.FLUSH_PLUS_START_CARD);
            } else {
                scores.add(Score.FLUSH);
            }
        }

        return scores;
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

    private List<Score> scoreCribHand(Player player) {
        Set<Card> finalCards = new HashSet<>();
        finalCards.addAll(player.crib.cards);
        finalCards.add(startCard);
        return scoreEndOfRound(finalCards, startCard, true);
    }

}
