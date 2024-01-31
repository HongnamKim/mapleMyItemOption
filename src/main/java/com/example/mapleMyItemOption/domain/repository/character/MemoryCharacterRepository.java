package com.example.mapleMyItemOption.domain.repository.character;

import com.example.mapleMyItemOption.domain.character.Character;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCharacterRepository {
    private final static Map<Long, Character> store = new ConcurrentHashMap<>();
    private static Long key = 0L;

    public Character save(Character character){
        character.setKey(++key);
        store.put(character.getKey() ,character);

        return character;
    }

    public Character findByKey(Long key){
        return store.get(key);
    }

    public void clear(){
        store.clear();
    }
}
