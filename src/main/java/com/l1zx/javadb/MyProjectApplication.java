package com.l1zx.javadb;

import com.l1zx.javadb.backend.dm.DataManager;
import com.l1zx.javadb.backend.tbm.TableManager;
import com.l1zx.javadb.backend.tm.TransactionManager;
import com.l1zx.javadb.backend.vm.VersionManager;
import com.l1zx.javadb.backend.vm.VersionManagerImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class MyProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);
    }

    @Bean
    public TableManager tableManager() throws Exception {
        String path = "/tmp/mydb";
        File dbFile = new File(path + ".xid");

        TransactionManager tm;
        DataManager dm;
        VersionManager vm;

        if (dbFile.exists()) {
            tm = TransactionManager.open(path);
            dm = DataManager.open(path, (1 << 20) * 64, tm);
        } else {
            tm = TransactionManager.create(path);
            dm = DataManager.create(path, (1 << 20) * 64, tm);
        }

        vm = new VersionManagerImpl(tm, dm);
        return TableManager.open(path, vm, dm);
    }
}
