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
        return mysql8NoSqlFactoryConfig.getSession();
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
     * 根据sql修改
     *
     * @param queryStrs 查询sql
     * @param setMap    修改map
     * @param params    参数
     * @return
     */
    @Override
    public Long updateFromSqlStrsBySqlStrs(List<String> queryStrs, Map<String, Object> setMap, Map<String, Object> params) throws Exception {
        Session session = getSession();
        try {
            String paramsSql = "";

            for (String kb : queryStrs) {

                if (BaseUtils.isNotBlank(paramsSql)) {
                    paramsSql += " AND ";
                }

                paramsSql += kb;
            }

            ModifyStatement modifyStatement = getCollection(session).modify(paramsSql);

            for (String kb : setMap.keySet()) {
                modifyStatement.set(kb, setMap.get(kb));
            }

            for (String kb : params.keySet()) {
                modifyStatement.bind(kb, params.get(kb));
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
     * 分页查询
     *
     * @param paramsDTO 查询sql
     * @return
     * @throws Exception
     */
    @Override
    public PageVO pageQuery(Mysql8NosqlParamsDTO paramsDTO) throws Exception {
        Session session = getSession();
        String paramsSql = "";
        try {
            if (paramsDTO.getFindStrs().size() == 0 && paramsDTO.getParams().size() > 0){
                paramsSql = getFindSqlByMapParams(paramsDTO.getParams());
            }else {
                paramsSql = getFindSqlByFindStrs(paramsDTO.getFindStrs());
            }


            FindStatement findStatement = BaseUtils.isBlank(paramsSql) ? getCollection(session).find() : getCollection(session).find(paramsSql);

            if (paramsDTO.getParams().size() > 0) {
                for (Object kb : paramsDTO.getParams().keySet()) {
                    String key = kb.toString();
                    if (null != paramsDTO.getParams().get(key)) {
                        findStatement.bind(key, paramsDTO.getParams().get(key));
                    }
                }
            }

            findStatement.offset((paramsDTO.getToPage() - 1) * paramsDTO.getPageSize()).limit(paramsDTO.getPageSize());

            if (BaseUtils.isNotBlank(paramsDTO.getSortFiled())) {
                String sortStr = paramsDTO.getSortFiled() + " " + (paramsDTO.isDesc() ? "desc" : "asc");
                findStatement.sort(sortStr);
            }


            DocResult dbDocs = findStatement.execute();

            List result = coverDocToModel(dbDocs.fetchAll());
            return new PageVO(PageVO.getStartOfPage(paramsDTO.getToPage(), paramsDTO.getPageSize()), dbDocs.count(), paramsDTO.getPageSize(), result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("分页查询 " + this.name + " 失败");
        } finally {
            session.close();
        }
    }

    /**
     * 根据find 拼接sql
     * @param findStrs
     * @return
     */
    private String getFindSqlByFindStrs(List<String> findStrs) {
        String paramsSql = "";

        for (int i = 0; i < findStrs.size(); i++) {
            if (i == 0) {
                paramsSql += findStrs.get(i);
            } else {
                paramsSql += " AND " + findStrs.get(i);
            }
        }
        return paramsSql;
    }

    /**
     * 根据map key 拼接sql，默认 = 连接符
     * @param params
     * @return
     */
    private String getFindSqlByMapParams(Map<String,Object> params) {
        String paramsSql = "";

        if (params != null && params.size() > 0) {


            for (Object kb : params.keySet()) {

                String key = kb.toString();
                if (null != params.get(key)) {
                    if (BaseUtils.isNotBlank(paramsSql)) {
                        paramsSql += " AND ";
                    }
                    paramsSql += (key + "=:" + key);
                }
            }
        }
        return paramsSql;
    }

    /**
     * 根据 map kv查询多条数据
     *
     * @param findStrs sql语句
     * @param params   bind参数
     * @return
     * @throws BusinessException
     */
    @Override
    public List selectListForFindStrs(List<String> findStrs, Map<String, Object> params) throws Exception {

        Session session = getSession();
        try {
            String paramsSql = getFindSqlByFindStrs(findStrs);

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
    public long delete(List id) throws Exception {
        Session session = getSession();
        try {

            String str = "(" + BaseUtils.convertListToString(id) + ")";
            Result result = getCollection(session).remove("_id in " + str).execute();
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
    public List<T> selectListByKV(String key, Object value) throws Exception {
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
