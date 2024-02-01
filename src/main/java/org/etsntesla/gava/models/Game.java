package org.etsntesla.gava.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.etsntesla.gava.utils.exceptions.GameException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
@Scope("prototype")
public class Game extends TextWebSocketHandler implements Runnable {

    static private int nextID=0;
    public final int GAME_ID;

    public final String GAME_NAME;

    protected WebSocketSession webSocketSession;

    private volatile boolean isRunning = false;
    protected int countTurns=0;

   final private  GameContext gameContextListener;

    final private Map<Integer, GamePlayer> Players= new HashMap<>();

    public Game(){
        GAME_ID=nextID++;
        GAME_NAME="GAME-"+GAME_ID;
        gameContextListener = new GameContext();
        gameContextListener.setGameContext(this);
    }

    public static void  resetId(){
        nextID=0;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Map<Integer, GamePlayer> getPlayers() {
        return Players;
    }

    public GameContextListener getGameContextListener() {
        return gameContextListener;
    }

    public Player getPlayer(int id){
        return Players.get(id);
    }

    synchronized public Player addPlayer(Player player){
            Players.put(player.id, new GamePlayer(player));
            gameContextListener.refreshGameContext(this);
            return player;
    }

    synchronized public Player removePlayer(int playerID){
        Player player = Players.remove(playerID);
        gameContextListener.refreshGameContext(this);
        return player;
    }

    @Override
    public void run() {
        isRunning=true;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                turn();
            }
        }catch (InterruptedException | IOException ex) {
            System.out.println(ex.getMessage());
        }finally {
            System.out.println("Game ends on " + countTurns + ". turn");
            isRunning=false;
        }
    }

    synchronized private void turn() throws InterruptedException, IOException {
        Set<GamePlayer> players = new HashSet(Players.values());
        while (!players.isEmpty()){
            wait();
            if(!isRunning) Thread.currentThread().interrupt();
            for (GamePlayer player: players){
                if(player.status== Player.PlayerStatus.WAITING){
                    System.out.println("Igrac "+player.name+" je odigrao potez \n"
                        +player.properties.toString()+"\n");
                    players.remove(player);
                    if( webSocketSession != null && webSocketSession.isOpen())
                        webSocketSession.sendMessage(new TextMessage(player.properties.toString()));
                }
            }
            Thread.sleep(1500);
        }
        Players.forEach((id,player) -> player.status= Player.PlayerStatus.PLAYING );
        System.out.println("Turn number "+countTurns++);
    }

    synchronized public void doAction(int playerId, Properties properties) throws GameException {
        GamePlayer player;
        if(Players.get(playerId)!=null) {
            player = Players.get(playerId);
            player.properties=properties;
            player.status= Player.PlayerStatus.WAITING;
            notify();
        } else throw new GameException(this,"Non existing player in game!");

    }

    public int getId(int playerId) throws GameException {
        if(Players.containsKey(playerId))
            return GAME_ID;
        else
            throw new GameException(this,"Non existing player in game!");
    }

    synchronized public boolean stopGame(int playerId) {
        if(Players.containsKey(playerId) && isRunning) {
            isRunning=false;
            notifyAll();
            return true;
        } else
            return false;
    }



    static class GamePlayer extends Player {

        public GamePlayer(Player player){
            id=player.id;
            name= player.name;
        }

        @JsonCreator
        public GamePlayer(@JsonProperty("id") Integer id,
                          @JsonProperty("name")String name,
                          @JsonProperty("status")PlayerStatus status) {
            super(id, name, status);
        }

        @JsonIgnore
        Properties properties;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket is opened!");
        super.afterConnectionEstablished(session);
        webSocketSession = session;
        //Player player = (Player) session.getAttributes().get(Provisions.PLAYER);
        session.sendMessage((new TextMessage("Welcome to the WebSocket server")));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        //this.webSocketSession = session;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }




    @Override
    public String toString() {
        return "Game{" +
                "GAME_ID=" + GAME_ID +
                ", GAME_NAME='" + GAME_NAME + '\'' +
                ", countTurns=" + countTurns +
                ", gameContextListener=" + gameContextListener.toString() +
                ", Players=" + Players.toString() +
                '}';
    }



}
