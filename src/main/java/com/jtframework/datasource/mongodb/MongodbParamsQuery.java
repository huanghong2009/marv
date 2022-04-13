package com.jtframework.datasource.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * 存 CriteriaDefinition，不然取不到
 */
@Data
public  class MongodbParamsQuery extends Query {
    /**
     * join
     */

    @JsonIgnore
    public MongoJoin mongoJoin;

    private Sort sort;

    public List<CriteriaDefinition> getCriterias(){
        return super.getCriteria();
    }


    @Override
    public Query with(Sort sort) {
        this.sort = sort;
        return super.with(sort);
    }
}