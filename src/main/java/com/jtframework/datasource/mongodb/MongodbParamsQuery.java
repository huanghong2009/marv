package com.jtframework.datasource.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @ApiModelProperty(hidden=true)
    @JsonIgnore
    @Schema(hidden = true)
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