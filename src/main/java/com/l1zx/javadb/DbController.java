package com.l1zx.javadb;
import java.net.URLDecoder;        // 包含 URLDecoder 类
import java.nio.charset.StandardCharsets; // 包含 StandardCharsets 枚举
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.l1zx.javadb.backend.tbm.TableManager;
import com.l1zx.javadb.backend.server.Executor;

@RestController
@RequestMapping("/db")
public class DbController {

    @Autowired
    private TableManager tbm;

    @PostMapping("/execute")
    public String execute(@RequestBody String sql) {
        try {
            // 解码URL编码的SQL语句
            String decodedSql = URLDecoder.decode(sql, StandardCharsets.UTF_8.toString());
            Executor exe = new Executor(tbm);
            byte[] result = exe.execute(decodedSql.getBytes());
            return new String(result);
        } catch (Exception e) {
            return "执行错误: " + e.getMessage();  // 返回具体的异常信息
        }
    }
}