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

    /**
     * 프리셋 별 종합 옵션 정보 (스타포스, 추가옵션, 잠재, 에디셔널, 주문서)
     * @param myItemEquipment MyItemEquipment 캐릭터 장비 정보
     * @param character Character 캐릭터 기본 정보
     * @return
     */
    public List<PresetTotalStat> getPresetTotalStats (MyItemEquipment myItemEquipment, Character character){
        List<PresetTotalStat> presetTotalStats = new ArrayList<>();

        List<Float> presetAverageStarforce = presetTotalStatAnalyzer.getPresetAverageStarforce(myItemEquipment);
        List<Integer> presetMaximumStarforce = presetTotalStatAnalyzer.getPresetMaximumStarforce(myItemEquipment);
        List<Integer> presetMinimumStarforce = presetTotalStatAnalyzer.getPresetMinimumStarforce(myItemEquipment);

        List<Map<Integer, Float>> presetAverageAddOption = presetTotalStatAnalyzer.getPresetAverageAddOption(myItemEquipment, character);

        List<Map<String, List<Float>>> presetAverageEtcOption = presetTotalStatAnalyzer.getPresetAverageEtcOption(myItemEquipment, character);

        List<Map<String, Integer>> presetPotentialGradeCount = presetTotalStatAnalyzer.getPresetPotentialGradeCount(myItemEquipment, false);
        List<Map<String, Integer>> presetAdditionalPotentialGradeCount = presetTotalStatAnalyzer.getPresetPotentialGradeCount(myItemEquipment, true);

        List<Map<String, Float>> presetAveragePotentialValues = presetTotalStatAnalyzer.getPresetPotentialValues(myItemEquipment, character, false, PotentialValuesOption.AVERAGE);
        List<Map<String, Float>> presetAverageAdditionalPotentialValues = presetTotalStatAnalyzer.getPresetPotentialValues(myItemEquipment, character, true, PotentialValuesOption.AVERAGE);

        List<Map<String, Float>> presetTotalPotentialValues = presetTotalStatAnalyzer.getPresetPotentialValues(myItemEquipment, character, false, PotentialValuesOption.TOTAL);
        List<Map<String, Float>> presetTotalAdditionalPotentialValues = presetTotalStatAnalyzer.getPresetPotentialValues(myItemEquipment, character, true, PotentialValuesOption.TOTAL);

        List<Map<String, Float>> presetPotentialLines = presetTotalStatAnalyzer.getPresetPotentialValues(myItemEquipment, character, false, PotentialValuesOption.LINES);
        List<Map<String, Float>> presetAdditionalPotentialLines = presetTotalStatAnalyzer.getPresetPotentialValues(myItemEquipment, character, true, PotentialValuesOption.LINES);


        // 불러온 정보 프리셋 별로 넣기
        for(int i = 0; i < 3; i++){
            PresetTotalStat presetTotalStat = new PresetTotalStat();
            presetTotalStat.setAverageStarforce(presetAverageStarforce.get(i)); // 평균 스타포스
            presetTotalStat.setMaximumStarforce(presetMaximumStarforce.get(i)); // 최대 스타포스
            presetTotalStat.setMinimumStarforce(presetMinimumStarforce.get(i)); // 최소 스타포스

            presetTotalStat.setAverageAddOption(presetAverageAddOption.get(i)); // 추옵

            presetTotalStat.setAverageEtcOption(presetAverageEtcOption.get(i)); // 주문서 공/주스탯

            presetTotalStat.setPotentialGradeCount(presetPotentialGradeCount.get(i)); // 잠재 등급 개수
            presetTotalStat.setAdditionalPotentialGradeCount(presetAdditionalPotentialGradeCount.get(i)); // 에디 등급 개수

            presetTotalStat.setAveragePotentialValue(presetAveragePotentialValues.get(i)); // 잠재 평균 수치
            presetTotalStat.setAverageAdditionalPotentialValue(presetAverageAdditionalPotentialValues.get(i)); // 에디 평균 수치

            presetTotalStat.setTotalPotentialValue(presetTotalPotentialValues.get(i)); // 잠재 총합 수치
            presetTotalStat.setTotalAdditionalPotentialValue(presetTotalAdditionalPotentialValues.get(i));

            presetTotalStat.setPotentialOptionLines(presetPotentialLines.get(i)); // 잠재 옵션 개수
            presetTotalStat.setAdditionalPotentialOptionLines(presetAdditionalPotentialLines.get(i)); // 에디 옵션 개수

            presetTotalStats.add(presetTotalStat);
        }

        return presetTotalStats;
    }

    public Map<String, Item> getPresetItemStats (MyItemEquipment myItemEquipment, Character character, Integer presetNum, List<String> itemCategory){

        List<MyItem> preset;

        switch (presetNum){
            case 2 -> preset = myItemEquipment.getPreset2();
            case 3 -> preset = myItemEquipment.getPreset3();
            default -> preset = myItemEquipment.getPreset1();
        }

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
