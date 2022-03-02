package com.jtframework.datasource.mysql;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.CheckParam;
import com.jtframework.datasource.common.ModelDaoServiceImpl;
import com.jtframework.utils.BaseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public long delete(String id) throws BusinessException {
        try {
            return getDao().delete(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    @Override
    public long delete(Set id) throws Exception {
        try {
            return getDao().delete(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    @Override
    public long delete(List id) throws BusinessException {
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
    public long update(BaseModel model) throws BusinessException {
        try {
            return getDao().update(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }

    /**
     * 根据id批量查询
     *
     * @param ids
     */
    @Override
    public List load(Set ids) throws Exception {
        try {
            return getDao().load(cls,ids);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("批量查询" + this.name + "失败");
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
    public List<T> selectListByKV(String key, Object value) throws BusinessException {
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
     * 根据id 修改一个key value
     *
     * @param id
     * @param key
     * @param value
     * @throws SQLException
     */
    @Override
    public long updateKVById(String id, String key, Object value) throws SQLException {

        try {
            return getDao().updateKVById(this.cls,id,key,value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }

    /**
     * 根据map 修改一个mao
     *
     * @param whereParmas
     * @param updateParmas
     * @throws SQLException
     */
    @Override
    public long updateMapByMap(Map whereParmas, Map updateParmas) throws Exception {
        try {
            return getDao().update(this.cls,whereParmas,updateParmas);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }

    /**
     * 根据id 修改一个key value
     *
     * @param id
     * @param updateParmas
     * @throws SQLException
     */
    @Override
    public long updateMapById(String id, Map updateParmas) throws Exception {
        try {
            Map<String,Object> whereParmas = new HashMap<>();
            whereParmas.put("id",id);
            return getDao().update(this.cls,whereParmas,updateParmas);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }


}
