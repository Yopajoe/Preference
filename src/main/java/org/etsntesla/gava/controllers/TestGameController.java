package org.etsntesla.gava.controllers;

import org.etsntesla.gava.models.ActionListener;
import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.models.GameContext;
import org.etsntesla.gava.models.Player;
import org.etsntesla.gava.service.GameService;
import org.etsntesla.gava.utils.JsonToPropertiesConverter;
import org.etsntesla.gava.utils.Provisions;
import org.etsntesla.gava.utils.StackTraceMessage;
import org.etsntesla.gava.utils.exceptions.GameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.etsntesla.gava.utils.Provisions.GAME;
import static org.etsntesla.gava.utils.Provisions.PLAYER;

@Controller
public class TestGameController {


    @Autowired
    GameService gameService;

    @Autowired
    private HandlerMapping handlerMapping;


    @RequestMapping(path = "/game", method = {RequestMethod.GET, RequestMethod.POST})
    public void newGame(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if(session == null || session.isNew() || session.getAttribute(PLAYER)==null){
            String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/new_player").build().toUriString();
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", redirectUrl);
            return;
        }
        Player player = (Player) session.getAttribute(PLAYER);
        Game newGame = gameService.createGame(player);
        printWebSocketHandlers(request);
        session.setAttribute(GAME,newGame.GAME_ID);
        PrintWriter writer = response.getWriter();
        writer.write(newGame.toString());
    }




    @RequestMapping(path = "/add/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public void addPlayerToGame(@PathVariable int id, HttpSession session, HttpServletResponse response) throws IOException {
        Player player = (Player) session.getAttribute(PLAYER);
        int gameId = (int)session.getAttribute(GAME);
        gameService.removePlayerToGame(id,player);
        Game game=gameService.addPlayerToGame(id,player);
        session.setAttribute(PLAYER,player);
        session.setAttribute(GAME,game.GAME_ID);
        PrintWriter writer = response.getWriter();
        writer.write(game.toString());
    }

    @RequestMapping(path = "/remove", method = {RequestMethod.GET, RequestMethod.POST})
    public void removePlayerToGame(HttpSession session, HttpServletResponse response) throws IOException {
        Player player = (Player) session.getAttribute(PLAYER);
        int id = (int)session.getAttribute(GAME);
        Game game=gameService.removePlayerToGame(id,player);
        PrintWriter writer = response.getWriter();
        writer.write(game.toString());
    }

    @RequestMapping(path = "/gamelist", method = {RequestMethod.GET, RequestMethod.POST})
    public void getGameList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        List<GameContext> gameContexts = gameService.getGameContexts();
        gameContexts.forEach(e->writer.write(e.toString()));
    }

    @RequestMapping(path = "/gamelist", method = {RequestMethod.GET, RequestMethod.POST}, params = "fOr")
    public void getGameListFor(@RequestParam("fOr") int fOr, HttpServletResponse response) throws IOException {
        List<GameContext>   gameContexts = gameService.getGameContextsForNumPlayers(fOr);
        PrintWriter writer = response.getWriter();
        gameContexts.forEach(e->writer.write(e.toString()));
    }


    @RequestMapping(path = "/gamelist", method = {RequestMethod.GET, RequestMethod.POST}, params = "upTo")
    public void getGameListUpTo(@RequestParam("upTo") int upTo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<GameContext> gameContexts = gameService.getGameContextsUpToNumPlayers(upTo);
        PrintWriter writer = response.getWriter();
        gameContexts.forEach(e->writer.write(e.toString()));
    }

    @PostMapping(path = "/start")
    public void startGame(HttpServletRequest request, HttpServletResponse response) throws GameException, IOException {
        HttpSession session = request.getSession(true);
        Player player = (Player) session.getAttribute(PLAYER);
        int gameId = (int) session.getAttribute(GAME);
        Game game;
        Objects.requireNonNull(game=gameService.getGame(gameId),StackTraceMessage.message());
        gameService.startGame(gameId);
        PrintWriter writer = response.getWriter();
        writer.write(game.toString());
    }

    @PostMapping(path = "/stop")
    public void stopGame(HttpServletRequest request, HttpServletResponse response) throws GameException, IOException {
        HttpSession session = request.getSession(true);
        Player player = (Player) session.getAttribute(PLAYER);
        int gameId = (int) session.getAttribute(GAME);
        Game game;
        Objects.requireNonNull(game=gameService.getGame(gameId),StackTraceMessage.message());
        gameService.stopGame(gameId,player);
        PrintWriter writer = response.getWriter();
        writer.write(game.toString());
    }


    @PostMapping("/action")
    @ResponseBody
    public String doAction(@RequestBody String msg, HttpServletRequest request,HttpServletResponse response) throws IOException, GameException {
        Player player = (Player) request.getSession().getAttribute(Provisions.PLAYER);
        Properties actions = JsonToPropertiesConverter.convertJsonToProperties(msg);
        int gameId = (int) request.getSession().getAttribute(Provisions.GAME);
        Game game = gameService.getGame(gameId);
        game.doAction(player.getId(), actions);
        return "Player has played "+player.toString();
    }

    public void printWebSocketHandlers(HttpServletRequest request) throws Exception {

            System.out.println("WebSocket Handlers:");
            HandlerExecutionChain  chain= handlerMapping.getHandler(request);
            List<HandlerInterceptor> list = chain.getInterceptorList();
            list.forEach(i -> System.out.println(i.getClass().toString()));

    }
}


