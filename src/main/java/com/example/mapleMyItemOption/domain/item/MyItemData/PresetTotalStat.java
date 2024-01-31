package com.example.mapleMyItemOption.domain.item.MyItemData;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PresetTotalStat {
    private Float averageStarforce;
    private Integer maximumStarforce;
    private Integer minimumStarforce;

    private Map<Integer, Float> averageAddOption;

    private List<Float> averageEtcOption;

    private Map<String, Integer> potentialGradeCount;
    private Map<String, Integer> additionalPotentialGradeCount;

    private Map<String, Float> averagePotentialValue;
    private Map<String, Float> averageAdditionalPotentialValue;

    private Map<String, Float> totalPotentialValue;
    private Map<String, Float> totalAdditionalPotentialValue;

    private Map<String, Float> potentialOptionLines;
    private Map<String, Float> additionalPotentialOptionLines;
}
