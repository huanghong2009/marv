package ${package}.rest;


import ${package}.model.${name}Model;
import ${package}.service.dto.${name}Dto;
import ${package}.service.${name}Service;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.rest.BaseController;
import com.jtframework.datasource.common.ModelDaoService;

import com.jtframework.base.rest.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.ArrayList;
/**
* ${moduleName}  接口
* @author
* @date ${date}
**/
@Slf4j
@RestController
@Api(tags = "${moduleName}管理")
@RequestMapping("/api/${changeClassName}")
public class ${name}Controller extends BaseController<${name}Model>{

    @Autowired
    private  ${name}Service ${changeClassName}Service;

    @Override
    public ModelDaoService getModelDaoService() throws BusinessException {
        return ${changeClassName}Service;
    }


     /**
     * 查询${moduleName}列表
     *
     * @param ${changeClassName}Dto
     * @return
     */
    @ApiOperation(value = "查询${moduleName}列表")
    @GetMapping("/query_page")
    public ServerResponse queryPage(${name}Dto ${changeClassName}Dto) {
        try {
            return ServerResponse.succeed("查询${moduleName}列表成功", ${changeClassName}Service.queryPage(${changeClassName}Dto) );
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("查询${moduleName}列表失败", e.getMessage());
        }
    }

    /**
     * 添加${moduleName}
     *
     * @param ${changeClassName}Model
     * @return
     */
    @ApiOperation(value = "添加${moduleName}")
    @PostMapping("/insert")
    public ServerResponse insert(${name}Model ${changeClassName}Model) {
        try {
            ${changeClassName}Service.insert(${changeClassName}Model);
            return ServerResponse.succeed("添加${moduleName}成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("添加${moduleName}失败", e.getMessage());
        }
    }


  /**
     * 修改${moduleName}信息
     *
     * @param ${changeClassName}Model
     * @return
     */
    @ApiOperation(value = "修改${moduleName}信息")
    @PostMapping("/update")
    public ServerResponse update(${name}Model ${changeClassName}Model) {
        try {
            ${changeClassName}Service.update(${changeClassName}Model);
            return ServerResponse.succeed("修改${moduleName}信息成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("修改${moduleName}信息失败", e.getMessage());
        }
    }


     /**
     * 删除
     *
     * @param ids
     * @return
     */
    @ApiOperation("批量删除")
    @PostMapping("/deleteBatch")
    public ServerResponse deleteBatch(@RequestBody Set<String> ids) {
        try {
            ${changeClassName}Service.delete(new ArrayList<>(ids));
            return ServerResponse.succeed("删除成功", "");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("删除失败", e.getMessage());
        }
    }

}