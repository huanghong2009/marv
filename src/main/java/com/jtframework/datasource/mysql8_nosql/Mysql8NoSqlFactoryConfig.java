package com.jtframework.datasource.mysql8_nosql;


import com.jtframework.utils.BaseUtils;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Mysql8NoSqlFactoryConfig {

    public String url = "";


    private SessionFactory sessionFactory;


    public Mysql8NoSqlFactoryConfig(@Value("${mysql8.host:}") String host, @Value("${mysql8.prot:33060}") Integer prot,
                                    @Value("${mysql8.user:}") String user, @Value("${mysql8.password:}") String password,
                                    @Value("${mysql8.db:}") String db) {
        if (BaseUtils.isBlank(host) || BaseUtils.isBlank(user) || BaseUtils.isBlank(password) || BaseUtils.isBlank(db)) {
            log.warn("mysql8 未配置 ....");
            return;
        }

        this.sessionFactory = new SessionFactory();
        this.url = "mysqlx://" + host + ":" + prot + "/" + db + "?user=" + user + "&password=" + password+ "&xdevapi.compression-algorithm="

                + "lz4_message," // LZ4 triplet

                + FramedLZ4CompressorInputStream.class.getName() + ","

                + FramedLZ4CompressorOutputStream.class.getName() + ","

                + "zstd_stream," // zstd triplet

                + ZstdCompressorInputStream.class.getName() + ","

                + ZstdCompressorOutputStream.class.getName();

    }


    /**
     * 获取mysql8 配置信息
     *
     * @return
     */
    public Session getSession() throws Exception {
        if (BaseUtils.isBlank(url)){
            throw new Exception("mysql8未配置...");
        }
        return sessionFactory.getSession(this.url);
    }


}
