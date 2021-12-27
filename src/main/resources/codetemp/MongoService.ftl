
package ${package}.service;

import com.jtframework.datasource.mongodb.MongoModelDaoService;
import ${package}.model.${name}Model;
import ${package}.service.dto.${name}Dto;
import com.jtframework.base.query.PageVO;


/**
* @author
* @date ${date}
**/


public interface ${name}Service extends MongoModelDaoService {


       /**
        * ${moduleName}分页查询
        * @param ${changeClassName}Dto
        * @return
        * @throws Exception
        */
       PageVO<${name}Model> queryPage(${name}Dto ${changeClassName}Dto) throws Exception;

}