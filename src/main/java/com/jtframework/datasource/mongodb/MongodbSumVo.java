package com.jtframework.datasource.mongodb;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MongodbSumVo implements Serializable {

    private String _id;

    private BigDecimal amount;

}