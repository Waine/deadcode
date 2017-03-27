package com.aurea.deadcode.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by ekonovalov on 14.03.2017.
 */
@ApiModel
@Data
public class Operation {

    public static final String PULL = "pull";

    private String name;
    private String path;
    private String value;

}
