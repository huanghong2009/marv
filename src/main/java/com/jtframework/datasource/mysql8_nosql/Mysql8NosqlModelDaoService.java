package com.jtframework.datasource.mysql8_nosql;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.common.ModelDaoService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface Mysql8NosqlModelDaoService extends ModelDaoService {
    /**
     * 保存，有id 修改，无id 插入
     *
     * @param model
     * @throws BusinessException
     */
    public void save(BaseModel model) throws BusinessException;

    /**
     * 分页查询，默认查询前10条
     *
     * @return
     * @throws SQLException
     */
    PageVO pageQuery(int pageNo,int pageSize) throws Exception;


    /**
     * 根据map 分页查询，sql 默认是 =，key，是map的key,占位符参数是map的value
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     * @throws Exception
     */
    PageVO pageQuery(int pageNo, int pageSize, Map<String,Object> params) throws Exception;


    /**'
     * 根据查询sql 分页查询，不需要 占位符 参数
     * @param pageNo
     * @param pageSize
     * @param findStr
     * @return
     * @throws Exception
     */
    PageVO pageQuery(int pageNo, int pageSize,String findStr) throws Exception;


    /**
     * 根据查询sql 分页查询，占位符参数，是传递过来的 参数
     * @param pageNo
     * @param pageSize
     * @param findStr
     * @param bindParams
     * @return
     * @throws Exception
     */
    PageVO pageQuery(int pageNo, int pageSize,String findStr,Map<String,Object> bindParams) throws Exception;


    /**
     * 根据 多个查询sql分页 ，这里 会把每一个 查询sql and 拼接起来，占位符参数，是传递过来的 参数
     * @param pageNo
     * @param pageSize
     * @param findStr
     * @param bindParams
     * @return
     * @throws Exception
     */
    PageVO pageQuery(int pageNo, int pageSize, List<String> findStr, Map<String,Object> bindParams) throws Exception;
}
