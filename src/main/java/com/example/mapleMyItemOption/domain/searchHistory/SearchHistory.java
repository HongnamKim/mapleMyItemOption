package com.example.mapleMyItemOption.domain.searchHistory;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchHistory {
    private Long id;
    private String characterName;
    private LocalDate date;
    private Boolean maximumAssaultDate;
}
