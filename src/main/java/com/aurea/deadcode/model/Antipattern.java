package com.aurea.deadcode.model;

/**
 * Created by Waine on 27.03.2017.
 */
public enum Antipattern {

    DEAD_CODE("Dead code");

    private String value;

    Antipattern(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
