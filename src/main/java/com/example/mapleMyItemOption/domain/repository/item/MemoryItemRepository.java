package com.example.mapleMyItemOption.domain.repository.item;

import com.example.mapleMyItemOption.domain.item.RepoItemData.Item;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryItemRepository {
    private static final Map<Long, Item> store = new ConcurrentHashMap<>();
    private static Long key = 0L;

    public static Map<Long, Item> getStore() {
        return store;
    }

    public Item save(Item item){
        item.setItemId(++key);
        store.put(item.getItemId(), item);

        return item;
    }

    public Item findByKey(Long key){
        return store.get(key);
    }

    public void clear(){
        store.clear();
    }
}
