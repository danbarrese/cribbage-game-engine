package net.pladform.cribbage.engine;

import net.pladform.perf.Timer;

import java.util.ArrayList;
import java.util.List;

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
        Timer.start("shuffle", Timer.Priority.LOW);
        deck.shuffle();
        Timer.stop("shuffle", Timer.Priority.LOW);
        scoreboard.scores.clear();
        currentRound = new Round(scoreboard.players, deck.chooseOne());
        rounds.add(currentRound);
        return currentRound;
    }

    public void play() {
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
            List<Score> scores;
            Card nonDealerCard = nonDealer.playCard();
            scores = currentRound.take(new Turn(nonDealer, nonDealerCard));
            if (scoreboard.addScores(scores, nonDealer)) {
                break;
            }
            if (currentRound.isOver()) {
                break;
            }

            Card dealerCard = dealer.playCard();
            scores = currentRound.take(new Turn(dealer, dealerCard));
            if (scoreboard.addScores(scores, dealer)) {
                break;
            }
            if (currentRound.isOver()) {
                break;
            }
        }
        Timer.stop("round play");

        Timer.start("round scoring");
        // score non-dealer's hand
        scoreboard.addScores(currentRound.scoreEndOfRound(nonDealer), nonDealer);

        // score dealer's hand
        if (!scoreboard.isDone()) {
            scoreboard.addScores(currentRound.scoreEndOfRound(dealer), dealer);
        }

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
