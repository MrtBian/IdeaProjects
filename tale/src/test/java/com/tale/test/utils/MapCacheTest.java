package com.tale.test.utils;

import com.tale.test.ALLTests;
import com.blade.ioc.annotation.Inject;
import com.tale.utils.MapCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MapCacheTest extends ALLTests {

    @Inject
    private static MapCache mapCache;
    String name = "李俊薇";
    String schoolID = "SX1716006";
    String field = "nuaa";

    @Before
    public void before(){
        mapCache = new MapCache();
    }

    @Test
    public void testSet(){
        mapCache.set("name",name);
        mapCache.set("schoolID",schoolID);
        Assert.assertEquals(mapCache.get("name"),name);
        Assert.assertEquals(mapCache.get("schoolID"),schoolID);
        mapCache.clean();
        mapCache.set("name",name,1);
        Assert.assertEquals(mapCache.get("name"),name);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNull(mapCache.get("name"));
    }

    @Test
    public void testHset(){
        mapCache.hset("name",field,name);
        mapCache.hset("schoolID",field,schoolID);
        Assert.assertEquals(mapCache.hget("name",field),name);
        Assert.assertEquals(mapCache.hget("schoolID",field),schoolID);
        Assert.assertNull(mapCache.hget("name","nju"));
        mapCache.clean();
        mapCache.hset("name",field,name,1);
        Assert.assertEquals(mapCache.hget("name",field),name);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNull(mapCache.hget("name",field));
    }

    @Test
    public void testClean(){
        mapCache.set("name",name);
        mapCache.set("schoolID",schoolID);
        mapCache.hset("name",field,name);
        mapCache.hset("schoolID",field,schoolID);
        mapCache.clean();
        Assert.assertNull(mapCache.get("name"));
        Assert.assertNull( mapCache.get("schoolID"));
        Assert.assertNull(mapCache.hget("name",field));
        Assert.assertNull(mapCache.hget("schoolID",field));
    }

    @Test
    public void testDel(){
        mapCache.set("name",name);
        mapCache.set("schoolID",schoolID);
        mapCache.del("name");
        Assert.assertNull(mapCache.get("name"));
        Assert.assertEquals(mapCache.get("schoolID"),schoolID);
    }
    @Test
    public void testHdel(){
        mapCache.hset("name",field,name);
        mapCache.hset("schoolID",field,schoolID);
        mapCache.hdel("name",field);
        Assert.assertNull(mapCache.hget("name",field));
        Assert.assertEquals(mapCache.hget("schoolID",field),schoolID);
    }
}
