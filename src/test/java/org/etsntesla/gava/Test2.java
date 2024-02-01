package org.etsntesla.gava;

import org.etsntesla.gava.auxiliaries.CustomRedisTemplate;
import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.models.GameContext;
import org.etsntesla.gava.models.Player;
import org.etsntesla.gava.utils.stub.PlayerServiceStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Test2 {

    CustomRedisTemplate customRedisTemplate = new CustomRedisTemplate();

    List<Player> players = new ArrayList<>();

    {
        PlayerServiceStub stub = new PlayerServiceStub();
        for(int i=0; i<6;i++){
            players.add(stub.get());
        }
        customRedisTemplate.getConnectionFactory().getConnection().flushAll();

    }

    @Disabled
    @Test
    public void test2(){
        SessionCallback<String> callback = new SessionCallback<String>() {
            @Override
            public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                int counter=0;
                for (Player p: players){
                    customRedisTemplate.boundZSetOps("games").add(p.getId(),counter++%3);
                    customRedisTemplate.opsForValue().set(String.valueOf(p.getId()),p);
                }
                List<Object> players1 = operations.exec();
                players1.forEach(System.out::println);
                return "Done";
            }
        };
        customRedisTemplate.execute(callback);
    }

    @Test
    public void test3(){
        List<GameContext> gameContexts = new ArrayList<>();
        for(int i=0;i<6;i++){
            Game game = new Game();
            GameContext gameContext = (GameContext) game.getGameContextListener();
            gameContexts.add(gameContext);
            customRedisTemplate.boundZSetOps("games").add(String.valueOf(gameContext.getGAME_ID()),0.0d);
        }
        customRedisTemplate.boundZSetOps("games").remove(Arrays.stream(new String[]{"1","3","12"}).toList());
        System.out.println(customRedisTemplate.boundZSetOps("games").range(0,10));

    }


}
