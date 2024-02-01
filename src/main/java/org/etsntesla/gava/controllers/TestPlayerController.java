package org.etsntesla.gava.controllers;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.etsntesla.gava.models.Game;
import org.etsntesla.gava.models.Player;
import org.etsntesla.gava.service.GameService;
import org.etsntesla.gava.utils.JsonToPropertiesConverter;
import org.etsntesla.gava.utils.Provisions;
import org.etsntesla.gava.utils.exceptions.GameException;
import org.etsntesla.gava.utils.stub.PlayerServiceStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

@Controller
public class TestPlayerController {

    @Autowired
    PlayerServiceStub playerServiceStub;

    @Autowired
    GameService gameService;



    @GetMapping("/info_player")
    public void infoPlayer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        PrintWriter writer = response.getWriter();
        response.setContentType("text/plain");
        if(session.isNew()){
            Player player = playerServiceStub.get();
            session.setAttribute(Provisions.PLAYER,player);
            writer.write(player.toString());
            return;
        }
        Player player = (Player) session.getAttribute("PLAYER");
        writer.write(player.toString());
    }

    @RequestMapping(path = "/new_player", method = {RequestMethod.GET, RequestMethod.POST})
    public void newPlayer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        Player player = playerServiceStub.get();
        session.setAttribute("PLAYER",player);
        session.setAttribute("GAME",-1);
        response.setContentType("text/plain");
        response.getWriter().write(player.toString());
    }


}
