package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryShopTypeList() {
        String shopTypeJson = stringRedisTemplate.opsForValue().get(RedisConstants.SHOP_TYPE_KEY);
        if (StrUtil.isNotBlank(shopTypeJson)) {
            return Result.ok(JSONUtil.toList(shopTypeJson, ShopType.class));
        }
        List<ShopType> shopTypes = query().orderByAsc("sort").list();
        if (shopTypes == null) {
            return Result.fail("不存在店铺信息");
        }
        stringRedisTemplate.opsForValue().set(RedisConstants.SHOP_TYPE_KEY,JSONUtil.toJsonStr(shopTypes));
        return Result.ok(shopTypes);
    }
}
