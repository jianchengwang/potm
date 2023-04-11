package org.example.framework.config.dict;

import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
public class DictRedisOperator {

    private final String DICT_CACHE = "dict_cache";

    private final String DICT_ITEM_LIST = "dict_cache:%s:%s";

    private final RedissonClient redissonClient;

    public DictRedisOperator(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public List<SysDict> loadAll() {
        return (List<SysDict>) redissonClient.getBucket(DICT_CACHE).get();
    }

    public void putAll(List<SysDict> dictList) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        transaction.getBucket(DICT_CACHE).set(dictList);
        dictList.forEach(dict -> {
            String dictItemListKey = String.format(DICT_ITEM_LIST, dict.getSvcName(), dict.getDictKey());
            redissonClient.getBucket(dictItemListKey).set(dict.getItemList());
        });
        transaction.commit();
    }

    public List<SysDictItem> getItemList(String svcName, String dictKey) {
        String dictItemListKey = String.format(DICT_ITEM_LIST, svcName, dictKey);
        return (List<SysDictItem>) redissonClient.getBucket(dictItemListKey).get();
    }

    public void putItemList(String svcName, String dictKey, List<SysDictItem> itemList) {
        String dictItemListKey = String.format(DICT_ITEM_LIST, svcName, dictKey);
        redissonClient.getBucket(dictItemListKey).set(itemList);
    }
}
