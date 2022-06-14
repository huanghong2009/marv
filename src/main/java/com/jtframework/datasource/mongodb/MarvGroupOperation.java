package com.jtframework.datasource.mongodb;

import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;

public class MarvGroupOperation  extends GroupOperation {
    /**
     * Creates a new {@link GroupOperation} including the given {@link Fields}.
     *
     * @param fields must not be {@literal null}.
     */
    public MarvGroupOperation(Fields fields) {
        super(fields);
    }
}
