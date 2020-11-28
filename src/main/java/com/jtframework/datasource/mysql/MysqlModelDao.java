package com.jtframework.datasource.mysql;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.base.service.BaseServiceImpl;
import com.jtframework.utils.BaseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public  class MysqlModelDao<T> extends BaseServiceImpl implements MysqlModelDaoService,Serializable {

    private String name;

    private Class cls;

    public MysqlModelDao() {
        Class cls = getTClass();
        this.cls = cls;
        this.name = BaseUtils.getServeModelDesc(cls);
    }

    public  MysqlService getMysqlService(){
        return new MysqlService();
    }

    public Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    public void init(String... args) throws BusinessException {
    }


    public void insert(Object model) throws BusinessException {
        try {
            getMysqlService().insert(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    public T load(String id) throws BusinessException {
        try {
            return (T) getMysqlService().load(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("获取" + this.name + "失败");
        }
    }

    public int delete(String id) throws BusinessException {
        try {
            return getMysqlService().delete(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    public int update(Object model) throws BusinessException {
        try {
            return getMysqlService().update(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }

    /**
     * 查询全部数据
     *
     * @return
     * @throws BusinessException
     */
    public List<T> selectAll() throws BusinessException {
        try {
            return getMysqlService().selectListAll(this.cls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        }
    }

    /**
     * 根据kv查询多条数据
     *
     * @param key
     * @param value
     * @return
     * @throws BusinessException
     */
    public List<T> selectListByKV(String key, String value) throws BusinessException {
        try {
            return getMysqlService().selectListFromKV(this.cls, key, value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        }
    }

    /**
     * 根据 map kv查询多条数据
     *
     * @param params
     * @return
     * @throws BusinessException
     */
    @Override
    public List selectListForMap(Map params) throws BusinessException {
        try {
            return getMysqlService().selectListFromMap(this.cls, params);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        }
    }

    /**
     * 根据kv查询单条数据
     *
     * @param key
     * @param value
     * @return
     * @throws BusinessException
     */
    public T selectOneByKV(String key, String value) throws BusinessException {
        try {
            return (T) getMysqlService().selectOneFromKV(this.cls, key, value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        }
    }

    /**
     * 根据kv查询单条数据
     *
     * @param params
     * @return
     * @throws BusinessException
     */
    @Override
    public Object selectOneByMap(Map params) throws BusinessException {
        try {
            return (T) getMysqlService().selectOneFromMap(this.cls, params);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        }
    }



    /**
     * 分页查询，默认查询前10条
     *
     * @return
     * @throws SQLException
     */
    public PageVO<T> defalutPageQuery() throws SQLException {
        try {
            return getMysqlService().pageQuery(this.cls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("分页查询" + this.name + "失败");
        }
    }


}
