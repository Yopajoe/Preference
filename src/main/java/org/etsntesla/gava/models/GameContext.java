package org.etsntesla.gava.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public  class GameContext implements GameContextListener, Serializable, Comparable<GameContext> {

    protected   int GAME_ID;
    protected  String GAME_NAME;

    protected List<Player> players = new ArrayList<>();

    public GameContext() {}

    @JsonCreator
    public GameContext(
            @JsonProperty(value = "game_ID") int GAME_ID,
            @JsonProperty(value = "game_NAME")String GAME_NAME,
            @JsonProperty(value="players") List<Player> players) {
        this.GAME_ID = GAME_ID;
        this.GAME_NAME = GAME_NAME;
        this.players = players;
    }

    public int getGAME_ID() {
        return GAME_ID;
    }

    public String getGAME_NAME() {
        return GAME_NAME;
    }

    public List<Player> getPlayers() {
        return players;
    }

    //@PreferenceRedisCache(value = "PUT",type = PreferenceType.GAME)
    public void setGameContext(Game game){
        GAME_NAME=game.GAME_NAME;
        GAME_ID= game.GAME_ID;
        players.clear();
        for( Map.Entry<Integer, Game.GamePlayer> p: game.getPlayers().entrySet()){
           players.add(p.getValue());
        }
    }

    @Override
    public String toString() {
        return "GameContextRepository{" +
                "GAME_ID=" + GAME_ID +
                ", GAME_NAME='" + GAME_NAME + '\'' +
                ", players=" + players.toString() +
                '}';
    }

    @Override
    public void refreshGameContext(Game game) {
        setGameContext(game);
    }

    @Override
    public int compareTo(GameContext o) {
        return this.players.size()-o.getPlayers().size();
    }
}
