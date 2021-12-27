
package ${package}.service.impl;

import ${package}.model.${name}Model;
import ${package}.service.dto.${name}Dto;
import ${package}.service.${name}Service;
import org.springframework.stereotype.Service;
import com.jtframework.datasource.mysql.MysqlModelDao;
import com.jtframework.datasource.mysql.MysqlService;
import lombok.extern.slf4j.Slf4j;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.mysql.MysqlQueryParams;
import com.jtframework.utils.BaseUtils;


/**
* ${moduleName} 接口
* @description 服务实现
* @author
* @date ${date}
**/
@Service
@Slf4j
public class ${name}ServiceImpl extends MysqlModelDao<${name}Model>  implements ${name}Service {


    /**
     * 分页查询
     *
     * @param dto
     * @return
     */
    @Override
    PageVO<${name}Model> queryPage(${name}Dto dto)throws Exception {
        try {
            MysqlQueryParams mysqlQueryParams = new MysqlQueryParams(${name}Model.class);

            if (BaseUtils.isNotBlank(dto.getOrderFiled())) {
                mysqlQueryParams.sort(dto.getOrderFiled(), dto.isDesc() ? MysqlQueryParams.MysqlSort.DESC : MysqlQueryParams.MysqlSort.ASC);
            }

            mysqlQueryParams.limit(dto.getToPage(), mysqlQueryParams.getPageSize());

            return this.getMysqlService().pageQuery(${name}Model.class, mysqlQueryParams);

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new Exception("分页查询失败");
        }
    }
}