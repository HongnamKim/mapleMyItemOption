package com.example.mapleMyItemOption.domain.repository.item;

import com.example.mapleMyItemOption.domain.item.RepoItemData.ItemOption;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryItemOptionRepository {
    private static final Map<Long, ItemOption> store = new ConcurrentHashMap<>();
    private static Long key = 0L;

    public static Map<Long, ItemOption> getStore() {
        return store;
    }

    public ItemOption save(ItemOption itemOption){
        itemOption.setItemOptionId(++key);
        store.put(itemOption.getItemOptionId(), itemOption);

        return null;
    }

    public ItemOption findById(Long id){
        return store.get(id);
    }

    public void deleteById(Long id){
        store.remove(id);
    }

    public void clear(){
        store.clear();
    }
}
