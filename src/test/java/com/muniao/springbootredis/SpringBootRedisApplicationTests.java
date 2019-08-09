package com.muniao.springbootredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisApplicationTests
{
    @Autowired
    private JedisPool jedisPool;

    @Test
    public void test1()
    {
        Jedis jedis = jedisPool.getResource();

        jedis.select(1);
        jedis.set("key1","value1");

    }

}
