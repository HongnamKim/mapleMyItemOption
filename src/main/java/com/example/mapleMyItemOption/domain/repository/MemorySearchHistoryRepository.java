package com.example.mapleMyItemOption.domain.repository;

import com.example.mapleMyItemOption.domain.searchHistory.SearchHistory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemorySearchHistoryRepository implements SearchHistoryRepository{
    public static final Map<Long, SearchHistory> store = new ConcurrentHashMap<>();

    private static final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public SearchHistory save(SearchHistory searchHistory){
        searchHistory.setId(sequence.incrementAndGet());
        store.put(searchHistory.getId(), searchHistory);

        return searchHistory;
    }

    @Override
    public Optional<SearchHistory> findById(Long id){
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<SearchHistory> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void clear(){
        store.clear();
    }


}
