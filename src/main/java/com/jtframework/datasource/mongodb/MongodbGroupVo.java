package com.jtframework.datasource.mongodb;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MongodbGroupVo<T> implements Serializable {

    private String _id;

    private T result;

}