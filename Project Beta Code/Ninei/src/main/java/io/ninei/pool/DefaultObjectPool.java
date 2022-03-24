package io.ninei.pool;

import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.RecyclableArrayList;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public abstract class DefaultObjectPool<T> {

    public synchronized T getObject() throws Exception {
        long now = System.currentTimeMillis();
        if (!idleMap.isEmpty()) {
            for (Map.Entry<T, Long> entry : idleMap.entrySet()) {
                if (now - entry.getValue() > expTime) { //object has expired
                    removeElement(idleMap);
                } else {
                    T po = popElement(idleMap, entry.getKey());
                    push(activeMap, po, now);
                    return po;
                }
            }
        }

        // either no PooledObject is available or each has expired, so return a new one
        return createPooledObject(now);
    }

    private T createPooledObject(long now) throws Exception {
        T po;
        try {
            po = tClass.getDeclaredConstructor().newInstance();
            push(activeMap, po, now);
        } catch (Exception e) {
            throw e;
        }
        return po;
    }

    private synchronized void push(Map map, T po, long now) {
        map.put(po, now);
    }

    public void releaseObject(T po) throws Exception {
        cleanUp(po);
        idleMap.put(po, System.currentTimeMillis());
        activeMap.remove(po);
    }

    public int getActiveSize() {
        return activeMap.size();
    }

    public int getIdleSize() {
        return idleMap.size();
    }

    private T removeElement(ConcurrentHashMap<T, Long> map) {
        Map.Entry<T, Long> entry = map.entrySet().iterator().next();
        T key = entry.getKey();
//        Long value=entry.getValue();
        map.remove(entry.getKey());
        return key;
    }

    private T popElement(ConcurrentHashMap<T, Long> map, T key) {
        map.remove(key);
        return key;
    }

    protected abstract void cleanUp(T t);

    public void cleanUpAll() throws Exception {
        idleMap.forEach( (key, value) -> {
            cleanUp(key);
        });
        activeMap.forEach((key, value) -> {
            cleanUp(key);
        });
        idleMap.clear();
        activeMap.clear();


    }

    public DefaultObjectPool(Class<T> cls, long expireTime) {
        tClass = cls;
        expTime = expireTime;
    }

    private Class<T> tClass;
    private static long expTime = 6000;//6 seconds
    private ConcurrentHashMap<T, Long> idleMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<T, Long> activeMap = new ConcurrentHashMap<>();
}
