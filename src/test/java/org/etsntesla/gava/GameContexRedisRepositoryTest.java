package org.etsntesla.gava;


import org.etsntesla.gava.auxiliaries.CustomRedisTemplate;
import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.models.GameContext;
import org.etsntesla.gava.repositories.GameContexRedisRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,classes = Main.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameContexRedisRepositoryTest {

    @Autowired
    GameContexRedisRepository repository;


    CustomRedisTemplate customRedisTemplate = new CustomRedisTemplate();


    @Test
    @Order(1)
    public void save(){
        Game game = new Game();
        GameContext gameContext = (GameContext) game.getGameContextListener();
        System.out.println(gameContext);
        Assertions.assertEquals(gameContext.getGAME_ID(),repository.save(gameContext).getGAME_ID(),"Greska");
    }

    @Test
    @Order(2)
    public void saveAll1(){
        Game game = new Game();
        GameContext gameContext = (GameContext) game.getGameContextListener();
        List<GameContext> gameContexts = new ArrayList<>();
        for(int i=0;i<4;i++)gameContexts.add(gameContext);
        repository.saveAll(gameContexts);
        Assertions.assertEquals(2,customRedisTemplate.boundZSetOps("games").count(0.0,0.0),"saveAllRedisRepositories1");
        Assertions.assertEquals(gameContext.getGAME_NAME(),((GameContext)customRedisTemplate.opsForValue().get(String.valueOf(gameContext.getGAME_ID()))).getGAME_NAME(),"saveAllRedisRepositories2");
    }

    @Test
    @Order(3)
    public void saveAll2(){

        List<GameContext> gameContexts = new ArrayList<>();
        for(int i=0;i<4;i++){
            Game game = new Game();
            GameContext gameContext = (GameContext) game.getGameContextListener();
            gameContexts.add(gameContext);
        }
        repository.saveAll(gameContexts);
        Assertions.assertEquals(6,customRedisTemplate.boundZSetOps("games").count(0.0,0.0),"saveAllRedisRepositories1");
    }

    @Test
    @Order(4)
    public void findById(){
        int expect  = repository.findById("2").get().getGAME_ID();
        Assertions.assertEquals(2,2,"findById");
    }

    @Test
    @Order(5)
    public void existById(){
        Assertions.assertTrue(repository.existsById("2"));
        Assertions.assertFalse(repository.existsById("40"));
    }

    @Test
    @Order(6)
    public void findAll(){
        List<GameContext> gameContexts = (List<GameContext>) repository.findAll();
        Assertions.assertEquals(6,gameContexts.size(),"findAll");
    }

    @Test
    @Order(7)
    public void findAllWithParameters(){
        List<GameContext> gameContexts = (List<GameContext>) repository.findAll(2l,2l);
        Assertions.assertEquals(2,gameContexts.size(),"findAll");
    }

    @Test
    @Order(8)
    public void findAllById(){
        List<String> gamesId = ((Collection<GameContext>)repository.findAll(0l,3l))
                .stream()
                .map(gameContext -> String.valueOf(gameContext.getGAME_ID()))
                .toList();
        Assertions.assertEquals(3,((Collection<GameContext>)repository.findAllById(gamesId)).size(),"findAllById");

    }

    @Test
    @Order(9)
    public void count(){
        Assertions.assertEquals(6l,repository.count(),"count");
        Assertions.assertEquals(6l,repository.count(0),"count");
    }

    @Test
    @Order(10)
    public void deleteById(){
        repository.deleteById("4");
        Assertions.assertTrue(repository.findById("4").isEmpty(),"deleteById2");
        Assertions.assertEquals(5l,repository.count(),"deleteById1");
    }

    @Test
    @Order(11)
    public void delete(){
        GameContext gameContext = repository.findById("3").get();
        repository.delete(gameContext);
        Assertions.assertEquals(4l,repository.count(),"deleteById1");

    }

    @Test
    @Order(12)
    public  void deleteAll(){
        String[] ids = new String[]{"1","5","3"};
        List<GameContext> gameContexts = (List<GameContext>)repository.findAllById(Arrays.stream(ids).toList());
        repository.deleteAll(gameContexts);
        Assertions.assertEquals(2l,repository.count(),"deleteAll");
    }



}
