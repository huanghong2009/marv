
package ${package}.service.impl;

import ${package}.model.${name}Model;
import ${package}.service.dto.${name}Dto;
import ${package}.service.${name}Service;
import org.springframework.stereotype.Service;
import com.jtframework.datasource.mongodb.MongoModelDao;
import com.jtframework.datasource.mongodb.MongodbService;
import com.jtframework.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.mongodb.MongodbParamsDTO;
import org.springframework.data.mongodb.core.query.Criteria;

/**
* ${moduleName} 接口
* @description 服务实现
* @author
* @date ${date}
**/
@Slf4j
@Service
public class ${name}ServiceImpl extends MongoModelDao<${name}Model>  implements ${name}Service {

    /**
    * ${moduleName}分页查询
    * @param ${changeClassName}Dto
    * @return
    * @throws Exception
    */
    @Override
    public PageVO<${name}Model> queryPage(${name}Dto ${changeClassName}Dto) throws Exception {
        <#if hasName=='Y'>
        if (BaseUtils.isNotBlank(${changeClassName}Dto.getName())) {
            ${changeClassName}Dto.getQuery().addCriteria(Criteria.where("name").regex(MongodbParamsDTO.like(${changeClassName}Dto.getName())));
        }
        </#if>

        <#if hasType=='Y'>
        if (BaseUtils.isNotBlank(${changeClassName}Dto.getType())) {
            ${changeClassName}Dto.getQuery().addCriteria(Criteria.where("type").is(${changeClassName}Dto.getType()));
        }
        </#if>


        <#if hasState=='Y'>
        if (BaseUtils.isNotBlank(${changeClassName}Dto.getState())) {
            ${changeClassName}Dto.getQuery().addCriteria(Criteria.where("state").is(${changeClassName}Dto.getState()));
        }
        </#if>


        <#if hasStatus=='Y'>
        if (BaseUtils.isNotBlank(${changeClassName}Dto.getStatus())) {
            ${changeClassName}Dto.getQuery().addCriteria(Criteria.where("status").is(${changeClassName}Dto.getStatus()));
        }
        </#if>

        if (BaseUtils.isBlank(${changeClassName}Dto.getSortFiled())) {
            ${changeClassName}Dto.setSortFiled("createTime");
            ${changeClassName}Dto.setDesc(true);
        }


        return pageQuery(${changeClassName}Dto);
    }


}