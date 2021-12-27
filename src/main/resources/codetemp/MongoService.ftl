
package ${package}.service;

import com.jtframework.datasource.mongodb.MongoModelDaoService;
import ${package}.model.${name}Model;
import ${package}.service.dto.${name}Dto;



/**
* @author
* @date ${date}
**/


public interface ${name}MongoService extends MongoModelDaoService {


       /**
        * ${moduleName}分页查询
        * @param ${changeClassName}DTO
        * @return
        * @throws Exception
        */
       PageVO<${name}Model> queryPage(${name}DTO ${changeClassName}DTO) throws Exception;

}