package com.jtframework.datasource.mysql8_nosql;

import com.alibaba.fastjson.JSONArray;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class Mysql8NosqlModelDao<T extends BaseModel> extends ModelDaoServiceImpl implements Mysql8NosqlModelDaoService {

    /**
     * 注入默认数据源
     */
    @Autowired
    private Schema schema;


    /**
     * 获取数据源
     *
     * @return
     */
    public Schema getSchema() throws Exception {

        if (schema == null) {
            throw new Exception("未注入mysql xdevapi 数据源 且 未能成功初始化 默认数据源，请检查配置 ....");
        }
        return this.schema;
    }

    /**
     * 获取集合
     *
     * @param collectioNname
     * @return
     * @throws Exception
     */
    private Collection getCollection(String collectioNname) throws Exception {
        Collection myColl = getSchema().createCollection(collectioNname, true);
        return myColl;
    }


    /**
     * 获取集合
     *
     * @param
     * @return
     * @throws Exception
     */
    public Collection getCollection() throws Exception {
        Collection myColl = getSchema().createCollection(BaseUtils.getServeModelValue(cls), true);
        return myColl;
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
            AddResult addResult = getCollection().add(model.toJson()).execute();
            if (addResult.getGeneratedIds().size() > 0) {
                model.setId(addResult.getGeneratedIds().get(0));
            }
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
    public void save(BaseModel model) throws BusinessException {
        try {
            if (BaseUtils.isNotBlank(model.getId())) {
                getCollection().addOrReplaceOne(model.getId(), model.toJson());
            } else {
                insert(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
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

        FindStatement findStatement = BaseUtils.isBlank(findStr) ? getCollection().find() : getCollection().find(findStr);

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

        return this.pageQuery(pageNo,pageSize,paramsSql,bindParams);
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
    public T load(String id) throws BusinessException {
        try {
            return coverDocToModel(getCollection().getOne(id));
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
        JSONObject data = JSONObject.parseObject(model.toJson());

        try {
            ModifyStatement modifyStatement = getCollection().modify("_id=:id");
            for (String key : data.keySet()) {
                modifyStatement.set("key", data.get(key));
            }
            modifyStatement.bind("id", model.getId());
            Result result = modifyStatement.execute();
            return result.getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    @Override
    @CheckParam()
    public long delete(String id) throws BusinessException {
        try {
            Result result = getCollection().removeOne(id);
            return result.getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    @Override
    public long delete(java.util.Collection id) throws BusinessException {
        try {
            Result result = getCollection().remove("_id in (:ids) ").bind(id).execute();
            return result.getAffectedItemsCount();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    /**
     * doc转 model
     *
     * @param datas
     * @return
     */
    private List<T> coverDocToModel(List<DbDoc> datas) {
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(datas));
        List result = new ArrayList<>();
        if (datas==null){
            return result;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            result.add(JSONObject.toJavaObject(jsonArray.getJSONObject(i), cls));
        }
        return result;
    }


    /**
     * doc转 model
     *
     * @param data
     * @return
     */
    private T coverDocToModel(DbDoc data) {
        if (data == null || BaseUtils.isBlank(data.toString())){
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
    public List<T> selectAll() throws BusinessException {
        try {
            DocResult result = getCollection().find().execute();
            return coverDocToModel(result.fetchAll());
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
    public List<T> selectListByKV(String key, String value) throws BusinessException {
        try {
            DocResult result = getCollection().find(key + "=:data").bind("data", value).execute();
            return coverDocToModel(result.fetchAll());
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
            String paramsSql = "";

            for (Object kb : params.keySet()) {
                String key = kb.toString();
                if (BaseUtils.isNotBlank(paramsSql)) {
                    paramsSql += " AND ";
                }
                paramsSql += (key + "=:" + key);
            }

            FindStatement findStatement = getCollection().find(paramsSql);

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
            return coverDocToModel(getCollection().find(key + "=:" + key).bind(key, value).execute().fetchOne());
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
            String paramsSql = "";

            for (Object kb : params.keySet()) {
                String key = kb.toString();
                if (BaseUtils.isNotBlank(paramsSql)) {
                    paramsSql += " AND ";
                }
                paramsSql += (key + "=:" + key);
            }

            FindStatement findStatement = getCollection().find(paramsSql);

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
    public long updateKVById(String id, String key, Object value) throws SQLException {
        try {
            Result result = this.getCollection().modify("_id=:id").set(key, value).bind("id", id).execute();
            return result.getAffectedItemsCount();
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
            String paramsSql = "";

            for (Object kb : whereParmas.keySet()) {
                String key = kb.toString();
                if (BaseUtils.isNotBlank(paramsSql)) {
                    paramsSql += " AND ";
                }
                paramsSql += (key + "=:" + key);
            }

            ModifyStatement modifyStatement = getCollection().modify(paramsSql);

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
            ModifyStatement modifyStatement = getCollection().modify("_id=:id");

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
        }
    }

}
