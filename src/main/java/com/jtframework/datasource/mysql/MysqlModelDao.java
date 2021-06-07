package com.jtframework.datasource.mysql;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.CheckParam;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.common.ModelDaoServiceImpl;
import com.jtframework.utils.BaseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class MysqlModelDao<T> extends ModelDaoServiceImpl {

    /**
     * 注入默认数据源
     */
    @Autowired
    MysqlServiceInit mysqlServiceInit;

    /**
     * 重写此方法即调用此方法数据源，否认采用默认数据源
     *
     * @return
     */
    public MysqlService getMysqlService() {
        return null;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public MysqlService getDao() throws Exception {
        MysqlService mysqlService = getMysqlService();
        if (mysqlService != null) {
            return mysqlService;
        }
        mysqlService = mysqlServiceInit.getMysqlService();

        if (mysqlService != null) {
            return mysqlService;
        } else {
            throw new Exception("未注入数据源 且 未能成功初始化 默认数据源，请检查配置 ....");
        }
    }

    @Override
    public void insert(BaseModel model) throws BusinessException {

        if (BaseUtils.isBlank(model.getId())){
            model.setId(null);
        }

        try {
            getDao().insert(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    @Override
    @CheckParam
    public T load(String id) throws BusinessException {
        try {
            return (T) getDao().load(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("获取" + this.name + "失败");
        }
    }

    @Override
    @CheckParam
    public int delete(String id) throws BusinessException {
        try {
            return getDao().delete(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    @Override
    @CheckParam(checkType = CheckParam.Type.ONLY,value = "model.id")
    public int update(BaseModel model) throws BusinessException {
        try {
            return getDao().update(model);
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
    @Override
    public List<T> selectAll() throws BusinessException {
        try {
            return getDao().selectListAll(this.cls);
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
    @Override
    @CheckParam
    public List<T> selectListByKV(String key, String value) throws BusinessException {
        try {
            return getDao().selectListFromKV(this.cls, key, value);
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
            return getDao().selectListFromMap(this.cls, params);
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
    @Override
    @CheckParam
    public T selectOneByKV(String key, String value) throws BusinessException {
        try {
            return (T) getDao().selectOneFromKV(this.cls, key, value);
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
            return (T) getDao().selectOneFromMap(this.cls, params);
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
    @Override
    public PageVO<T> defalutPageQuery() throws SQLException {
        try {
            return getDao().pageQuery(this.cls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("分页查询" + this.name + "失败");
        }
    }

    /**
     * 根据id 修改一个key value
     *
     * @param id
     * @param key
     * @param value
     * @throws SQLException
     */
    @Override
    public int updateKVById(String id, String key, Object value) throws SQLException {

        try {
            return getDao().updateKVById(this.cls,id,key,value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }


}
