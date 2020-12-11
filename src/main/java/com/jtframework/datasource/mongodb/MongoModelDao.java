package com.jtframework.datasource.mongodb;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.common.ModelDaoServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Slf4j
public abstract class MongoModelDao<T> extends ModelDaoServiceImpl {

    public abstract MongodbService getMongoService();


    /**
     * 新增
     *
     * @param model
     * @throws BusinessException
     */
    public void insert(Object model) throws BusinessException {
        try {
            getMongoService().insert(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    /**
     * 保存，有id 修改，无id 插入
     *
     * @param model
     * @throws BusinessException
     */
    public void save(Object model) throws BusinessException {
        try {
            getMongoService().save(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    public T load(String id) throws BusinessException {
        try {
            return (T) getMongoService().findById(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("获取" + this.name + "失败");
        }
    }


    public int delete(String id) throws BusinessException {
        try {
            return (int) getMongoService().removeById(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
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
            return getMongoService().findAll(cls);
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
            return getMongoService().findByKV(this.cls, key, value);
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
            return getMongoService().findByMap(this.cls, params);
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
            return (T) getMongoService().findOneByKV(this.cls, key, value);
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
            return (T) getMongoService().findOneByMap(this.cls, params);
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
            return getMongoService().pageQuery(this.cls,getMongoService().createQuery(),1,10);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("分页查询" + this.name + "失败");
        }
    }

}