package com.jtframework.datasource.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;

import java.util.ArrayList;
import java.util.List;


@Data
public class MongodbGroupDto<T> extends MongodbParamsDTO{



    /**
     * 分组字段
     */
    private List<GroupFields> groupFieldNames;

    /**
     * sum字段
     */
    private String sortFieldName;

    public List<GroupFields> getReturnFieldNames() {
        return returnFieldNames;
    }

    /**
     * 是否正序
     */
    private Boolean asc;




    /**
     * 返回字段
     */
    private List<GroupFields> returnFieldNames;




    /**
     * sum字段
     */
    private List<GroupFields>  sumFields;

    /**
     * 返回class
     */
    private Class<T> resultClass;


    public MongodbGroupDto(){
        init();
    }

    private void init() {
        this.groupFieldNames = new ArrayList<GroupFields>();
        this.returnFieldNames = new ArrayList<GroupFields>();
        this.sumFields = new ArrayList<GroupFields>();
    }



    public MongodbGroupDto(String groupFieldName, String sortFieldName, Boolean asc, List<GroupFields> returnFieldNames, Class<T> resultClass) {
        this.init();
        this.setGroupFieldName(groupFieldName);
        this.setReturnFieldNames(returnFieldNames);

        this.sortFieldName = sortFieldName;
        this.asc = asc;
        this.resultClass = resultClass;
    }

    public MongodbGroupDto(List<GroupFields> groupFieldNames, String sortFieldName, Boolean asc, List<GroupFields> returnFieldNames, Class<T> resultClass) {
        this.init();
        this.setGroupFieldNames(groupFieldNames);
        this.setReturnFieldNames(returnFieldNames);
        this.sortFieldName = sortFieldName;
        this.asc = asc;

        this.resultClass = resultClass;
    }

    public MongodbGroupDto(String groupFieldName, String sortFieldName, Boolean asc) {
        this.init();
        this.setGroupFieldName(groupFieldName);
        this.sortFieldName = sortFieldName;
        this.asc = asc;
    }


    public MongodbGroupDto(List<GroupFields> groupFieldNames, String sortFieldName, Boolean asc) {
        this.init();
        this.setGroupFieldNames(groupFieldNames);
        this.sortFieldName = sortFieldName;
        this.asc = asc;
    }


    private void setReturnFieldNames(List<GroupFields> returnFieldNames) {
        this.returnFieldNames = returnFieldNames;
    }

    private void setSumFields(List<GroupFields> sumFields) {
        this.sumFields = sumFields;
    }

    private void setGroupFieldNames(List<GroupFields> groupFieldNames) {
        this.groupFieldNames = groupFieldNames;
    }

    public void setGroupFieldName(String groupFieldName) {
        if (this.groupFieldNames.size() > 0){
            throw new RuntimeException("设置单个字段就不能有多个group field");
        }
        this.groupFieldNames.add(new GroupFields(groupFieldName,groupFieldName));
    }

    /**
     * 添加分组字段
     * @param groupFieldName
     * @param asName
     * @return
     */
    public MongodbGroupDto addGroupFieldName(String groupFieldName,String asName) {
        this.groupFieldNames.add(new GroupFields(groupFieldName,asName));
        return this;
    }

    /**
     * set sum field
     * @return
     */
    public MongodbGroupDto addSumField(String fieldName,String asName){
        this.sumFields.add(new GroupFields(fieldName,asName));
        return this;
    }

    /**
     * set sum field
     * @return
     */
    public MongodbGroupDto setSumField(String fieldName){
        if (this.returnFieldNames.size() > 0){
            throw new RuntimeException("设置单个字段就不能有多个sum field");
        }
        this.sumFields.add(new GroupFields(fieldName,"amount"));
        return this;
    }

    /**
     * set return field
     * @return
     */
    public MongodbGroupDto setReturnField(String fieldName){
        if (this.returnFieldNames.size() > 0){
            throw new RuntimeException("设置单个字段就不能有多个result field");
        }
        this.returnFieldNames.add(new GroupFields(fieldName,"result"));

        return this;
    }

    /**
     * set return field
     * @return
     */
    public MongodbGroupDto addReturnField(String fieldName,String asName){
        this.returnFieldNames.add(new GroupFields(fieldName,asName));

        return this;
    }

    /**
     * 获取group
     * @param
     * @return
     */
    public GroupOperation getGroupOperation() {
        /**
         * group
         */
        List<Field> fields = new ArrayList<Field>();

        this.getGroupFieldNames().stream().forEach(groupFields -> {
            fields.add(Fields.field(groupFields.getAsName(),groupFields.getFieldName()));
        });


        GroupOperation groupOperation = Aggregation.group(Fields.from(fields.toArray(new Field[fields.size()])));

        /**
         * first 每次 都会new 新的对象，所以每次需要用 groupOperation 再次接收一下
         */
        for (GroupFields groupField : this.getReturnFieldNames()) {
            groupOperation = groupOperation.first(groupField.getFieldName()).as(groupField.getAsName());
        }

        for (GroupFields groupField : this.getSumFields()) {
            groupOperation = groupOperation.sum(groupField.getFieldName()).as(groupField.getAsName());
        }

        return groupOperation;
    }

}
