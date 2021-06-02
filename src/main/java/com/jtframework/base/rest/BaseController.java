package com.jtframework.base.rest;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.CheckParam;
import com.jtframework.base.query.ParamsDTO;
import com.jtframework.datasource.common.ModelDaoService;
import com.jtframework.utils.BaseUtils;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.ParameterizedType;

/**
 * 公共接口父类
 */
@Data
@Slf4j
public class BaseController<T extends BaseModel> {
    public String name;

    public Class cls;

    public BaseController(){
        Class cls = getTClass();
        this.cls = cls;
        this.name = BaseUtils.getServeModelDesc(cls);
    }

    /**
     * 想要实现公共方法 需要重写此方法
     * @return
     */
    public ModelDaoService getModelDaoService() throws BusinessException {
        throw new BusinessException("未注入实现类，需要重写 getModelDaoService 方法...");
    }

    public Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }


    @ApiOperation("加载对象")
    @PostMapping(value = "/load")
    @CheckParam
    public ServerResponse load(String id){
        try {
            return ServerResponse.succeed("加载对象:"+name+"成功", getModelDaoService().load(id));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ServerResponse.error(e.getMessage(),"加载对象"+name+"失败...");
        }
    }

    @ApiOperation("删除对象")
    @PostMapping(value = "/delete")
    public ServerResponse delete(String id){
        try {
            getModelDaoService().delete(id);
            return ServerResponse.succeed("删除:"+name+"成功",null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ServerResponse.error(e.getMessage(),"删除"+name+"失败...");
        }
    }
}
