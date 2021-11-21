package com.jtframework.datasource.mysql8_nosql;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.base.query.ParamsDTO;
import com.jtframework.datasource.common.ModelDaoService;

import java.util.List;
import java.util.Map;

public interface Mysql8NosqlModelDaoService extends ModelDaoService {
    /**
     * 保存，有id 修改，无id 插入
     *
     * @param model
     * @throws BusinessException
     */
    public void save(BaseModel model) throws Exception;


    /**
     * 根据sql修改
     * @param queryStrs 查询sql
     * @param setMap 修改sql
     * @param params 参数
     * @return
     */
    Long updateFromSqlStrsBySqlStrs(List<String> queryStrs,Map<String,Object> setMap,Map<String,Object> params) throws Exception;





    /**
     * 根据 map kv查询多条数据
     * @param findStrs sql语句
     * @param params bind参数
     * @return
     * @throws BusinessException
     */
    List selectListForFindStrs(List<String> findStrs,Map<String, Object> params) throws Exception;



    /**
     * 根据 多个查询sql分页 ，这里 会把每一个 查询sql and 拼接起来，占位符参数，是传递过来的 参数
     * @return
     * @throws Exception
     */
    PageVO pageQuery(Mysql8NosqlParamsDTO paramsDTO) throws Exception;
}
