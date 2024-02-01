package org.etsntesla.gava;


import org.etsntesla.gava.auxiliaries.CustomRedisTemplate;
import org.etsntesla.gava.models.Player;
import org.etsntesla.gava.utils.stub.PlayerServiceStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;


@Disabled
public class Test1 {

    CustomRedisTemplate customRedisTemplate = new CustomRedisTemplate();

    List<Player> players = new ArrayList<>();

    @BeforeEach
    public void init(){
        PlayerServiceStub stub = new PlayerServiceStub();
        for(int i=0; i<6;i++){
            players.add(stub.get());
        }
    }

    @Test
    public void test1(){
        double factor = 0.0d;
        int reset = 0;
        for (Player p:players){
            customRedisTemplate.boundZSetOps("games").add(p,factor+(reset++%3));
        }
        List<Object> players1=customRedisTemplate.boundZSetOps("games").rangeByScore(2.0,2.0).stream().toList();
        players1.forEach(System.out::println);
    }

}
