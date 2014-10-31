package com.springapp.mvc;

import java.io.Serializable;

public class TestObject implements Serializable {
    String value;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String toString(){
        return value;
    }
}
