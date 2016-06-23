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
    public Player dealer;
    public Player nonDealer;
    public Card startCard;
    public List<State> states;
    public State currentState;
    public Map<Player, Integer> awardedPoints;
    public List<RoundStack> stacks;
    public RoundStack currentStack;

    public Round(Player[] players, Card startCard) {
        dealer = players[0];
        nonDealer = players[1];
        this.startCard = startCard;
        this.players = players;
        states = new ArrayList<>();
        currentState = null;
        awardedPoints = new HashMap<>();
        stacks = new ArrayList<>(3);
        stacks.add(new RoundStack());
        currentStack = stacks.get(0);
    }

//    public List<State> play() {
//        Player dealer = players[0].dealer ? players[0] : players[1];
//        Player nonDealer = players[0].dealer ? players[1] : players[0];
//        while (true) {
//            int points;
//            Card nonDealerCard = nonDealer.playCard();
//            points = take(new Turn(nonDealer, nonDealerCard));
//            if (isOver()) {
//                break;
//            }
//
//            Card dealerCard = dealer.playCard();
//            points = take(new Turn(dealer, dealerCard));
//            if (isOver()) {
//                break;
//            }
//        }
//        int nonDealerPoints = scoreEndOfRound(nonDealer);
//        int dealerPoints = scoreEndOfRound(dealer);
//        return states;
//    }

    private List<Card> allCardsThisRound() {
        List<Card> inOrder = new ArrayList<>();
        stacks.forEach(stack -> inOrder.addAll(stack.cards));
        return inOrder;
    }

    public int take(Turn turn) {
        int score = currentStack.take(turn);
        currentState = new State(turn.player, startCard, turn.player.hand, allCardsThisRound());
        states.add(currentState);
        System.out.println(currentState);
        if (currentStack.isOver() && !this.isOver()) {
            currentStack = new RoundStack();
            stacks.add(currentStack);
        }
        return score;
    }

    public boolean isOver() {
        return dealer.hand.isEmpty() && nonDealer.hand.isEmpty();
    }

    public int scoreEndOfRound(Player player) {
        Timer.start("score end of round");
        Set<Card> finalCards = new HashSet<>();
        for (RoundStack stack : stacks) {
            for (Turn t : stack.turns) {
                if (t != null && t.player == player && t.card != Card.NIL) {
                    finalCards.add(t.card);
                }
            }
        }
        finalCards.add(startCard);
        int points = scoreEndOfRound(finalCards, startCard, false);
        final int endOfRoundPoints = points;
        awardedPoints.compute(player, (p, playersPoints) -> playersPoints == null
                ? endOfRoundPoints
                : playersPoints + endOfRoundPoints);
        // TODO: keep track of crib hand separately?
        if (player.dealer) {
            points += scoreCribHand(player);
        }
        Timer.stop("score end of round");
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

    @Override
    public String toString() {
        return "Round:" + stacks;
    }

    // ---------------------------------
    // private methods
    // ---------------------------------

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

        public State(Player player, Card starterCard, Hand playersHand, List<Card> cardsPlayed) {
            this.player = player;
            String starterCardState = starterCard.toString();
            String cardsPlayedState = cardsPlayed.stream()
                    .filter(card -> card != null && card != Card.NIL)
                    .map(Card::toString)
                    .collect(Collectors.joining());
            String playersHandState = playersHand.cards.stream()
                    .map(Card::toString)
                    .collect(Collectors.joining());
            this.state = String.format("%s%s:%s:%s", player.dealer ? "*" : "", starterCardState, cardsPlayedState, playersHandState);
        }

        @Override
        public String toString() {
            return state;
        }
    }

}
