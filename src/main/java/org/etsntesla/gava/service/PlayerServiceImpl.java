package org.etsntesla.gava.service;


import org.etsntesla.gava.models.Player;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Objects;

@Service
public class PlayerServiceImpl implements PlayerService{


    PlayerAssertion playerAssertion = new PlayerAssertion();

    public Player get(){
        return get(playerAssertion);
    }

    @Override
    public Player get(PlayerService.PlayerAssertion playerAssertion) {
        return playerAssertion.getPlayer();
    }

    @Override
    public Player save(Player player) {
        return null;
    }

    @Override
    public int delete(Player player) {
        return 0;
    }

    public class PlayerAssertion implements PlayerService.PlayerAssertion{

        private boolean isAnonymous = false;

        @Override
        public boolean isValid() {
            return isAnonymous;
        }

        @Override
        public Player getPlayer() {
            Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "No current ServletRequestAttributes");
            Cookie[] cookies = ((HttpServletRequest) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getCookies();
            Cookie cookieGameSession = Arrays.stream(cookies)
                    .filter(cookie1 -> cookie1.getName().equals("gameSession"))
                    .findFirst().get();
            if(cookieGameSession!=null){
                HttpSession session = ((HttpServletRequest) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getSession(false);
                Object sessionGame = session.getAttribute(cookieGameSession.getValue());
                //ovde treba da se prepravi;
                isAnonymous = false;
                return Player.builder().id(1).name("Pajo").build();
            } else {
                isAnonymous = true;
                return Player.builder().id(-1).name("ANON").build();
            }


        }
    }
}
