package com.aurea.deadcode.model;

import io.swagger.annotations.ApiModel;

/**
 * Created by ekonovalov on 08.03.2017.
 */
@ApiModel
public enum Status {

    NEW("NEW"),
    PROCESSING("PROCESSING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private String value;

    Status(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
