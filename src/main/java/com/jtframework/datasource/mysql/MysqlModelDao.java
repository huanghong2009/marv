package com.jtframework.datasource.mysql;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.utils.BaseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public abstract class MysqlModelDao<T> implements Serializable {

    private String name;

    private Class cls;

    public MysqlModelDao() {
        Class cls = getTClass();
        this.cls = cls;
        this.name = BaseUtils.getServeModelDesc(cls);
    }

    abstract MysqlService getMysqlService();

    public Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }


    public void insert(T model) throws BusinessException {
        try {
            getMysqlService().insert(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("保存 " + this.name + " 失败");
        }
    }

    public T load(String id) throws BusinessException {
        try {
            return (T) getMysqlService().load(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("获取" + this.name + "失败");
        }
    }

    public int delete(String id) throws BusinessException {
        try {
            return getMysqlService().delete(cls, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("删除" + this.name + "失败");
        }
    }

    public int update(T model) throws BusinessException {
        try {
            return getMysqlService().update(model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("修改" + this.name + "失败");
        }
    }

    /**
     * 查询全部数据
     * @return
     * @throws BusinessException
     */
    public List<T> selectAll() throws BusinessException {
        try {
            return getMysqlService().selectListAll(this.cls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("查询全部" + this.name + "失败");
        }
    }

    /**
     * 分页查询，默认查询前10条
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> PageVO<T> defalutPageQuery() throws SQLException {
        try {
            return getMysqlService().pageQuery(this.cls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException("分页查询" + this.name + "失败");
        }
    }

}