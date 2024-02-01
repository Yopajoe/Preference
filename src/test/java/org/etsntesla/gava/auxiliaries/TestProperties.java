package org.etsntesla.gava.auxiliaries;

import java.io.IOException;
import java.util.Properties;

public class TestProperties extends Properties {

    public TestProperties(String filename){
        super();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            this.load(classLoader.getResourceAsStream(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TestProperties(){
        this("application.properties");
    }

}
