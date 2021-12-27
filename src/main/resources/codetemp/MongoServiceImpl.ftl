
package ${package}.service.impl;

import ${package}.model.${name}Model;
import ${package}.service.dto.${name}Dto;
import ${package}.service.${name}Service;
import org.springframework.stereotype.Service;
import com.jtframework.datasource.mongodb.MongoModelDao;
import com.jtframework.datasource.mongodb.MongodbService;
import com.jtframework.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;

/**
* ${moduleName} 接口
* @description 服务实现
* @author
* @date ${date}
**/
@Slf4j
@Service
public class ${name}MongoServiceImpl extends MongoModelDao<${name}Model>  implements ${name}Service {

        /**
        * ${moduleName}分页查询
        * @param ${changeClassName}DTO
        * @return
        * @throws Exception
        */
        @Override
         PageVO<${name}Model> queryPage(${name}DTO ${changeClassName}DTO) throws Exception {



            if (BaseUtils.isBlank(${changeClassName}DTO.getSortFiled())) {
                ${changeClassName}DTO.setSortFiled("createTime");
                ${changeClassName}DTO.setDesc(true);
            }


            return pageQuery(empDTO);
        }


}