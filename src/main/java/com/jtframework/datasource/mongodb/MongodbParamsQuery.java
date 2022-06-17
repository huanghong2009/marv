package com.jtframework.datasource.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.utils.AnnotationUtils;
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

    /**
     * join查询
     * @param joinCollectionName 需要链接的 结合名称
     * @param localField on 左边的字段
     * @param foreignField on 右边的字段
     * @param asName as 的名称
     */
    public void join(String joinCollectionName,String localField,String foreignField,String asName){
        this.mongoJoin = new MongoJoin();
        this.mongoJoin.setJoinCollectionName(joinCollectionName);
        this.mongoJoin.setAsName(asName);
        this.mongoJoin.setForeignField(foreignField);
        this.mongoJoin.setLocalField(localField);
    }

    /**
     * join查询
     * @param modelClass 需要链接的 model class
     * @param localField on 左边的字段
     * @param foreignField on 右边的字段
     * @param asName as 的名称
     */
    public void join(Class<? extends BaseModel> modelClass, String localField, String foreignField, String asName){
        this.mongoJoin = new MongoJoin();
        this.mongoJoin.setJoinCollectionName(AnnotationUtils.getServeModelValue(modelClass));
        this.mongoJoin.setAsName(asName);
        this.mongoJoin.setForeignField(foreignField);
        this.mongoJoin.setLocalField(localField);
    }


}