package com.jtframework.datasource.mysql8_nosql;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.datasource.common.ModelDaoService;

import java.sql.SQLException;
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
    PageVO pageQuery(int pageNo,int pageSize) throws SQLException;


    PageVO pageQuery(int pageNo, int pageSize, Map<String,Object> params) throws SQLException;
}
