package com.jtframework.datasource.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.dao.ServerModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.CheckParam;
import com.jtframework.base.query.PageVO;
import com.jtframework.utils.AnnotationUtils;
import com.jtframework.utils.BaseUtils;

import com.mongodb.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * mongodb curd 工具类
 *
 * @author ttan
 */
@Slf4j
@Data
public class MongodbService {

    /**
     * # TCP连接超时，毫秒
     */
    public int connectionTimeoutMs = 10000;

    /**
     * # TCP读取超时，毫秒
     */
    public int readTimeoutMs = 15000;

    /**
     * #当连接池无可用连接时客户端阻塞等待的时长，单位毫秒
     */
    public int poolMaxWaitTimeMs = 3000;

    /**
     * #TCP连接闲置时间，单位毫秒
     */
    public int connectionMaxIdleTimeMs = 60000;

    /**
     * #TCP连接最多可以使用多久，单位毫秒
     */
    public int connectionMaxLifeTimeMs = 120000;

    /**
     * #心跳检测发送频率，单位毫秒
     */
    public int heartbeatFrequencyMs = 20000;

    /**
     * #最小的心跳检测发送频率，单位毫秒
     */
    public int minHeartbeatFrequencyMs = 8000;

    /**
     * #心跳检测TCP连接超时，单位毫秒
     */
    public int heartbeatConnectionTimeoutMs = 10000;

    /**
     * #心跳检测TCP连接读取超时，单位毫秒
     */
    public int heartbeatReadTimeoutMs = 15000;

    /**
     * # 每个host的最大TCP连接数
     */
    public int connectionsPerHost = 100;

    /***
     * #每个host的最小TCP连接数
     */
    public int minConnectionsPerHost = 5;

    /**
     * #计算允许多少个线程阻塞等待可用TCP连接时的乘数，算法：threadsAllowedToBlockForConnectionMultiplier*connectionsPerHost，当前配置允许2*100个线程阻塞
     */
    public int threadsAllowedToBlockForConnectionMultiplier = 2;

    public MongoTemplate mongoTemplate;

    public static String getCollectionName(Class<?> resultClass) {
        String serverName = resultClass.getClass().getSimpleName();
        ServerModel serverModel = (ServerModel) resultClass.getAnnotation(ServerModel.class);
        if (serverModel != null) {
            serverName = serverModel.value();
        }

        return serverName;
    }

    /**
     * 非用户验证init
     *
     * @param host
     * @param port
     * @param database
     * @throws Exception
     */
    public void initMongodbService(String host, Integer port, String database) throws Exception {
        this.mongoTemplate = new MongoTemplate(getMongoDbFactory(host, port, database, null));
        log.info("{}:{}:{} 初始化中 ......", host, port, database);
    }

    public void initMongodbService(MongoTemplate mongoTemplate) throws Exception {
        this.mongoTemplate = mongoTemplate;
        log.info("默认mongodb 初始化中 ......");
    }

    /**
     * 用户验证init
     *
     * @param host
     * @param port
     * @param database
     * @param mongoAuthClient
     * @throws Exception
     */
    public void initMongodbService(String host, Integer port, String database, MongoAuthClient mongoAuthClient) throws Exception {

        this.mongoTemplate = new MongoTemplate(getMongoDbFactory(host, port, database, mongoAuthClient));
        log.info("{}:{}:{}--{} 初始化中 ......", host, port, database, mongoAuthClient.getUserName());
    }


    /**
     * 创建database
     *
     * @param host
     * @param port
     * @param database
     * @param mongoAuthClient
     * @return
     */
    public MongoDbFactory getMongoDbFactory(String host, Integer port, String database, MongoAuthClient mongoAuthClient) {
        //创建客户端参数
        MongoClientOptions options = mongoClientOptions();

        ServerAddress serverAddress = new ServerAddress(host, port);

        MongoClient mongoClient = null;

        if (mongoAuthClient == null) {
            /** ps: 创建非认证客户端*/
            mongoClient = new MongoClient(serverAddress, options);
        } else {
            //创建认证客户端
            MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(mongoAuthClient.getUserName(), database,
                    mongoAuthClient.getPassWord().toCharArray());

            List<MongoCredential> mongoCredentials = new ArrayList<>();
            mongoCredentials.add(mongoCredential);

            mongoClient = new MongoClient(serverAddress, mongoCredentials, options);
        }

        return new SimpleMongoDbFactory(mongoClient, database);
    }


    /**
     * mongo客户端参数配置
     *
     * @return
     */
    public MongoClientOptions mongoClientOptions() {
        return MongoClientOptions.builder()
                .connectTimeout(this.getConnectionTimeoutMs())
                .socketTimeout(this.getReadTimeoutMs())
                .heartbeatConnectTimeout(this.getHeartbeatConnectionTimeoutMs())
                .heartbeatSocketTimeout(this.getHeartbeatReadTimeoutMs())
                .heartbeatFrequency(this.getHeartbeatFrequencyMs())
                .minHeartbeatFrequency(this.getMinHeartbeatFrequencyMs())
                .maxConnectionIdleTime(this.getConnectionMaxIdleTimeMs())
                .maxConnectionLifeTime(this.getConnectionMaxLifeTimeMs())
                .maxWaitTime(this.getPoolMaxWaitTimeMs())
                .connectionsPerHost(this.getConnectionsPerHost())
                .threadsAllowedToBlockForConnectionMultiplier(
                        this.getThreadsAllowedToBlockForConnectionMultiplier())
                .minConnectionsPerHost(this.getMinConnectionsPerHost()).build();
    }

    /**
     * 从1 开始，默认查询 10条 ，1 ，10
     *
     * @param resultClass
     * @param query
     * @param pageNo
     * @param pageSize
     * @param <T>
     * @return
     */
    public <T> PageVO<T> pageQuery(Class<T> resultClass, Query query, int pageNo, int pageSize) {
        int total = (int) this.mongoTemplate.count(query, getCollectionName(resultClass));
        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = 10;
            }


            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            query.limit(pageSize).skip((pageNo - 1) * pageSize);

            List<T> result = this.mongoTemplate.find(query, resultClass, getCollectionName(resultClass));

            return new PageVO(startIndex, total, pageSize, result);
        }
    }


    /**
     * 从1 开始，默认查询 10条 ，1 ，10
     *
     * @param resultClass
     * @param query
     * @param pageNo
     * @param pageSize
     * @param <T>
     * @return
     */
    public <T> PageVO<T> pageJoinQuery(Class<T> resultClass, MongodbParamsQuery query, MongoJoin mongoJoin, int pageNo, int pageSize) {
        LookupOperation lookupOperation = Aggregation.lookup(mongoJoin.getJoinCollectionName(), mongoJoin.getLocalField(), mongoJoin.getForeignField(), mongoJoin.getAsName());
        long total = aggregationCount(lookupOperation, resultClass, query);

        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = 10;
            }


            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);

            List<AggregationOperation> aggregationOperations = new ArrayList<>();
            /**
             * jon连接
             */
            aggregationOperations.add(lookupOperation);

            /**
             * 拼装where 参数
             */
            getOperationsMath(aggregationOperations, query);


            /**
             * project参数
             */
            setPeojectOperation(query, aggregationOperations);

            /**
             * 分页处理
             */
            aggregationOperations.add(Aggregation.limit(pageSize));
            aggregationOperations.add(Aggregation.skip((pageNo - 1) * pageSize));

            /**
             * 排序处理
             */
            if (query.getSort() != null) {
                aggregationOperations.add(Aggregation.sort(query.getSort()));
            }

            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
            List<T> result = mongoTemplate.aggregate(aggregation, AnnotationUtils.getServeModelValue(resultClass), resultClass).getMappedResults();

            return new PageVO(startIndex, total, pageSize, result);
        }
    }

    /**
     * 聚合统计
     *
     * @param
     * @return
     */
    public long aggregationCount(LookupOperation lookupOperation, Class resultClass, MongodbParamsQuery query) {
        List<AggregationOperation> aggregationOperations = getOperationsMath(query);
        aggregationOperations.add(Aggregation.count().as("total"));
        aggregationOperations.add(0, lookupOperation);
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        AggregationResults<CountVo> mongoTest = mongoTemplate.aggregate(aggregation, AnnotationUtils.getServeModelValue(resultClass), CountVo.class);

        List<CountVo> mappedResults = mongoTest.getMappedResults();
        long total = 0;
        if (!CollectionUtils.isEmpty(mappedResults)) {
            total = mappedResults.get(0).getTotal();
        }

        return total;
    }


    /**
     * 获取math管道
     *
     * @param query
     * @return
     */
    public static List<AggregationOperation> getOperationsMath(MongodbParamsQuery query) {
        return getOperationsMath(new ArrayList<AggregationOperation>(), query);
    }

    /**
     * 获取math管道
     *
     * @param query
     * @return
     */
    public static List<AggregationOperation> getOperationsMath(List<AggregationOperation> aggregationOperations, MongodbParamsQuery query) {

        for (CriteriaDefinition criteriaDefinition : query.getCriterias()) {
            aggregationOperations.add(Aggregation.match(criteriaDefinition));
        }

        return aggregationOperations;
    }

    public void dropCollection(Class<?> resultClass) {
        this.mongoTemplate.dropCollection(getCollectionName(resultClass));
    }

    public <T> List<T> find(Class<T> resultClass, Query query) {
        return this.mongoTemplate.find(query, resultClass, getCollectionName(resultClass));
    }


    /**
     * 根据query查询
     *
     * @param resultClass
     * @param <T>
     * @return
     */
    public <T> List<T> findByAggregationWithModel(Class<T> resultClass, MongodbParamsQuery query) {
        return findByAggregation(resultClass, query, AnnotationUtils.getServeModelValue(resultClass));
    }

    /**
     * 根据query查询
     *
     * @param resultClass
     * @param <T>
     * @return
     */
    public <T> List<T> findByAggregation(Class<T> resultClass, MongodbParamsQuery query, String collectionName) {

        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        /**
         * jon连接
         */
        MongoJoin mongoJoin = query.getMongoJoin();
        if (mongoJoin != null) {
            aggregationOperations.add(Aggregation.lookup(mongoJoin.getJoinCollectionName(), mongoJoin.getLocalField(), mongoJoin.getForeignField(), mongoJoin.getAsName()));
        }

        /**
         * 拼装where 参数
         */
        getOperationsMath(aggregationOperations, query);

        /**
         * project参数
         */
        setPeojectOperation(query, aggregationOperations);

        /**
         * 排序处理
         */
        if (query.getSort() != null) {
            aggregationOperations.add(Aggregation.sort(query.getSort()));
        }

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        return mongoTemplate.aggregate(aggregation, collectionName, resultClass).getMappedResults();
    }

    /**
     * 统计
     *
     * @param sumDto
     * @return
     * @throws BusinessException
     */
    public List<MongodbSumVo> sumList(String collectionName, MongodbGroupDto sumDto) throws Exception {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        /**
         * 拼装unwind参数
         */
        setUnwindOperation(sumDto, aggregationOperations);

        /**
         * 拼装where 参数
         */
        getOperationsMath(aggregationOperations, sumDto.query);

        /**
         * join参数
         */
        setJoinOperation(sumDto, aggregationOperations);


        /**
         * project参数
         */
        setPeojectOperation(sumDto.getQuery(), aggregationOperations);

        /**
         * group
         */
        aggregationOperations.add(sumDto.getGroupOperation());

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.mongoTemplate.aggregate(aggregation, collectionName, MongodbSumVo.class).getMappedResults();
    }

    /**
     * 拼装join参数
     *
     * @param sumDto
     * @param aggregationOperations
     */
    private void setUnwindOperation(MongodbGroupDto sumDto, List<AggregationOperation> aggregationOperations) {
        if (BaseUtils.isNotBlank(sumDto.getUnwindFieldName())) {
            aggregationOperations.add(Aggregation.unwind(sumDto.getUnwindFieldName()));
        }
    }


    /**
     * 拼装peoject参数
     *
     * @param mongodbParamsDTO
     * @param aggregationOperations
     */
    private void setPeojectOperation(MongodbParamsQuery mongodbParamsDTO, List<AggregationOperation> aggregationOperations) {
        if (mongodbParamsDTO.getProjectExpressionOperations().size() > 0) {

            /**
             * 每一个参数，写一个 peoject ，这样因为一个peoject里，无法引用上个计算出来的字段
             * sttep1：每次 新的 project 把之前的字段全部引用
             */
            List<String> fieldsList = new ArrayList<>();

            for (List<ProjectExpressionOperation> projectExpressionOperationList : mongodbParamsDTO.getProjectExpressionOperations()) {

                ProjectionOperation projectionOperation = Aggregation.project();
                /**
                 * 引用之前的字段
                 */
                for (String field : fieldsList) {
                    projectionOperation = projectionOperation.and(field).as(field);
                }

                for (ProjectExpressionOperation projectExpressionOperation : projectExpressionOperationList) {
                    /**
                     * 是否是原生语法
                     */
                    if (projectExpressionOperation.getIsExpression()) {
                        projectionOperation = projectionOperation.andExpression(projectExpressionOperation.getFieldName()).as(projectExpressionOperation.getAsName());
                    } else {
                        projectionOperation = projectionOperation.and(projectExpressionOperation.getFieldName()).as(projectExpressionOperation.getAsName());
                    }
                    fieldsList.add("$" + projectExpressionOperation.getAsName());
                }

                aggregationOperations.add(projectionOperation);

            }

        }
    }

    /**
     * 拼装join参数
     *
     * @param sumDto
     * @param aggregationOperations
     */
    private void setJoinOperation(MongodbGroupDto sumDto, List<AggregationOperation> aggregationOperations) {
        if (sumDto.getQuery().getMongoJoin() != null) {
            MongoJoin mongoJoin = sumDto.getQuery().getMongoJoin();
            LookupOperation lookupOperation = Aggregation.lookup(mongoJoin.getJoinCollectionName(), mongoJoin.getLocalField(), mongoJoin.getForeignField(), mongoJoin.getAsName());
            aggregationOperations.add(lookupOperation);
        }
    }

    /**
     * 统计
     *
     * @param sumDto
     * @return
     * @throws BusinessException
     */
    public <T> List<T> sumListWithResultClass(String collectionName, MongodbGroupDto<T> sumDto) throws Exception {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        /**
         * 拼装unwind参数
         */
        setUnwindOperation(sumDto, aggregationOperations);

        /**
         * 拼装join参数
         */
        setJoinOperation(sumDto, aggregationOperations);

        /**
         * 拼装where 参数
         */
        getOperationsMath(aggregationOperations, sumDto.query);

        /**
         * project参数
         */
        setPeojectOperation(sumDto.query, aggregationOperations);


        aggregationOperations.add(sumDto.getGroupOperation());

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return this.mongoTemplate.aggregate(aggregation, collectionName, sumDto.getResultClass()).getMappedResults();
    }


    /**
     * 统计
     *
     * @param sumDto
     * @return
     * @throws BusinessException
     */
    public BigDecimal sum(String collectionName, MongodbGroupDto sumDto) throws Exception {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();


        /**
         * 拼装unwind参数
         */
        setUnwindOperation(sumDto, aggregationOperations);

        /**
         * 拼装join参数
         */
        setJoinOperation(sumDto, aggregationOperations);

        /**
         * 拼装where 参数
         */
        getOperationsMath(aggregationOperations, sumDto.query);

        /**
         * project参数
         */
        setPeojectOperation(sumDto.query, aggregationOperations);


        aggregationOperations.add(sumDto.getGroupOperation());
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        List<MongodbSumVo> countDtos = this.mongoTemplate.aggregate(aggregation, collectionName, MongodbSumVo.class).getMappedResults();

        if (countDtos.size() == 0) {
            return new BigDecimal(0);
        }
        return countDtos.get(0).getAmount();
    }


    /**
     * 分组统计(返回单个字段) 取  returnFiledName
     *
     * @param groupDto
     * @return
     * @throws BusinessException
     */
    @CheckParam(value = "collectionName,groupDto.groupFiledName,groupDto.returnFiledName")
    public <T> List<MongodbGroupVo> selectWithOneFieldByGroup(String collectionName, MongodbGroupDto<T> groupDto) throws Exception {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        /**
         * 拼装unwind参数
         */
        setUnwindOperation(groupDto, aggregationOperations);


        /**
         * 拼装join参数
         */
        setJoinOperation(groupDto, aggregationOperations);

        /**
         * 拼装where 参数
         */
        getOperationsMath(aggregationOperations, groupDto.query);

        /**
         * project参数
         */
        setPeojectOperation(groupDto.query, aggregationOperations);

        /**
         * sort
         */
        if (BaseUtils.isNotBlank(groupDto.getSortFieldName()) && null != groupDto.getAsc()) {
            aggregationOperations.add(Aggregation.sort(groupDto.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, groupDto.getSortFieldName()));
        } else if (BaseUtils.isNotBlank(groupDto.getSortFiled()) && null != groupDto.getIsDesc()) {
            aggregationOperations.add(Aggregation.sort(groupDto.getIsDesc() ? Sort.Direction.ASC : Sort.Direction.DESC, groupDto.getSortFiled()));
        }

        aggregationOperations.add(groupDto.getGroupOperation());

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<MongodbGroupVo> countDtos = this.mongoTemplate.aggregate(aggregation, collectionName, MongodbGroupVo.class).getMappedResults();
        return countDtos;
    }

    /**
     * 分组统计(返回多个字段) 取  returnFiledNames
     *
     * @param groupDto
     * @return
     * @throws BusinessException
     */
    @CheckParam(value = "groupDto.groupFiledName")
    public <T> List<T> selectAllWithFieldsByGroup(String collectionName, MongodbGroupDto<T> groupDto) throws Exception {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        if (groupDto.getReturnFieldNames().size() == 0) {
            throw new Exception("returnFiledNames must not empty！");
        }

        /**
         * 拼装unwind参数
         */
        setUnwindOperation(groupDto, aggregationOperations);


        /**
         * 拼装join参数
         */
        setJoinOperation(groupDto, aggregationOperations);

        /**
         * 拼装where 参数
         */
        getOperationsMath(aggregationOperations, groupDto.query);

        /**
         * project参数
         */
        setPeojectOperation(groupDto.query, aggregationOperations);

        /**
         * sort
         */
        if (BaseUtils.isNotBlank(groupDto.getSortFieldName()) && null != groupDto.getAsc()) {
            aggregationOperations.add(Aggregation.sort(groupDto.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, groupDto.getSortFieldName()));
        } else if (BaseUtils.isNotBlank(groupDto.getSortFiled()) && null != groupDto.getIsDesc()) {
            aggregationOperations.add(Aggregation.sort(groupDto.getIsDesc() ? Sort.Direction.ASC : Sort.Direction.DESC, groupDto.getSortFiled()));
        }

        aggregationOperations.add(groupDto.getGroupOperation());

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<T> countDtos = this.mongoTemplate.aggregate(aggregation, collectionName, groupDto.getResultClass()).getMappedResults();

        return countDtos;
    }


    /**
     * 根据query查询
     *
     * @param resultClass
     * @param <T>
     * @return
     */
    public <T> List<T> findByQuery(Class<T> resultClass, Query query) {
        return this.mongoTemplate.find(query, resultClass, getCollectionName(resultClass));
    }

    /**
     * 根据kv简单查询
     *
     * @param resultClass
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> List<T> findByKV(Class<T> resultClass, String key, Object value) {
        return this.mongoTemplate.find(createQuery().addCriteria(Criteria.where(key).is(value)), resultClass, getCollectionName(resultClass));
    }

    /**
     * 根据map简单查询
     *
     * @param resultClass
     * @param <T>
     * @return
     */
    public <T> List<T> findByMap(Class<T> resultClass, Map<String, Object> params) {
        Query query = createQuery();
        for (String key : params.keySet()) {
            query.addCriteria(Criteria.where(key).is(params.get(key)));
        }
        return this.mongoTemplate.find(query, resultClass, getCollectionName(resultClass));
    }


    /**
     * 根据kv简单查询
     *
     * @param resultClass
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> T findOneByKV(Class<T> resultClass, String key, Object value) {
        return this.mongoTemplate.findOne(createQuery().addCriteria(Criteria.where(key).is(value)), resultClass, getCollectionName(resultClass));
    }

    /**
     * 根据map简单查询
     *
     * @param resultClass
     * @param <T>
     * @return
     */
    public <T> T findOneByMap(Class<T> resultClass, Map<String, Object> params) {
        Query query = createQuery();
        for (String key : params.keySet()) {
            query.addCriteria(Criteria.where(key).is(params.get(key)));
        }
        return this.mongoTemplate.findOne(query, resultClass, getCollectionName(resultClass));
    }

    /**
     * 查询全部
     *
     * @param resultClass
     * @param <T>
     * @return
     */
    public <T> List<T> findAll(Class<T> resultClass) {
        return this.mongoTemplate.findAll(resultClass, getCollectionName(resultClass));
    }

    public <T> T findOne(Class<T> resultClass, Query query) {
        return this.mongoTemplate.findOne(query, resultClass, getCollectionName(resultClass));
    }

    public <T> T findById(Class<T> resultClass, String id) {
        return this.mongoTemplate.findById(id, resultClass, getCollectionName(resultClass));
    }

    /**
     * 根据id查询
     *
     * @param resultClass
     * @param ids
     * @param <T>
     * @return
     */
    public <T> List<T> findById(Class<T> resultClass, Set<String> ids) {
        return this.mongoTemplate.find(createQuery().addCriteria(Criteria.where("_id").in(ids)), resultClass, getCollectionName(resultClass));
    }

    public void insert(Object model) {
        insert(model, getCollectionName(model.getClass()));
    }

    public void insert(Object model, String collectionName) {
        this.mongoTemplate.insert(model, collectionName);
    }

    /**
     * 批量插入，分500个一组
     *
     * @param models
     */
    public void insertList(List<?> models) {
        if (models.size() > 0) {
            insertList(models, AnnotationUtils.getServeModelValue(models.get(0).getClass()));
        }
    }

    /**
     * 批量插入，分500个一组
     *
     * @param models
     */
    public void insertList(List<?> models, String collectionName) {
        if (models.size() <= 700) {
            this.mongoTemplate.insert(models, collectionName);
        } else {
            BaseUtils.splitList(models, 700).parallelStream().forEach(list -> {
                this.mongoTemplate.insert(list, collectionName);
            });
        }
    }


    public void save(Object model) {
        this.mongoTemplate.save(model, getCollectionName(model.getClass()));
    }


    public void save(List<?> models) {
        this.save(models, getCollectionName(models.get(0).getClass()));
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
        return remove(Query.query(Criteria.where("_id").is(id)), resultClass);
    }

    public long removeByIds(Class<?> resultClass, Collection<String> ids) {
        return remove(Query.query(Criteria.where("_id").in(ids)), resultClass);
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
