
package ${package}.service;

import com.jtframework.datasource.common.ModelDaoService;

import com.jtframework.base.query.PageVO;
import ${package}.model.${name}Model;
import ${package}.service.dto.${name}Dto;
/**
* ${moduleName} 接口
* @author
* @date ${date}
**/

public interface ${name}Service extends ModelDaoService {

   /**
   * ${moduleName}分页查询
   * @param ${changeClassName}Dto
   * @return
   * @throws Exception
   */
  PageVO<${name}Model> queryPage(${name}Dto ${changeClassName}Dto) throws Exception;

}