package com.aurea.deadcode.model;

import io.swagger.annotations.ApiModel;

/**
 * Created by ekonovalov on 10.03.2017.
 */
@ApiModel
public enum Task {

    CLONE("CLONE"),
    PULL("PULL"),
    DELETE("DELETE"),
    FILL("FILL"),
    ANALYZE("ANALYZE"),
    FIND("FIND");

    private String value;

    Task(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
