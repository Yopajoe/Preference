package stash;

import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Game implements Runnable, ActionListener<Player>, WebSocketMessageBrokerConfigurer {


    enum GameStatus{ BEGIN,PENDING,PLAYING,STOP,END}
    static private int nextID=0;

    public final int GAME_ID;

    private GameProperties gameProperties;

    public Game() {
        GAME_ID=nextID++;
    }

    private Map<Integer,Player> Players= new HashMap<>();

    @Override
    public void run() {
        gameProperties = new GameProperties();
        gameProperties.status=GameStatus.PLAYING;
        gameProperties.GameThreadName = Thread.currentThread().getName();
        System.out.println(gameProperties.GameThreadName);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                switch (gameProperties.getStatus()) {
                    case PLAYING : play();break;
                    case STOP: Thread.currentThread().interrupt();break;
                }
            }
        }catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }finally {
            System.out.println("Game ends on " + gameProperties.countTurns + ". turn");
            gameProperties.status = GameStatus.END;
        }

    }

    public Player getPlayer(int id){
        return Players.get(id);
    }
    synchronized public GameProperties getGameProperties(){
        return gameProperties;
    }
    synchronized public int addPlayer(Player player){
        if(Players.size()<3) {
            Players.put(player.getId(),player);
            return player.getId();
        } else return -1;
    }

    synchronized public Player removePlayer(int playerID){
        return Players.remove(playerID);
    }

    synchronized public void stopGame(){
        gameProperties.status = GameStatus.STOP;
    }

    synchronized private void play() throws InterruptedException {
        wait();
        Players.forEach((id,player) -> {
            System.out.println("Player " +  id + " sent message: " + player.getMessage());
        });
        Thread.sleep(1500);
        System.out.println("Turn number "+gameProperties.countTurns++);
    }

    public class GameProperties extends Properties {

        GameStatus status=GameStatus.BEGIN;

        String GameThreadName;

        int countTurns=0;

        public GameStatus getStatus() {
            return status;
        }

        public int getCountTurns() {
            return countTurns;
        }
    }

    @Override
    synchronized public void doAction(Player player) {
        if(player.getStatus()== Player.PlayerStatus.PLAYED) notify();
    }

}
