package com.jtframework.datasource.mongodb;

import lombok.Data;

@Data
public class MongoAuthClient {

    private String userName;

    private String passWord;

    public MongoAuthClient(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

}
