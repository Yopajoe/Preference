package org.etsntesla.gava.handlers;


import org.etsntesla.gava.models.Game;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import java.util.Map;



public class CustomWebSocketHandlerMapping extends SimpleUrlHandlerMapping {



    public void setGameHandler(Game game){
        String path = "/websocket"+game.GAME_ID;
        Map<String, Game> mapUrl = (Map<String, Game>) getUrlMap();
        mapUrl.put(path,game);
        setUrlMap(mapUrl);
    }

}
