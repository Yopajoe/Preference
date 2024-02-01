package org.etsntesla.gava.service;

import org.etsntesla.gava.models.Player;

public interface PlayerService {

    default Player get(PlayerAssertion playerAssertion){
        return playerAssertion.isValid()? playerAssertion.getPlayer():null;
    };

    Player save(Player player);

    int delete(Player player);

    static interface PlayerAssertion{

        boolean isValid();
        Player getPlayer();

    }

}
