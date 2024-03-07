package com.example.mapleMyItemOption.domain.item.itemSearch;


import com.example.mapleMyItemOption.api.ApiService;
import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.item.*;
import com.example.mapleMyItemOption.domain.item.MyItemData.Item;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItem;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemSearchService {

    private final ApiService apiService;
    private final PresetTotalStatAnalyzer presetTotalStatAnalyzer;
    private final PresetItemAnalyzer presetItemAnalyzer;

    /**
     * 캐릭터의 프리셋에 등록된 장비 불러오는 메소드
     * @param characterName 검색하려는 캐릭터 이름
     * @param date 검색 기준 날짜
     * @return MyItemEquipment (날짜, 직업, 성별, 현재 프리셋 번호, 프리셋장비)
     */
    public MyItemEquipment searchMyItemEquipment(String characterName, String date){
        String ocid = apiService.fetchCharacterOcid(characterName);
        return apiService.fetchMyItemEquipment(ocid, date);
    }

    private List<MyItem> getPreset(MyItemEquipment myItemEquipment, int presetNo){
        switch (presetNo){
            case 1 -> {return myItemEquipment.getPreset1();}
            case 2 -> {return myItemEquipment.getPreset2();}
            case 3 -> {return myItemEquipment.getPreset3();}
            default -> throw new IllegalArgumentException("ItemEquipment PresetNo Error");
        }
    }

    /**
     * 프리셋 종합 옵션 정보 (스타포스, 추가옵션, 잠재, 에디셔널, 주문서)
     * @param myItemEquipment MyItemEquipment 캐릭터 장비 정보
     * @param character Character 캐릭터 기본 정보
     * @param presetNum 조회하려는 프리셋 번호
     * @return PresetTotalStat 객체
     */
    public PresetTotalStat getPresetTotalStat(MyItemEquipment myItemEquipment, Character character, int presetNum){

        String mainStat = presetTotalStatAnalyzer.getMainStat(character);

        List<MyItem> preset = getPreset(myItemEquipment, presetNum);

        Float averageStarforce = presetTotalStatAnalyzer.getAverageStarforce(preset);
        Integer maximumStarforce = presetTotalStatAnalyzer.getMaximumStarforce(preset);
        Integer minimumStarforce = presetTotalStatAnalyzer.getMinimumStarforce(preset);

        if(minimumStarforce > maximumStarforce){
            minimumStarforce = 0;
        }

        Map<Integer, Float> averageAddOption = presetTotalStatAnalyzer.getAverageAddOption(preset, mainStat);
        Map<String, List<Float>> averageEtcOption = presetTotalStatAnalyzer.getAverageEtcOption(preset, mainStat);

        Map<String, Integer> potentialGradeCount = presetTotalStatAnalyzer.countPotentialGrade(preset, false);
        Map<String, Integer> additionalPotentialGradeCount = presetTotalStatAnalyzer.countPotentialGrade(preset, true);

        Map<String, Float> averagePotentialValues = presetTotalStatAnalyzer.getPresetPotentialValue(preset, character, false, PotentialValuesOption.AVERAGE);
        Map<String, Float> averageAdditionalPotentialValues = presetTotalStatAnalyzer.getPresetPotentialValue(preset, character, true, PotentialValuesOption.AVERAGE);

        Map<String, Float> totalPotentialValues = presetTotalStatAnalyzer.getPresetPotentialValue(preset, character, false, PotentialValuesOption.TOTAL);
        Map<String, Float> totalAdditionalPotentialValues = presetTotalStatAnalyzer.getPresetPotentialValue(preset, character, true, PotentialValuesOption.TOTAL);

        Map<String, Float> potentialValueLines = presetTotalStatAnalyzer.getPresetPotentialValue(preset, character, false, PotentialValuesOption.LINES);
        Map<String, Float> additionalPotentialValueLines = presetTotalStatAnalyzer.getPresetPotentialValue(preset, character, true, PotentialValuesOption.LINES);

        // 불러온 정보 프리셋 별로 넣기
        PresetTotalStat presetTotalStat = new PresetTotalStat();
        presetTotalStat.setAverageStarforce(averageStarforce); // 평균 스타포스
        presetTotalStat.setMaximumStarforce(maximumStarforce); // 최대 스타포스
        presetTotalStat.setMinimumStarforce(minimumStarforce); // 최소 스타포스

        presetTotalStat.setAverageAddOption(averageAddOption); // 추옵

        presetTotalStat.setAverageEtcOption(averageEtcOption); // 주문서 공/주스탯

        presetTotalStat.setPotentialGradeCount(potentialGradeCount); // 잠재 등급 개수
        presetTotalStat.setAdditionalPotentialGradeCount(additionalPotentialGradeCount); // 에디 등급 개수

        presetTotalStat.setAveragePotentialValue(averagePotentialValues); // 잠재 평균 수치
        presetTotalStat.setAverageAdditionalPotentialValue(averageAdditionalPotentialValues); // 에디 평균 수치

        presetTotalStat.setTotalPotentialValue(totalPotentialValues); // 잠재 총합 수치
        presetTotalStat.setTotalAdditionalPotentialValue(totalAdditionalPotentialValues); // 에디 총합 수치

        presetTotalStat.setPotentialOptionLines(potentialValueLines); // 잠재 옵션 개수
        presetTotalStat.setAdditionalPotentialOptionLines(additionalPotentialValueLines); // 에디 옵션 개수

        return presetTotalStat;

    }

    public Map<String, Item> getPresetItemStats (PresetTotalStat presetTotalStat, MyItemEquipment myItemEquipment, Character character, Integer presetNum, String category) throws IllegalArgumentException{

        List<MyItem> preset = getPreset(myItemEquipment, presetNum);

        Map<String, Item> presetItemStats = new HashMap<>();

        List<String> itemCategory;

        switch (category){
            case ItemSlot.CATEGORY_WEAPONS -> itemCategory = ItemSlot.WEAPONS;
            case ItemSlot.CATEGORY_ARMORS -> itemCategory = ItemSlot.ARMORS;
            case ItemSlot.CATEGORY_ACCESSORIES -> itemCategory = ItemSlot.ACCESSORIES;
            case ItemSlot.CATEGORY_OTHERS -> itemCategory = ItemSlot.OTHERS;
            default -> throw new IllegalArgumentException();
        }

        for(MyItem myItem : preset) {
            if(!itemCategory.contains(myItem.getItemEquipmentSlot())){
                continue;
            }

            Item item = presetItemAnalyzer.analyzeItem(presetTotalStat, myItem, character, category);

            presetItemStats.put(myItem.getItemEquipmentSlot(), item);

        }

        Map<String, Item> sortedPresetItemStats = new LinkedHashMap<>();

        for(String slot : itemCategory) {
            Optional.ofNullable(presetItemStats.get(slot))
                    .ifPresent(item -> sortedPresetItemStats.put(slot, item));
        }

        return sortedPresetItemStats;
    }

    @Deprecated
    public Map<String, Item> getPresetItemStats (MyItemEquipment myItemEquipment, Character character, Integer presetNum, List<String> itemCategory){

        List<MyItem> preset = getPreset(myItemEquipment, presetNum);

        Map<String, Item> presetItemStats = new HashMap<>();

        for(MyItem myItem : preset) {
            if(!itemCategory.contains(myItem.getItemEquipmentSlot())){
                continue;
            }

            Item item = presetItemAnalyzer.analyzeItem(myItem, character);

            presetItemStats.put(myItem.getItemEquipmentSlot(), item);

        }

        Map<String, Item> sortedPresetItemStats = new LinkedHashMap<>();

        for(String slot : itemCategory) {
            Optional.ofNullable(presetItemStats.get(slot))
                    .ifPresent(item -> sortedPresetItemStats.put(slot, item));
        }

        return sortedPresetItemStats;
    }
}
