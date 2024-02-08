package com.example.mapleMyItemOption.domain.item.itemSearch;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PresetTotalStat {
    // 스타포스
    private Float averageStarforce;
    private Integer maximumStarforce;
    private Integer minimumStarforce;

    // 추가옵션
    private Map<Integer, Float> averageAddOption;

    // 주문서 작
    private List<Float> averageEtcOption;

    // 잠재 등급 갯수
    private Map<String, Integer> potentialGradeCount;
    private Map<String, Integer> additionalPotentialGradeCount;

    // 평균 잠재 수치
    private Map<String, Float> averagePotentialValue;
    private Map<String, Float> averageAdditionalPotentialValue;

    // 전체 잠재 수치
    private Map<String, Float> totalPotentialValue;
    private Map<String, Float> totalAdditionalPotentialValue;

    // 잠재 옵션 갯수
    private Map<String, Float> potentialOptionLines;
    private Map<String, Float> additionalPotentialOptionLines;
}
