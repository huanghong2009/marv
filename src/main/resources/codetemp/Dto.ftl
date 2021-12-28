package ${package}.service.dto;

import lombok.Data;
import com.jtframework.datasource.mongodb.MongodbParamsDTO;
import com.jtframework.base.query.ParamsDTO;
import io.swagger.annotations.ApiModelProperty;

/**
* @description
* @author
* @date ${date}
**/
@Data
public class ${name}Dto extends ${dtoType} {

    <#if hasName =='Y'>
    @ApiModelProperty(value = "${moduleName}名称")
    private String name;
    </#if>

    <#if hasType =='Y'>
    @ApiModelProperty(value = "${moduleName}类型")
    private String type;
    </#if>


    <#if hasState=='Y'>
    @ApiModelProperty(value = "${moduleName}状态")
    private String state;
    </#if>


    <#if hasStatus=='Y'>
    @ApiModelProperty(value = "${moduleName}状态")
    private String status;
    </#if>
}