package com.example.mapleMyItemOption.domain.repository.item;

import com.example.mapleMyItemOption.domain.item.RepoItemData.ItemEquipment;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryItemEquipmentRepository {
    private static final Map<Long, ItemEquipment> store = new ConcurrentHashMap<>();
    private static Long key = 0L;

    public static Map<Long, ItemEquipment> getStore() {
        return store;
    }

    public ItemEquipment save(ItemEquipment itemEquipment){
        itemEquipment.setItemEquipmentId(++key);
        store.put(itemEquipment.getItemEquipmentId(), itemEquipment);

        return itemEquipment;
    }

    public ItemEquipment findById (Long id){
        return store.get(id);
    }

    public void clear(){
        store.clear();
    }
}
