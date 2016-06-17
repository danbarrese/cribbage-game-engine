package net.pladform.cribbage.engine;

import net.pladform.cribbage.engine.util.Sum15;
import net.pladform.perf.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<State> states;
    public Map<Player, Integer> finalPoints;

    public Round(Player[] players, Card startCard) {
        this.startCard = startCard;
        this.maxTurns = players.length * 4; // magic number
        this.players = players;
        this.turns = new Turn[maxTurns];
        this.cards = new Card[maxTurns];
        cardValueTotal = 0;
        turnCount = 0;
        cardCount = 0;
        states = new ArrayList<>();
        finalPoints = new HashMap<>();
    }

    public int take(Turn turn) {
        turns[turnCount++] = turn;
        if (turn.card != Card.NIL) {
            cards[cardCount++] = turn.card;
            cardValueTotal += turn.card.value;

            int points = scoreLastTurn(turn.player);
            final int turnPoints = points;
            finalPoints.compute(turn.player, (player, playersPoints) -> playersPoints == null
                    ? turnPoints
                    : playersPoints + turnPoints);

            // last card +1, got 31 +2
            if (isOver()) {
                if (cardValueTotal == 31) {
                    points += Score.LAST_CARD_31;
                } else {
                    points += Score.LAST_CARD;
                }
            }
            states.add(new State(turn.player, startCard, turn.player.hand, cards));
            return points;
        } else {
            return 0;
        }
    }

    public int pointsRemaining() {
        return 31 - cardValueTotal;
    }

    public boolean isOver() {
        Timer.start("is round over");
        if (turnCount == maxTurns) {
            Timer.stop("is round over");
            return true;
        }
        if (pointsRemaining() == 0) {
            Timer.stop("is round over");
            return true;
        }
        if (turnCount > 1 && lastTurn().player == secondToLastTurn().player) {
            Timer.stop("is round over");
            return true;
        }
        if (turnCount > 1 && lastTurn().card == Card.NIL && secondToLastTurn().card == Card.NIL) {
            Timer.stop("is round over");
            return true;
        }
        Timer.stop("is round over");
        return false;
    }

    private Card cardFromLast(int distance) {
        return cards[cardCount - 1 - distance];
    }

    public int scoreEndOfRound(Player player) {
        Timer.start("score end of round");
        Set<Card> finalCards = new HashSet<>();
        for (Turn t : turns) {
            if (t != null && t.player == player && t.card != Card.NIL) {
                finalCards.add(t.card);
            }
        }
        finalCards.add(startCard);
        int points = scoreEndOfRound(finalCards, startCard, false);
        final int endOfRoundPoints = points;
        finalPoints.compute(player, (p, playersPoints) -> playersPoints == null
                ? endOfRoundPoints
                : playersPoints + endOfRoundPoints);
        // TODO: keep track of crib hand separately?
        if (player.dealer) {
            points += scoreCribHand(player);
        }
        Timer.stop("score end of round");
        return points;
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
                if (Card.isRun(Arrays.copyOfRange(cards, cardCount - i, cardCount))) {
                    points += i;
                    break;
                }
            }
        }
        return points;
    }

    public static int scoreEndOfRound(Set<Card> finalCards, Card startCard, boolean isCribHand) {
        int points = 0;
        Hand hand = new Hand(finalCards);

        // SUM_15 = new Score(2);
        int sum15s = Sum15.countSum15sInHand(hand);
        for (int i = 0; i < sum15s; i++) {
            points += Score.SUM_15;
        }

        // JACK_SUIT_MATCHES_START_CARD = new Score(1);
        for (Card jack : hand.getJacks()) {
            if (startCard.suit == jack.suit) {
                points += Score.JACK_SUIT_MATCHES_START_CARD;
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
                        points += Score.RUN_OF_5;
                        break;
                    } else {
                        points += Score.RUN_OF_4;
                        break;
                    }
                } else {
                    points += Score.RUN_OF_3;
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
        points += cardCounts.values().stream().mapToInt(i -> Score.ofAKind(i)).sum();

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
            points += Score.FLUSH_PLUS_START_CARD;
        } else if (flush) {
            if (startCard.suit == suit) {
                points += Score.FLUSH_PLUS_START_CARD;
            } else {
                points += Score.FLUSH;
            }
        }

        return points;
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

    private int scoreCribHand(Player player) {
        Set<Card> finalCards = new HashSet<>();
        finalCards.addAll(player.crib.cards);
        finalCards.add(startCard);
        return scoreEndOfRound(finalCards, startCard, true);
    }

    // ---------------------------------
    // inner classes
    // ---------------------------------

    public class State {
        Player player;
        String state;
        public State(Player player, Card starterCard, Hand playersHand, Card[] cardsPlayed) {
            this.player = player;
            String starterCardState = starterCard.toString();
            String cardsPlayedState = Arrays.stream(cardsPlayed)
                    .filter(card -> card != null && card != Card.NIL)
                    .map(Card::toString)
                    .collect(Collectors.joining());
            String playersHandState = playersHand.cards.stream()
                    .map(Card::toString)
                    .collect(Collectors.joining());
            this.state = String.format("%s%s:%s:%s", player.dealer ? "*" : "", starterCardState, cardsPlayedState, playersHandState);
        }
    }

}
