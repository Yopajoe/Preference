package org.etsntesla.gava;


import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.models.GameContext;
import org.etsntesla.gava.models.Player;
import org.etsntesla.gava.repositories.GameContexRedisRepository;
import org.etsntesla.gava.service.GameService;
import org.etsntesla.gava.utils.stub.PlayerServiceStub;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,classes = Main.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTest {

    @Autowired
    GameService gameService;

    @Autowired
    GameContexRedisRepository repository;

    @BeforeEach
    public void init(){
        PlayerServiceStub playerServiceStub = new PlayerServiceStub();
        List<GameContext> gameContexts = new ArrayList<>();
        Game.resetId();
        for(int i=0;i<6;i++){
            Game game = new Game();
            for(int j=0; j<i%3;j++){
                Player player = playerServiceStub.get();
                game.addPlayer(player);
            }
            GameContext gameContext = (GameContext) game.getGameContextListener();
            gameContexts.add(gameContext);
        }
        repository.saveAll(gameContexts);
    }

    @AfterEach
    public void finalizing(){
        repository.deleteAll();
    }



    @Test
    @Order(1)
    public void getGameContexts(){
        List<GameContext> gameContexts = gameService.getGameContexts(2,10);
        Assertions.assertEquals(1,gameContexts.get(0).getGAME_ID(),"getGameContexts1");
        Assertions.assertEquals(4,gameContexts.get(1).getGAME_ID(),"getGameContexts2");
        Assertions.assertEquals(4,gameContexts.size(),"getGameContexts3");
    }

    @Test
    @Order(2)
    public void getGameContextsUpto1Players(){
        List<GameContext> gameContexts = gameService.getGameContextsUpToNumPlayers(1,0,10);
        Assertions.assertEquals(1,gameContexts.get(2).getGAME_ID(),"getGameContexts1");
        Assertions.assertEquals(4,gameContexts.get(3).getGAME_ID(),"getGameContexts2");
        Assertions.assertEquals(4,gameContexts.size(),"getGameContexts3");
    }

    @Test
    @Order(3)
    public void getGameContextsFor1Players(){
        List<GameContext> gameContexts = gameService.getGameContextsForNumPlayers(1,0,10);
        Assertions.assertEquals(1,gameContexts.get(0).getGAME_ID(),"getGameContexts1");
        Assertions.assertEquals(4,gameContexts.get(1).getGAME_ID(),"getGameContexts2");
        Assertions.assertEquals(2,gameContexts.size(),"getGameContexts3");
    }



}
