package com.example.mapleMyItemOption.domain.searchHistory;

import com.example.mapleMyItemOption.domain.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistory saveSearchHistory(String characterName, String date, Boolean maximumAssaultDate){
        SearchHistory searchHistory = new SearchHistory();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);

        searchHistory.setCharacterName(characterName);
        searchHistory.setDate(localDate);
        searchHistory.setMaximumAssaultDate(maximumAssaultDate);

        searchHistoryRepository.save(searchHistory);

        return searchHistory;
    }

    public List<SearchHistory> findByCharacterName(String characterName){
        List<SearchHistory> allHistories = searchHistoryRepository.findAll();

        return allHistories.stream()
                .filter(searchHistory -> searchHistory.getCharacterName().equals(characterName))
                .toList();
    }

    public List<SearchHistory> findAll(){
        return searchHistoryRepository.findAll();
    }

    public List<String> getSearchRanking(){
        List<SearchHistory> allHistories = searchHistoryRepository.findAll();

        Map<String, Long> searchCount = new LinkedHashMap<>();

        allHistories.forEach(searchHistory -> {
            String characterName = searchHistory.getCharacterName();
            Long currentCount = searchCount.getOrDefault(characterName, 0L);

            searchCount.put(characterName, currentCount + 1);

        });

        return null;
    }
}
