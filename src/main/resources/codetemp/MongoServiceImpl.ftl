
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



            if (BaseUtils.isBlank(${changeClassName}Dto.getSortFiled())) {
                ${changeClassName}Dto.setSortFiled("createTime");
                ${changeClassName}Dto.setDesc(true);
            }


            return pageQuery(${changeClassName}Dto);
        }


}