package net.pladform.cribbage.engine;

import net.pladform.perf.Timer;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Dan Barrese
 */
public class GameTest {

    @Test
    public void testPlay() {
        Timer.start("game setup");
        Scoreboard scoreboard = new Scoreboard();
        Deck deck = new Deck();
        Game game = new Game(scoreboard, deck);
        Player dan = new Player("Dan", game);
        Player ken = new Player("Ken", game);
        scoreboard.setPlayers(new Player[]{dan, ken});
        scoreboard.setDealer(dan);
        Timer.stop("game setup");
        Timer.start("game play");
        Map<String, List<Integer>> stats = game.play();
        System.out.println(stats);
        Timer.stop("game play");
    }

    @Test
    public void testGames() throws Exception {
        int gamesPerThread = 100;
        int threads = Runtime.getRuntime().availableProcessors();
        System.out.println(String.format("threads: %d", threads));
        ExecutorService es = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            es.submit(() -> {
                for (int game = 0; game < gamesPerThread; game++) {
                    try {
                        Timer.start("entire game");
                        testPlay();
                        Timer.stop("entire game");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        es.shutdown();
        es.awaitTermination(1L, TimeUnit.DAYS);
        System.out.println(Timer.getStats());
    }

}