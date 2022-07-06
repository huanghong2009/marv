package com.jtframework.datasource.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.query.ParamsDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Data
public class MongodbParamsDTO extends ParamsDTO {




    @JsonIgnore
    public MongodbParamsQuery query;

    private static final String patternLikeStr = "^.*%s.*$";


    private static final String patternLeftLikeStr = "^%s.*$";


    private static final String patternRightLikeStr = "^.*%s$";

    public MongodbParamsDTO(){
        super();
        this.query = new MongodbParamsQuery();

    }



    /**
     * 模糊查询
     * @param search
     * @return
     */
    public static Pattern like(String search){
        return Pattern.compile(String.format(patternLikeStr,search), Pattern.CASE_INSENSITIVE);
    }
    /**
     * 左模糊查询
     * @param search
     * @return
     */
    public static Pattern leftLike(String search){
        return Pattern.compile(String.format(patternLeftLikeStr,search), Pattern.CASE_INSENSITIVE);
    }

    /**
     * 左模糊查询
     * @param search
     * @return
     */
    public static Pattern rightLike(String search){
        return Pattern.compile(String.format(patternRightLikeStr,search), Pattern.CASE_INSENSITIVE);
    }


    /**
     * join查询
     * @param joinCollectionName 需要链接的 结合名称
     * @param localField on 左边的字段
     * @param foreignField on 右边的字段
     * @param asName as 的名称
     */
    public void join(String joinCollectionName,String localField,String foreignField,String asName){
        this.query.join(joinCollectionName,localField,foreignField,asName);
    }

    /**
     * join查询
     * @param modelClass 需要链接的 model class
     * @param localField on 左边的字段
     * @param foreignField on 右边的字段
     * @param asName as 的名称
     */
    public void join(Class<? extends BaseModel> modelClass, String localField, String foreignField, String asName){
       this.query.join(modelClass,localField,foreignField,asName);
    }



}
