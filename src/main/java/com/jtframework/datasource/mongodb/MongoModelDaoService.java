package com.jtframework.datasource.mongodb;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.common.ModelDaoService;



import java.math.BigDecimal;
import java.util.List;

public interface MongoModelDaoService  extends ModelDaoService {
    /**
     * 保存，有id 修改，无id 插入
     *
     * @param model
     * @throws BusinessException
     */
     void save(Object model) throws BusinessException;

    /**
     * 分页查询
     *
     * @param mongodbParamsDTO
     * @throws BusinessException
     */
     PageVO pageQuery(MongodbParamsDTO mongodbParamsDTO) throws BusinessException;

    /**
     * 根据dto 查询
     * @param mongodbParamsDTO
     * @return
     * @throws BusinessException
     */
     List findByDto(MongodbParamsDTO mongodbParamsDTO) throws BusinessException;


    /**
     * 根据query 查询
     * @param query
     * @return
     * @throws BusinessException
     */
    List findByQuery(MongodbParamsQuery query) throws BusinessException;


    /**
     * 统计
     * @param sumDto
     * @return
     * @throws BusinessException
     */
    BigDecimal sum(MongodbGroupDto sumDto) throws Exception;

    /**
     * 统计
     * @param sumDto
     * @return
     * @throws BusinessException
     */
    List<MongodbSumVo> sumList(MongodbGroupDto sumDto) throws Exception;


    /**
     * 统计
     * @param sumDto
     * @return
     * @throws BusinessException
     */
    <T>List<T> sumListWithResultClass(MongodbGroupDto<T> sumDto) throws Exception;

    /**
     * 分组查询(返回单个字段)
     * @param mongodbGroupDto
     * @return
     * @throws BusinessException
     */
    List<MongodbGroupVo> selectWithOneFieldByGroup(MongodbGroupDto mongodbGroupDto) throws Exception;

    /**
     * 分组查询返回多个字段
     * @param mongodbGroupDto
     * @return
     * @throws BusinessException
     */
    <T>List<T>  selectWithFieldsByGroup(MongodbGroupDto<T> mongodbGroupDto) throws Exception;

    /**
     * 备份清空集合
     * @param maxSize 需要备份的最大数量
     * @throws Exception
     */
    void cleanAndBackups(int maxSize) throws Exception;

    /**
     * 备份集合
     * @param maxSize 需要备份的最大数量
     * @throws Exception
     */
    void backups(int maxSize) throws Exception;




}
