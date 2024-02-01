package org.etsntesla.gava.utils.stub;


import org.etsntesla.gava.models.Player;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PlayerServiceStub implements InitializingBean {

    private Map<Integer,Player> playersStubList = new HashMap<>();

    public static Map createPlayersStubList(){
        HashMap<Integer,Player> players = new HashMap();
        for (int i = 1; i <= 10; i++) {
            Player player = Player.builder()
                    .id(i)
                    .name("Player " + i)
                    .status(Player.PlayerStatus.PLAYING)
                    .build();
            players.put(i, player);
        }

        return players;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        playersStubList = PlayerServiceStub.createPlayersStubList();
    }

    public Player get(int i){
        return playersStubList.get(i);
    }

    public Player get(){
        int id = new Random().nextInt(-100,-1);
        return Player.builder()
                .id(id)
                .name("Anon"+id)
                .status(Player.PlayerStatus.PLAYING)
                .build();
    }



}
