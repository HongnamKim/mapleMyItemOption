package com.example.mapleMyItemOption.domain.repository;

import com.example.mapleMyItemOption.domain.searchHistory.SearchHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository {
    public SearchHistory save(SearchHistory searchHistory);

    public Optional<SearchHistory> findById(Long id);

    public List<SearchHistory> findAll();

    public void clear();
}
