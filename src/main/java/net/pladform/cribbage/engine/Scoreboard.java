package net.pladform.cribbage.engine;

import net.pladform.perf.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dan Barrese
 */
public class Scoreboard {

    public static final int WINNING_SCORE = 121;
    public Player[] players;
    public Map<Player, CumulativeScore> cumulativeScore;
    public List<Score> scores;

    public Scoreboard() {
        scores = new ArrayList<>();
        cumulativeScore = new HashMap<>();
    }

    public boolean isDone() {
        return cumulativeScore.values().stream()
                .anyMatch(cs -> cs.score >= WINNING_SCORE);
    }

    public void setPlayers(Player[] players) {
        this.players = players;
        for (Player p : players) {
            cumulativeScore.put(p, new CumulativeScore());
        }
    }

    public boolean addPoints(int scores, Player player) {
        Timer.start("add scores");
        CumulativeScore cs = cumulativeScore.get(player);
        boolean gameOver = false;
        if(cs.inc(scores)) {
            gameOver = true;
        }
        Timer.stop("add scores");
        return gameOver;
    }

    public void setDealer(Player player) {
        for (Player p : players) {
            if (p.equals(player)) {
                p.dealer = true;
            } else {
                p.dealer = false;
            }
        }
        Arrays.sort(players);
    }

    @Override
    public String toString() {
        return cumulativeScore.toString();
    }

    // --------------------------
    // inner classes
    // --------------------------

    private class CumulativeScore {
        int score;
        boolean inc(int amount) {
            score += amount;
            return score >= WINNING_SCORE;
        }
        @Override
        public String toString() {
            return "" + score;
        }
    }

}
