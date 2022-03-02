package com.jtframework.datasource.mongodb;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.CheckParam;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.common.ModelDaoServiceImpl;
import com.jtframework.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
public class MongoModelDao<T extends BaseModel> extends ModelDaoServiceImpl implements MongoModelDaoService {

    /**
     * 注入默认数据源
     */
    @Autowired
    MongoServiceInit mongoServiceInit;

    private String collectionName;


    public MongoModelDao(){
        this.collectionName = BaseUtils.getServeModelValue(cls);
    }

    public MongodbService getMongoService() {
        return null;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public MongodbService getDao() throws Exception {
        MongodbService mongodbService = getMongoService();
        if (mongodbService != null) {
            return mongodbService;
        }

        mongodbService = mongoServiceInit.getMongodbService();

        if (mongodbService != null) {
            return mongodbService;
        } else {
            throw new Exception("未注入数据源 且 未能成功初始化 默认数据源，请检查配置 ....");
        }
    }


    /**
     * 新增
     *
     * @param model
     * @throws BusinessException
     */
    @Override
    public void insert(BaseModel model) throws BusinessException {
        try {
            getDao().insert(model);
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
    @Override
    @CheckParam(checkType = CheckParam.Type.ONLY, value = "model.id")
    public void save(Object model) throws BusinessException {
        try {
            getDao().save(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    /**
     * 分页查询
     *
     * @param mongodbParamsDTO
     * @throws BusinessException
     */
    @Override
    public PageVO<T> pageQuery(MongodbParamsDTO mongodbParamsDTO) throws BusinessException {
        try {
            if (BaseUtils.isNotBlank(mongodbParamsDTO.getSortFiled())) {
                if (mongodbParamsDTO.isDesc()) {
                    mongodbParamsDTO.getQuery().with(Sort.by(Sort.Order.desc(mongodbParamsDTO.getSortFiled())));
                } else {
                    mongodbParamsDTO.getQuery().with(Sort.by(Sort.Order.asc(mongodbParamsDTO.getSortFiled())));
                }
            }

            return this.getDao().pageQuery(this.cls, mongodbParamsDTO.getQuery(), mongodbParamsDTO.getToPage(), mongodbParamsDTO.getPageSize());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("分页查询" + this.name + " 失败");
        }
    }



    /**
     * 根据query 查询
     *
     * @param query
     * @return
     * @throws BusinessException
     */
    @Override
    public List findByQuery(Query query) throws BusinessException {
        try {
            return this.getDao().findByQuery(this.cls,query);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询" + this.name + " 失败");
        }
    }

    /**
     * 根据dto 查询
     *
     * @param mongodbParamsDTO
     * @return
     * @throws BusinessException
     */
    @Override
    public List<T> findByDto(MongodbParamsDTO mongodbParamsDTO) throws BusinessException {
        if (BaseUtils.isNotBlank(mongodbParamsDTO.getSortFiled())) {
            if (mongodbParamsDTO.isDesc()) {
                mongodbParamsDTO.getQuery().with(Sort.by(Sort.Order.desc(mongodbParamsDTO.getSortFiled())));
            } else {
                mongodbParamsDTO.getQuery().with(Sort.by(Sort.Order.asc(mongodbParamsDTO.getSortFiled())));
            }
        }
        return findByQuery(mongodbParamsDTO.getQuery());
    }

    @Override
    @CheckParam("id")
    public T load(String id) throws BusinessException {
        try {
            return (T) getDao().findById(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("获取" + this.name + "失败");
        }
    }


    /**
     * 覆盖性修改
     *
     * @param model
     * @throws BusinessException
     */
    @Override
    @CheckParam(checkType = CheckParam.Type.ONLY, value = "model.id")
    public long update(BaseModel model) throws BusinessException {

        try {
            getDao().save(model);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    /**
     * 根据id批量查询
     *
     * @param ids
     */
    @Override
    public List load(Set ids) throws BusinessException {
        try {
            return getDao().findById(cls,ids);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("批量查询" + this.name + "失败");
        }
    }

    @Override
    @CheckParam
    public long delete(String id) throws BusinessException {
        try {
            return (int) getDao().removeById(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    @Override
    public long delete(Set id) throws BusinessException {
        try {
            return (int) getDao().removeByIds(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    @Override
    public long delete(List ids) throws BusinessException {
        try {
            return (int) getDao().removeByIds(cls, ids);
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
    @Override
    public List<T> selectAll() throws BusinessException {
        try {
            return getDao().findAll(cls);
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
    public List<T> selectListByKV(String key, Object value) throws BusinessException {
        try {
            return getDao().findByKV(this.cls, key, value);
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
            return getDao().findByMap(this.cls, params);
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
    public T selectOneByKV(String key, String value) throws BusinessException {
        try {
            return (T) getDao().findOneByKV(this.cls, key, value);
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
            return (T) getDao().findOneByMap(this.cls, params);
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
    public long updateKVById(String id, String key, Object value) throws BusinessException {
        try {
            Update update = new Update();
            update.set(key, value);
            return (int) getDao().updateById(id, update, this.cls);
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
    public long updateMapByMap(Map whereParmas, Map updateParmas) throws BusinessException {
        try {
            Update update = new Update();
            for (Object pk : updateParmas.keySet()) {
                String key = pk.toString();
                update.set(key, updateParmas.get(key));
            }


            Query query = new Query();
            for (Object pk : whereParmas.keySet()) {
                String key = pk.toString();
                query.addCriteria((new Criteria(key)).is(whereParmas.get(key)));
            }

            return (int) getDao().updateMulti(query, update, this.cls);
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
    public long updateMapById(String id, Map updateParmas) throws BusinessException {
        try {
            Update update = new Update();
            for (Object pk : updateParmas.keySet()) {
                String key = pk.toString();
                update.set(key, updateParmas.get(key));
            }

            Query query = new Query();
            query.addCriteria((new Criteria("_id")).is(id));

            return (int) getDao().updateFirst(query, update, this.cls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }

}
