package com.jtframework.datasource.mysql8_nosql;

import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.CheckParam;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.common.ModelDaoServiceImpl;
import com.jtframework.utils.BaseUtils;

import com.mysql.cj.xdevapi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class Mysql8NosqlModelDao<T extends BaseModel> extends ModelDaoServiceImpl implements Mysql8NosqlModelDaoService {

    /**
     * 注入默认数据源
     */
    @Autowired
    private Mysql8NoSqlFactoryConfig mysql8NoSqlFactoryConfig;


    /**
     * 获取session
     *
     * @return
     */
    public Session getSession() throws Exception {
        return  mysql8NoSqlFactoryConfig.getSession();
    }


    /**
     * 获取连接
     *
     * @param session
     * @return
     */
    public Collection getCollection(Session session) {
        return session.getDefaultSchema().createCollection(BaseUtils.getServeModelValue(this.cls), true);
    }


    /**
     * 新增
     *
     * @param model
     * @throws BusinessException
     */
    @Override
    public void insert(BaseModel model) throws Exception {
        Session session = getSession();
        try {
            AddResult addResult = this.getCollection(session).add(model.toJson()).execute();
            if (addResult.getGeneratedIds().size() > 0) {
                model.setId(addResult.getGeneratedIds().get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        } finally {
            session.close();
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
    public void save(BaseModel model) throws Exception {
        Session session = getSession();
        try {
            if (BaseUtils.isNotBlank(model.getId())) {
                getCollection(session).addOrReplaceOne(model.getId(), model.toJson());
            } else {
                insert(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        } finally {
            session.close();
        }
    }

    /**
     * 分页查询，默认查询前10条
     *
     * @return
     * @throws SQLException
     */
    @Override
    public PageVO<T> pageQuery(int pageNo, int pageSize) throws Exception {
        return this.pageQuery(pageNo, pageSize, "", null);
    }

    @Override
    public PageVO pageQuery(int pageNo, int pageSize, String findStr) throws Exception {
        return pageQuery(pageNo, pageSize, findStr, null);
    }

    /**
     * 分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param findStr    查询sql
     * @param bindParams 参数列表
     * @return
     * @throws Exception
     */
    @Override
    public PageVO pageQuery(int pageNo, int pageSize, String findStr, Map<String, Object> bindParams) throws Exception {
        Session session = getSession();
        try {
            FindStatement findStatement = BaseUtils.isBlank(findStr) ? getCollection(session).find() : getCollection(session).find(findStr);

            if (bindParams != null) {
                for (Object kb : bindParams.keySet()) {
                    String key = kb.toString();
                    if (null != bindParams.get(key)) {
                        findStatement.bind(key, bindParams.get(key));
                    }
                }
            }

            DocResult dbDocs = findStatement.offset((pageNo - 1) * pageSize).limit(pageSize).execute();
            List result = coverDocToModel(dbDocs.fetchAll());
            return new PageVO(PageVO.getStartOfPage(pageNo, pageSize), dbDocs.count(), pageSize, result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        } finally {
            session.close();
        }
    }

    /**
     * 根据 多个查询sql分页 ，这里 会把每一个 查询sql and 拼接起来，占位符参数，是传递过来的 参数
     *
     * @param pageNo
     * @param pageSize
     * @param findStr
     * @param bindParams
     * @return
     * @throws Exception
     */
    @Override
    public PageVO pageQuery(int pageNo, int pageSize, List<String> findStr, Map<String, Object> bindParams) throws Exception {
        if (findStr == null || findStr.size() == 0) {
            this.pageQuery(pageNo, pageSize);
        }
        String paramsSql = "";

        for (int i = 0; i < findStr.size(); i++) {
            if (i == 0) {
                paramsSql += findStr;
            } else {
                paramsSql += " AND " + findStr;
            }
        }

        return this.pageQuery(pageNo, pageSize, paramsSql, bindParams);
    }

    @Override
    public PageVO pageQuery(int pageNo, int pageSize, Map params) throws Exception {

        try {
            FindStatement findStatement = null;
            if (params != null && params.size() > 0) {
                String paramsSql = "";

                for (Object kb : params.keySet()) {

                    String key = kb.toString();
                    if (null != params.get(key)) {
                        if (BaseUtils.isNotBlank(paramsSql)) {
                            paramsSql += " AND ";
                        }
                        paramsSql += (key + "=:" + key);
                    }
                }
                return this.pageQuery(pageNo, pageSize, paramsSql, params);
            } else {
                return this.pageQuery(pageNo, pageSize, "", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("分页查询" + this.name + "失败");
        }

    }

    @Override
    @CheckParam("id")
    public T load(String id) throws Exception {
        Session session = getSession();
        try {
            return coverDocToModel(getCollection(session).getOne(id));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("获取" + this.name + "失败");
        } finally {
            session.close();
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
    public long update(BaseModel model) throws Exception {
        Session session = getSession();
        try {
            Result result = getCollection(session).addOrReplaceOne(model.getId(), model.toJson());
            return result.getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        } finally {
            session.close();
        }

    }

    @Override
    @CheckParam()
    public long delete(String id) throws Exception {
        Session session = getSession();
        try {
            Result result = getCollection(session).removeOne(id);
            return result.getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        } finally {
            session.close();
        }


    }

    @Override
    public long delete(java.util.Collection id) throws Exception {
        Session session = getSession();
        try {
            Result result = getCollection(session).remove("_id in (:ids) ").bind(id).execute();
            return result.getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        } finally {
            session.close();
        }

    }

    /**
     * doc转 model
     *
     * @param datas
     * @return
     */
    public List<T> coverDocToModel(List<DbDoc> datas) {
        List result = new ArrayList<>();

        if (datas == null) {
            return result;
        }

        for (DbDoc data : datas) {
            result.add(coverDocToModel(data));
        }

        return result;
    }


    /**
     * doc转 model
     *
     * @param data
     * @return
     */
    public T coverDocToModel(DbDoc data) {
        if (data == null || BaseUtils.isBlank(data.toString())) {
            return null;
        }

        return (T) JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), cls);
    }

    /**
     * 查询全部数据
     *
     * @return
     * @throws BusinessException
     */
    @Override
    public List<T> selectAll() throws Exception {
        Session session = getSession();
        try {
            DocResult result = getCollection(session).find().execute();
            return coverDocToModel(result.fetchAll());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        } finally {
            session.close();
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
    public List<T> selectListByKV(String key, String value) throws Exception {
        Session session = getSession();
        try {
            DocResult result = getCollection(session).find(key + "=:data").bind("data", value).execute();
            return coverDocToModel(result.fetchAll());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        } finally {
            session.close();
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
    public List selectListForMap(Map params) throws Exception {
        Session session = getSession();
        try {
            String paramsSql = "";

            for (Object kb : params.keySet()) {
                String key = kb.toString();
                if (BaseUtils.isNotBlank(paramsSql)) {
                    paramsSql += " AND ";
                }
                paramsSql += (key + "=:" + key);
            }

            FindStatement findStatement = getCollection(session).find(paramsSql);

            for (Object kb : params.keySet()) {
                String key = kb.toString();
                findStatement.bind(key, params.get(key));
            }

            DocResult result = findStatement.execute();
            return coverDocToModel(result.fetchAll());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        } finally {
            session.close();
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
    public T selectOneByKV(String key, String value) throws Exception {
        Session session = getSession();
        try {
            return coverDocToModel(getCollection(session).find(key + "=:" + key).bind(key, value).execute().fetchOne());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        } finally {
            session.close();
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
    public Object selectOneByMap(Map params) throws Exception {
        Session session = getSession();
        try {
            String paramsSql = "";

            for (Object kb : params.keySet()) {
                String key = kb.toString();
                if (BaseUtils.isNotBlank(paramsSql)) {
                    paramsSql += " AND ";
                }
                paramsSql += (key + "=:" + key);
            }

            FindStatement findStatement = getCollection(session).find(paramsSql);

            for (Object kb : params.keySet()) {
                String key = kb.toString();
                findStatement.bind(key, params.get(key));
            }

            DocResult result = findStatement.execute();
            return coverDocToModel(result.fetchOne());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        } finally {
            session.close();
        }

    }


    /**
     * 分页查询，默认查询前10条
     *
     * @return
     * @throws SQLException
     */
    @Override
    public PageVO<T> defalutPageQuery() throws Exception {
        return this.pageQuery(1, 10, "", null);
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
    public long updateKVById(String id, String key, Object value) throws Exception {
        Session session = getSession();
        try {
            Result result = this.getCollection(session).modify("_id=:id").set(key, value).bind("id", id).execute();
            return result.getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        } finally {
            session.close();
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
        Session session = getSession();
        try {
            String paramsSql = "";

            for (Object kb : whereParmas.keySet()) {
                String key = kb.toString();
                if (BaseUtils.isNotBlank(paramsSql)) {
                    paramsSql += " AND ";
                }
                paramsSql += (key + "=:" + key);
            }

            ModifyStatement modifyStatement = getCollection(session).modify(paramsSql);

            for (Object kb : updateParmas.keySet()) {
                String key = kb.toString();
                modifyStatement.set(key, updateParmas.get(key));
            }

            for (Object kb : whereParmas.keySet()) {
                String key = kb.toString();
                modifyStatement.bind(key, whereParmas.get(key));
            }

            return modifyStatement.execute().getAffectedItemsCount();

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        } finally {
            session.close();
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
        Session session = getSession();
        try {
            ModifyStatement modifyStatement = getCollection(session).modify("_id=:id");

            for (Object kb : updateParmas.keySet()) {
                String key = kb.toString();
                modifyStatement.set(key, updateParmas.get(key));
            }

            modifyStatement.bind("id", id);
            return modifyStatement.execute().getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        } finally {
            session.close();
        }

    }

}
