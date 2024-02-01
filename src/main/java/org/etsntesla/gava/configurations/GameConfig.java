package org.etsntesla.gava.configurations;


import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.models.Player;
import org.etsntesla.gava.service.GameService;
import org.etsntesla.gava.utils.stub.PlayerServiceStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.config.annotation.*;

import java.util.Map;
import java.util.function.Supplier;



@Configuration
@EnableWebSocket
public class GameConfig {

    @Autowired
    GameService gameService;

    @Bean
    public Supplier<Game> beanGameFactory() {
        return () -> {
            Game game = new Game();
            //((CustomWebSocketHandlerMapping)simpleUrlHandlerMapping()).setGameHandler(game);
            return game;
        };
    }

    //    @Bean
//    @DependsOn({"gameService"})
//    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
//        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
//        Map<String, Object> urlMap = new HashMap<>();
//        Player player = new PlayerServiceStub().get();
//        Game game = gameService.createGame(player);
//        gameService.removePlayerToGame(game.GAME_ID, player);
//        urlMap.put("/ws"+game.GAME_ID,game);
//        getHandlerMap().put("/ws"+game.GAME_ID,game);
//        simpleUrlHandlerMapping.setUrlMap(urlMap);
//        return simpleUrlHandlerMapping;
//    }
    @Bean("webSocketHandlerMapping1")
    public HandlerMapping webSocketHandlerMapping() {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        Player player = new PlayerServiceStub().get();
        Game game = gameService.createGame(player);
        gameService.removePlayerToGame(game.GAME_ID, player);
        handlerMapping.setOrder(1); // Set the order if required
        handlerMapping.setUrlMap(Map.of("/ws" + game.GAME_ID, game)); // Assign the WebSocket handler to the desired path
        return handlerMapping;
    }



}