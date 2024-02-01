package org.etsntesla.gava.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;

import java.io.Serializable;
import java.util.Properties;

@Getter
@Builder
public class Player implements Serializable {

    public enum PlayerStatus{PLAYING, WAITING}

    protected Integer id;

    protected String name;
    protected PlayerStatus status=PlayerStatus.PLAYING;


    protected Player() {}

    protected Player(Integer id, String name, PlayerStatus status)  {
        this.id = id;
        this.name = name;
        this.status = status;
    }


    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
