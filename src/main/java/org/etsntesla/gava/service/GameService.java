package org.etsntesla.gava.service;

import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.models.GameContext;
import org.etsntesla.gava.models.Player;
import org.etsntesla.gava.repositories.GameContexRedisRepository;
import org.etsntesla.gava.utils.StackTraceMessage;
import org.etsntesla.gava.utils.exceptions.GameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.function.Supplier;

@Service
public class GameService  {

    @Autowired
    private Supplier<Game> beanGameFactory;

    @Autowired
    TaskExecutor executor;

   @Autowired
   private GameContexRedisRepository redisRepository;

    protected LinkedHashMap<Integer,Game> appCacheGame = new LinkedHashMap<>();


    public void setOffset(long offset){
        redisRepository.setOffset(offset);
    }

    public void setCount(long count){
       redisRepository.setCount(count);
    }



    public Game createGame(Player player){
        Game game = beanGameFactory.get();
        game.addPlayer(player);
        GameContext gameContext = (GameContext) game.getGameContextListener();
        redisRepository.save(gameContext);
        appCacheGame.put(game.GAME_ID, game);
        return game;
    }

    public Game startGame(int id) throws GameException {
        Game game;
        Objects.requireNonNull( game = appCacheGame.get(id), StackTraceMessage.message());
        if(game.isRunning()) throw new GameException(game,"Game has been running!");
        executor.execute(game);
        return game;
    }

    public Game stopGame(int id,Player player) throws GameException{
        Game game;
        Objects.requireNonNull( game = appCacheGame.get(id),"Greska");
        if(game.isRunning()) game.stopGame(player.getId());
        return game;
    }

    public Game addPlayerToGame(int id,Player player){
        Game game;
        Objects.requireNonNull( game = appCacheGame.get(id), StackTraceMessage.message());
        game.addPlayer(player);
        GameContext gameContext = (GameContext) game.getGameContextListener();
        redisRepository.save(gameContext);
        return game;
    }

    public Game removePlayerToGame(int id,Player player){
        Game game;
        Objects.requireNonNull( game = appCacheGame.get(id), StackTraceMessage.message());
        if(game.removePlayer(player.getId())==null) return game;
        GameContext gameContext = (GameContext) game.getGameContextListener();
        redisRepository.save(gameContext);
        return game;
    }

    public Game getGame(int id){
        Game game;
        Objects.requireNonNull( game = appCacheGame.get(id), "Greska");
        return game;
    }

    public List<GameContext> getGameContexts(){
        return (List<GameContext>)redisRepository.findAll();
    }

    public List<GameContext> getGameContexts(long offset, long count){
        return (List<GameContext>)redisRepository.findAll(offset,count);
    }

    public List<GameContext> getGameContextsUpToNumPlayers(int upToNumPlayers,long offset,long count){
        long size = redisRepository.count(0,upToNumPlayers);
        if(offset+count<=size)
            return (List<GameContext>)redisRepository.findAll(offset,count);
        else if(offset<size && size-offset<=count)
            return (List<GameContext>)redisRepository.findAll(offset,size-offset);
        else
            return new ArrayList<>();
    }

    public List<GameContext> getGameContextsUpToNumPlayers(int upToNumPlayers){
        return getGameContextsUpToNumPlayers(upToNumPlayers,redisRepository.getOffset(),redisRepository.getCount());
    }



    public List<GameContext> getGameContextsForNumPlayers(int NumPlayers,long offset,long count){
        if(NumPlayers <0) return getGameContexts(offset,count);
        long size = redisRepository.count(NumPlayers);
        if(size==0) return  new ArrayList<>();
        long _offset = redisRepository.count(0,NumPlayers-1);
        offset +=_offset;
        if(offset+count<=size)
            return (List<GameContext>)redisRepository.findAll(offset,count);
        else if(size<=count)
            return (List<GameContext>)redisRepository.findAll(offset,size);
        else
            return  new ArrayList<>();
    }

    public List<GameContext> getGameContextsForNumPlayers(int NumPlayers){
        return getGameContextsForNumPlayers(NumPlayers,redisRepository.getOffset(),redisRepository.getCount());
    }

    public void deleteGame(int id) throws Exception {
        Game game;
        Objects.requireNonNull( game = appCacheGame.get(id), StackTraceMessage.message());
        redisRepository.deleteById(String.valueOf(id));
        appCacheGame.remove(id);
        game=null;
    }


}
