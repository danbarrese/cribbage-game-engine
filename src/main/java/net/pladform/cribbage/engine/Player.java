package net.pladform.cribbage.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dan Barrese
 */
public class Player implements Comparable<Player> {

    public final String name;
    public final List<Turn> turns;
    public final Game game;
    public Hand hand;
    public boolean dealer;
    public Hand crib;

    public Player(String name, Game game) {
        this.name = name;
        // turns and game should not be here?
        turns = new ArrayList<>();
        this.game = game;
        this.hand = new Hand();
        this.crib = new Hand();
    }

    public Card playCard() {
        return hand.play(game.currentRound.pointsRemaining());
    }

    public void discard() {
        for (Player player : game.scoreboard.players) {
            if (player.dealer) {
                player.crib.cards.addAll(hand.discard());
                break;
            }
        }
    }

    public void deal() {
        if (!dealer) {
            return;
        }
        for (Player p : game.scoreboard.players) {
            p.hand = new Hand(game.deck.deal(6));
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Player o) {
        return Boolean.compare(o.dealer, this.dealer);
    }

}
