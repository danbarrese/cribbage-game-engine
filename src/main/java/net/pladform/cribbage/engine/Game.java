package net.pladform.cribbage.engine;

import net.pladform.perf.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        _play(stats);
        return stats;
    }

    private void _play(Map<String, List<Integer>> stats) {
        Timer.start("new round");
        newRound();
        Timer.stop("new round");

        Timer.start("round play");
        Player dealer = scoreboard.players[0];
        Player nonDealer = scoreboard.players[1];
        dealer.deal();

        dealer.discard();
        nonDealer.discard();

        while (true) {
            int points;
            Card nonDealerCard = nonDealer.playCard();
            points = currentRound.take(new Turn(nonDealer, nonDealerCard));
            if (scoreboard.addPoints(points, nonDealer)) {
                break;
            }
            if (currentRound.isOver()) {
                break;
            }

            Card dealerCard = dealer.playCard();
            points = currentRound.take(new Turn(dealer, dealerCard));
            if (scoreboard.addPoints(points, dealer)) {
                break;
            }
            if (currentRound.isOver()) {
                break;
            }
        }
        Timer.stop("round play");

        Timer.start("round scoring");
        // score non-dealer's hand
        scoreboard.addPoints(currentRound.scoreEndOfRound(nonDealer), nonDealer);

        // score dealer's hand
        if (!scoreboard.isDone()) {
            scoreboard.addPoints(currentRound.scoreEndOfRound(dealer), dealer);
        }

        currentRound.states.forEach(state -> {
            stats.putIfAbsent(state.state, new ArrayList<>());
            stats.get(state.state).add(currentRound.finalPoints.get(state.player));
        });

        Timer.stop("round scoring");
        if (scoreboard.isDone()) {
            // game over
        } else {
            scoreboard.setDealer(nonDealer);
            play();
        }
    }

    @Override
    public String toString() {
        return "Game: " + rounds;
    }

}
