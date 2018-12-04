package com.lcy.tale.utils;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lcy
 * @since 2018/12/4
 */
public final class MapCache {
    private static final int DEFAULT_CACHE_SIZE = 1024;
    private final Map<String, CacheEntity> cache;
    private final ScheduledExecutorService executor;

    private MapCache() {
        cache = new ConcurrentHashMap<>(DEFAULT_CACHE_SIZE);
        executor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public static MapCache single() {
        return MapCacheHolder.me;
    }

    public void clear() {
        cache.clear();
    }

    public <T> T del(String key) {
        CacheEntity cacheEntity = cache.remove(key);
        if (null == cacheEntity) {
            return null;
        }
        Future future = cacheEntity.future;
        if (null != future) {
            future.cancel(true);
        }
        return (T) cacheEntity.value;
    }

    public <T> T get(String key) {
        CacheEntity cacheEntity = cache.get(key);
        return null == cacheEntity ? null : (T) cacheEntity.value;
    }

    /**
     * 读取一个hash类型缓存
     *
     * @param key   缓存key
     * @param field 缓存field
     * @param <T>
     * @return
     */
    public <T> T hget(String key, String field) {
        key = key + ":" + field;
        return this.get(key);
    }

    /**
     * 设置一个缓存
     *
     * @param key   缓存key
     * @param value 缓存value
     */
    public void set(String key, Object value) {
        this.set(key, value, 0);
    }

    /**
     * 设置一个缓存并带过期时间
     *
     * @param key     缓存key
     * @param value   缓存value
     * @param expired 过期时间，单位为秒
     */
    public void set(String key, Object value, long expired) {
        this.del(key);

        if (expired > 0) {
            Future future = executor.schedule(() -> {
                cache.remove(key);
            }, expired, TimeUnit.SECONDS);
            cache.put(key, new CacheEntity(value, future));
        } else {
            cache.put(key, new CacheEntity(value, null));
        }

        if (cache.size() > DEFAULT_CACHE_SIZE) {
            this.clear();
        }
    }

    /**
     * 设置一个hash缓存
     *
     * @param key   缓存key
     * @param field 缓存field
     * @param value 缓存value
     */
    public void hset(String key, String field, Object value) {
        this.hset(key, field, value, -1);
    }

    /**
     * 设置一个hash缓存并带过期时间
     *
     * @param key     缓存key
     * @param field   缓存field
     * @param value   缓存value
     * @param expired 过期时间，单位为秒
     */
    public void hset(String key, String field, Object value, long expired) {
        key = key + ":" + field;
        this.set(key, value, expired);
    }

    /**
     * 单实例静态类
     */
    private static final class MapCacheHolder {
        private static MapCache me =new MapCache();
    }

    /**
     * 缓存实体类
     */
    private static class CacheEntity {
        //键值对的value
        private Object value;
        //定时器Future
        private Future future;

        CacheEntity(Object value, Future future) {
            this.value = value;
            this.future = future;
        }
    }
}
