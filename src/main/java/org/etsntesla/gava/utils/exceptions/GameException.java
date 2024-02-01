package org.etsntesla.gava.utils.exceptions;

import org.etsntesla.gava.models.Game;

public class GameException extends Exception{

    Game game;
    StackTraceElement traceElement;

    public GameException(Game game, String msg){
        super(msg);
        this.game=game;
        traceElement = super.getStackTrace()[1];

    }

    public boolean alreadyStarted(){
        return traceElement.getClassName().equals("GameService") && traceElement.getMethodName().equals("startGame");
    }

}
