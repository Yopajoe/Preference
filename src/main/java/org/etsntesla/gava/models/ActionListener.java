package org.etsntesla.gava.models;

import org.etsntesla.gava.utils.exceptions.GameException;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ActionListener<T>  {
     default void doAction(int playerId,T t) throws GameException {};
     int getId(int playerId) throws GameException;
}
