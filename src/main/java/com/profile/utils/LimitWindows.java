package com.profile.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import java.util.Collections;

public class LimitWindows {
    private final static long limit = 1000;
    private final static long windowSeconds = 60; // 窗口时间（秒）

    public static boolean allowRequest(StringRedisTemplate stringRedisTemplate, String key) {
        long currentTime = System.currentTimeMillis();

        // 定义 Lua 脚本（使用模板字符串提高可读性）
        String luaScript =
                "local window_start_time = ARGV[1] - ARGV[3] * 1000\n" +
                        "redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', window_start_time)\n" +
                        "local now_request = redis.call('ZCARD', KEYS[1])\n" +
                        "if now_request < tonumber(ARGV[2]) then\n" +
                        "    redis.call('ZADD', KEYS[1], ARGV[1], ARGV[1])\n" +
                        "    return 1\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";

        // 封装 Lua 脚本并指定返回类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        // 执行脚本（参数顺序：键列表, 时间戳, 限流阈值, 窗口时间）
        Long result = stringRedisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                currentTime,
                limit,
                windowSeconds
        );

        return result != null && result == 1L;
    }
}