package com.muniao.springbootredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisApplicationTests
{
    @Autowired
    private JedisPool jedisPool;

    @Test
    public void test1()
    {
        long startTime = System.currentTimeMillis(); // 获取开始时间

        Jedis jedis = jedisPool.getResource();
        String setKey = "setKey";

        Pipeline p = jedis.pipelined();
        for (int i = 1; i <= 10000000; i++)
        {
            p.sadd(setKey, String.valueOf(i));
            if (i % 1000 == 0)
                p.sync();
        }
        p.sync();

        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
    }

    @Test
    public void test2()
    {
        long startTime = System.currentTimeMillis(); // 获取开始时间

        Jedis jedis = jedisPool.getResource();
        String setKey = "setKey";
        jedis.del(setKey);

        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
    }

    @Test
    public void test4()
    {
        long startTime = System.currentTimeMillis(); // 获取开始时间

        Jedis jedis = jedisPool.getResource();
        String bigSetKey = "setKey";

        ScanParams sp = new ScanParams().count(1000);
        String cursor = "0";
        do
        {
            ScanResult<String> scanResult = jedis.sscan(bigSetKey, cursor, sp);
            List<String> memberList = scanResult.getResult();
            if (memberList != null && !memberList.isEmpty())
            {
                memberList.forEach(v -> jedis.srem(bigSetKey, v));
            }
            cursor = scanResult.getStringCursor();
        }
        while (!"0".equals(cursor));

        jedis.del(bigSetKey);

        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
    }


    @Test
    public void test5()
    {
        try(Jedis jedis = jedisPool.getResource())
        {
            Pipeline pipeline = jedis.pipelined();

            for(int i=0;i<10000000;i++)
            {
                pipeline.set("k"+i, "v"+i);
                if(i%1000==0)
                    pipeline.sync();
            }
            pipeline.sync();

            pipeline.set("basekey","b1");
            pipeline.sync();
        }
    }

    @Test
    public void test6()
    {
        try(Jedis jedis = jedisPool.getResource())
        {
            String keyPattern = "k*";

            /*long startTime = System.currentTimeMillis(); // 获取开始时间
            Set<String> set = jedis.keys(keyPattern);
            System.out.println(!set.isEmpty());
            long endTime = System.currentTimeMillis(); // 获取结束时间
            System.out.println("程序运行时间： " + (endTime - startTime) + "ms");*/

            long startTime = System.currentTimeMillis(); // 获取开始时间
            boolean exist = existKeyPattern(jedis, keyPattern);
            System.out.println(exist);
            long endTime = System.currentTimeMillis(); // 获取结束时间
            System.out.println("程序运行时间： " + (endTime - startTime) + "ms");


            /*startTime = System.currentTimeMillis(); // 获取开始时间
            Set<String> set1 = jedis.keys(keyPattern);
            System.out.println(!set1.isEmpty());
            endTime = System.currentTimeMillis(); // 获取结束时间
            System.out.println("程序运行时间： " + (endTime - startTime) + "ms");*/
        }
    }

    public boolean existKeyPattern(Jedis jedis, String keyPattern)
    {
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams sp = new ScanParams();
        sp.count(10000);
        sp.match(keyPattern);

        do
        {
            ScanResult<String> scanResult = jedis.scan(cursor, sp);
            cursor = scanResult.getStringCursor();
            List<String> list = scanResult.getResult();
            if(!list.isEmpty()) return true;
        }while (!ScanParams.SCAN_POINTER_START.equals(cursor));

        return false;
    }


}
