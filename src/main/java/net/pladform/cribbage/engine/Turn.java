package net.pladform.cribbage.engine;

/**
 * @author Dan Barrese
 */
public class Turn {

    public final Player player;
    public final Card card;

    public Turn(Player player, Card card) {
        this.player = player;
        this.card = card;
    }

    @Override
    public String toString() {
        return player.name + ":" + card;
    }

}
