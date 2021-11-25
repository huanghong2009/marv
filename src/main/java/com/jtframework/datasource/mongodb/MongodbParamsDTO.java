package com.jtframework.datasource.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jtframework.base.query.ParamsDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.query.Query;

import java.util.regex.Pattern;

@Data
public class MongodbParamsDTO extends ParamsDTO {

    /**
     * 查询sql
     */
    @JsonIgnore
    @ApiModelProperty(hidden=true)
    private Query query;

    private static final String patternLikeStr = "^.*%s.*$";


    private static final String patternLeftLikeStr = "^%s.*$";


    private static final String patternRightLikeStr = "^.*%s$";

    public MongodbParamsDTO(){
        super();
        this.query = new Query();

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


}
