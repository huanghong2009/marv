package ${package}.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.dao.ServerModel;
import com.jtframework.utils.GenUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
* @description
* @author
* @date ${date}
**/
@Data
@ServerModel(value = "${changeClassName}", desc = "${moduleName}")
public class ${upClassName}Model extends BaseModel {

    <#list 0..(dataList!?size-1) as i>
    @ExcelProperty(value = "${dataList[i].desc!}", index = ${dataList[i].index})
    @ApiModelProperty(value = "${dataList[i].desc!}")
    private ${dataList[i].type!} ${dataList[i].name!};

    </#list>


}