package net.pladform.cribbage.engine;

import net.pladform.perf.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dan Barrese
 */
public class Game {

    public Scoreboard scoreboard;
    public Deck deck;
    public List<Round> rounds;
    public Round currentRound;

    public Game(Scoreboard scoreboard, Deck deck) {
        this.scoreboard = scoreboard;
        this.deck = deck;
        this.rounds = new ArrayList<>();
        this.currentRound = null;
    }

    public Round newRound() {
        deck.shuffle();
        scoreboard.scores.clear();
        currentRound = new Round(scoreboard.players, deck.chooseOne());
        rounds.add(currentRound);
        return currentRound;
    }

    public Map<String, List<Integer>> play() {
        Map<String, List<Integer>> stats = new HashMap<>();
        playNext(stats);
        return stats;
    }

    private void playNext(Map<String, List<Integer>> stats) {
        Timer.start("new round");
        newRound();
        Timer.stop("new round");

        Timer.start("round play");
        Player dealer = scoreboard.players[0];
        dealer.deal();

        play(stats);
    }

//    public Map<String, List<Integer>> playRounds(Player player, Set<Card> playerHand) {
//        Map<String, List<Integer>> stats = new HashMap<>();
//        Timer.start("new round");
//        newRound(); // TODO: start card should not be chosen yet!
//        Timer.stop("new round");
//
//        Timer.start("round play");
//        Player dealer = scoreboard.players[0];
//        Player nonDealer = scoreboard.players[1];
//        if (dealer == player) {
//            dealer.hand.cards = playerHand;
//            nonDealer.hand.cards = deck.deal(6);
//        } else {
//            dealer.hand.cards = deck.deal(6);
//            nonDealer.hand.cards = playerHand;
//        }
//
//        play(stats);
//        return stats;
//    }

    private void play(Map<String, List<Integer>> stats) {
        Player dealer = scoreboard.players[0];
        Player nonDealer = scoreboard.players[1];

        dealer.discard();
        nonDealer.discard();

        if (currentRound.startCard.isJack()) {
            scoreboard.addPoints(2, dealer);
        }

        if (!scoreboard.isDone()) {
            while (true) {
                int points;
                Card nonDealerCard = nonDealer.playCard();
                points = currentRound.take(new Turn(nonDealer, nonDealerCard));
                stats.putIfAbsent(currentRound.currentState.state, new ArrayList<>());
                if (scoreboard.addPoints(points, nonDealer)) {
                    break;
                }
                if (currentRound.isOver()) {
                    break;
                }

                Card dealerCard = dealer.playCard();
                points = currentRound.take(new Turn(dealer, dealerCard));
                stats.putIfAbsent(currentRound.currentState.state, new ArrayList<>());
                if (scoreboard.addPoints(points, dealer)) {
                    break;
                }
                if (currentRound.isOver()) {
                    break;
                }
            }
        }
        Timer.stop("round play");
        scoreRound(stats);
    }

    private void scoreRound(Map<String, List<Integer>> stats) {
        Timer.start("round scoring");
        Player dealer = scoreboard.players[0];
        Player nonDealer = scoreboard.players[1];

        // score non-dealer's hand
        scoreboard.addPoints(currentRound.scoreEndOfRound(nonDealer), nonDealer);

        // score dealer's hand
        if (!scoreboard.isDone()) {
            scoreboard.addPoints(currentRound.scoreEndOfRound(dealer), dealer);
        }

        currentRound.states.forEach(state -> {
            stats.putIfAbsent(state.state, new ArrayList<>());
            stats.get(state.state).add(currentRound.awardedPoints.get(state.player));
        });

        Timer.stop("round scoring");
        if (scoreboard.isDone()) {
            // game over
        } else {
            scoreboard.setDealer(nonDealer);
            playNext(stats);
        }
    }

    @Override
    public String toString() {
        return "Game: " + rounds;
    }

}
