package com.jtframework.datasource.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.dao.ServerModel;
import com.jtframework.base.query.PageVO;
import com.jtframework.utils.BaseUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.internal.connection.ServerAddressHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * mongodb curd 工具类
 *
 * @author ttan
 */
@Slf4j
public class MongodbService {

    public MongoTemplate mongoTemplate;

    public static String getCollectionName(Class<?> resultClass) {
        String serverName = resultClass.getClass().getSimpleName();
        ServerModel serverModel = (ServerModel) resultClass.getAnnotation(ServerModel.class);
        if (serverModel != null) {
            serverName = serverModel.value();
        }

        return serverName;
    }

    public void initMongodbService(String host, Integer port, String database) throws Exception{
        List<MongoCredential> credentialsList = new ArrayList<>();
        mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(ServerAddressHelper.createServerAddress(host, port)), database));
        log.info("{}:{}:{} 初始化中 ......", host, port, database);
    }

    public void initMongodbService(String host, Integer port, String database, MongoAuthClient mongoAuthClient) throws Exception{
        List<MongoCredential> credentialsList = new ArrayList<>();
        credentialsList.add(MongoCredential.createCredential(mongoAuthClient.getUserName(), database, mongoAuthClient.getPassWord().toCharArray()));
        mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(ServerAddressHelper.createServerAddress(host, port), credentialsList), database));
        log.info("{}:{}:{}--{} 初始化中 ......", host, port, database, mongoAuthClient.getUserName());
    }

    public <T> PageVO<T> pageQuery(Class<T> resultClass, Query query, int pageNo, int pageSize) {
        int total = (int) this.mongoTemplate.count(query, getCollectionName(resultClass));
        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = total;
            }

            query.limit(pageSize).skip((pageNo - 1) * pageSize);
            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            List<T> result = this.mongoTemplate.find(query, resultClass, getCollectionName(resultClass));
            return new PageVO(startIndex, total, pageSize, result);
        }
    }

    public void dropCollection(Class<?> resultClass) {
        this.mongoTemplate.dropCollection(getCollectionName(resultClass));
    }

    public <T> List<T> find(Class<T> resultClass, Query query) {
        return this.mongoTemplate.find(query, resultClass, getCollectionName(resultClass));
    }

    public <T> T findOne(Class<T> resultClass, Query query) {
        return this.mongoTemplate.findOne(query, resultClass, getCollectionName(resultClass));
    }

    public <T> T findById(Class<T> resultClass, String id) {
        return this.mongoTemplate.findById(id, resultClass, getCollectionName(resultClass));
    }

    public void insert(Object model) {
        this.mongoTemplate.insert(model, getCollectionName(model.getClass()));
    }

    public void insert(Object model, String collectionName) {
        this.mongoTemplate.insert(model, collectionName);
    }

    public void insertList(List<?> models) {
        this.mongoTemplate.insert(models, getCollectionName(models.get(0).getClass()));
    }

    public void insertList(List<?> models, String collectionName) {
        this.mongoTemplate.insert(models, collectionName);
    }


    public void save(Object model) {
        this.mongoTemplate.save(model, getCollectionName(model.getClass()));
    }

    public void save(List<?> models) {
        this.mongoTemplate.save(models, getCollectionName(models.get(0).getClass()));
    }

    public void save(List<?> models, String collectionName) {
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName);
        for (Object model : models) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(model));
                if (jsonObject.containsKey("_id")) {
                    ops.remove(Query.query(Criteria.where("_id").is(jsonObject.getString("_id"))));
                } else if (jsonObject.containsKey("id")) {
                    ops.remove(Query.query(Criteria.where("id").is(jsonObject.getString("id"))));
                }
            } catch (Exception e) {
                continue;
            }
            ops.insert(model);
        }
        ops.execute();
    }

    public long updateFirst(Query query, Update update, Class<?> resultClass) {
        return this.mongoTemplate.updateFirst(query, update, getCollectionName(resultClass)).getModifiedCount();
    }

    public long updateById(String id, Update update, Class<?> resultClass) {
        Query query = new Query();
        query.addCriteria((new Criteria("_id")).is(id));
        return updateFirst(query, update, resultClass);
    }

    public long updateMulti(Query query, Update update, Class resultClass) {
        return this.mongoTemplate.updateMulti(query, update, getCollectionName(resultClass)).getModifiedCount();
    }

    public long remove(Object model) {
        return this.mongoTemplate.remove(model, getCollectionName(model.getClass())).getDeletedCount();
    }

    public long remove(Query query, Class<?> resultClass) {
        return this.mongoTemplate.remove(query, getCollectionName(resultClass)).getDeletedCount();
    }

    public long removeById(Class<?> resultClass, String id) {
        return remove(Query.query(Criteria.where("_id").is(id)),resultClass);
    }

    public boolean collectionExists(String colName) throws MongoException {
        return this.mongoTemplate.collectionExists(colName);
    }

    public Query createQuery(String queryString) {
        return new BasicQuery(BaseUtils.isNotBlank(queryString) ? queryString : "{}");
    }

    public Query createQuery() {
        return createQuery((String) null);
    }
}
