package com.example.mapleMyItemOption.domain.item;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.ClassMainStat;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItem;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PresetTotalStatAnalyzer extends ItemAnalyzer{

    /**
     * 각각 프리셋 별로 잠재능력 옵션들의 수치를 구하는 메소드
     * @param myItemEquipment 조회하는 캐릭터의 장비
     * @param character 조회하는 캐릭터 정보
     * @param additional 에디셔널을 조회하는지
     * @param dataOption 평균 / 총합 / 개수
     * @return 프리셋 별 잠재능력과 수치 List<Map<옵션, 수치>>
     */
    public List<Map<String, Float>> getPresetPotentialValues(MyItemEquipment myItemEquipment, Character character, boolean additional, PotentialValuesOption dataOption){
        List<Map<String, Float>> presetPotentialValues = new ArrayList<>();

        List<List<MyItem>> presets = getPresets(myItemEquipment);

        for (List<MyItem> preset : presets) {
            presetPotentialValues.add(getPresetPotentialValue(preset, character, additional, dataOption));
        }

        return presetPotentialValues;
    }

    /**
     * 하나의 프리셋에서 잠재능력 별 수치 총합 구하는 함수
     * @param preset MyItemEquipment 의 프리셋 (List<MyItem>)
     * @param character 조회하는 캐릭터 정보
     * @param additional 에디셔널을 조회하는지
     * @param dataOption 평균 / 총합 / 개수
     * @return Map<옵션종류, 수치>
     */
    public Map<String, Float> getPresetPotentialValue(List<MyItem> preset, Character character, boolean additional, PotentialValuesOption dataOption) {

        Map<String, Float> presetPotentialValue = new HashMap<>(); // 옵션별 수치 총합
                                                                   // {"STR%" : 150, "공격력%" : 90}
        Map<String, Float> presetPotentialItems = new HashMap<>(); // 해당 옵션이 있는 아이템 개수
                                                                     // {"STR%" : 10, "공격력%" : 3}
        Map<String, Float> presetPotentialLines = new HashMap<>(); // 해당 옵션이 몇 줄인지
                                                                     // {"공격력%" : 9}

        for (MyItem myItem : preset) {

            Map<String, Float> potentialValue = getPotentialValue(myItem, character, additional, false);
            Map<String, Float> potentialLine = getPotentialValue(myItem, character, additional, true);

            if (potentialValue == null || potentialLine == null){
                continue;
            }

            // 옵션 수치 총합, 아이템 개수 업데이트
            for (Map.Entry<String, Float> potential : potentialValue.entrySet()) {
                String option = potential.getKey();
                Float value = potential.getValue();

                // ex) 드랍 옵션 수치 총합 200%
                presetPotentialValue.putIfAbsent(option, 0F);
                presetPotentialValue.computeIfPresent(option, (k, v) -> v + value);

                // ex) 드랍 옵션 보유 아이템 5개
                presetPotentialItems.putIfAbsent(option, 0F);
                presetPotentialItems.computeIfPresent(option, (k, v) -> v + 1);
            }
            // key 의 옵션의 개수 ex) 드랍 10줄
            for (Map.Entry<String, Float> potential : potentialLine.entrySet()) {
                String option = potential.getKey();
                Float value = potential.getValue();

                presetPotentialLines.putIfAbsent(option, 0F);
                presetPotentialLines.computeIfPresent(option, (k, v) -> v + value);
            }
        } // 한 개의 프리셋 내의 아이템 분석 완료


        // 순서 정렬
        Map<String, Float> orderedPresetAveragePotentialValue = new LinkedHashMap<>();

        for(String option : PotentialOption.AVERAGE_LIST_SHORTEN){
            Float totalValue = presetPotentialValue.getOrDefault(option, 0F);
            Float itemCount = presetPotentialItems.getOrDefault(option, 0F);

            float average = totalValue / itemCount;
            Float roundedAvg = Math.round(average * 10) / 10F;

            if(itemCount != 0) {
                orderedPresetAveragePotentialValue.put(option, roundedAvg);
            }
        }

        // 순서 정렬
        Map<String, Float> orderedPresetPotentialLines = new LinkedHashMap<>();
        Map<String, Float> orderedPresetPotentialValue = new LinkedHashMap<>();

        for(String option : PotentialOption.TOTAL_LIST_SHORTEN) {
            Float totalLines = presetPotentialLines.getOrDefault(option, 0F);
            Float totalValue = presetPotentialValue.getOrDefault(option, 0F);
            if(totalLines != 0){
                orderedPresetPotentialLines.put(option, totalLines);
                orderedPresetPotentialValue.put(option, totalValue);
            }
        }

        switch (dataOption){
            case AVERAGE -> {
                if(orderedPresetAveragePotentialValue.isEmpty()){
                    return null;
                }
                return orderedPresetAveragePotentialValue;
            }
            case LINES -> {
                if(orderedPresetPotentialLines.isEmpty()){
                    return null;
                }
                return orderedPresetPotentialLines;
            }
            case TOTAL -> {
                if(orderedPresetPotentialValue.isEmpty()){
                    return null;
                }
                return orderedPresetPotentialValue;
            }
        }
        return orderedPresetAveragePotentialValue;
    }




    /**
     * 프리셋 별 잠재능력 등급 개수 확인
     * @param myItemEquipment 캐릭터 장비 총 목록
     * @param additional true 일 경우 에디 / false 일 경우 윗잠
     * @return 각 프리셋 별 Map 을 담는 List
     */
    public List<Map<String, Integer>> getPresetPotentialGradeCount (MyItemEquipment myItemEquipment, boolean additional) {
        List<Map<String, Integer>> presetPotentialGradeCounts = new ArrayList<>();

        List<List<MyItem>> presets = getPresets(myItemEquipment);

        for (List<MyItem> preset : presets) {
            presetPotentialGradeCounts.add(countPotentialGrade(preset, additional));
        }

        return presetPotentialGradeCounts;
    }

    /**
     * 하나의 프리셋에서 잠재능력 등급 별 개수 확인
     * @param preset 하나의 프리셋
     * @param additional true 일 경우 에디 / false 일 경우 윗잠
     * @return 등급 별 갯수 Map
     */
    public Map<String, Integer> countPotentialGrade(List<MyItem> preset, boolean additional) {
        Map<String, Integer> gradeCount = new LinkedHashMap<>();

        // 잠재능력 옵션 등급의 순서 고정을 위함
        for(String grade : PotentialOption.GRADE_LIST){
            gradeCount.put(grade, 0);
        }

        for(MyItem myItem : preset) { // 잠재능력 등급 카운트

            String potentialOptionGrade = additional ? myItem.getAdditionalPotentialOptionGrade() : myItem.getPotentialOptionGrade();

            if(potentialOptionGrade == null) {
                continue;
            }
            switch (potentialOptionGrade){
                case PotentialOption.LEGENDARY ->
                    gradeCount.computeIfPresent(PotentialOption.LEGENDARY, (k, v) -> v + 1);

                case PotentialOption.UNIQUE ->
                    gradeCount.computeIfPresent(PotentialOption.UNIQUE, (k, v) -> v + 1);

                case PotentialOption.EPIC ->
                    gradeCount.computeIfPresent(PotentialOption.EPIC, (k, v) -> v + 1);

                case PotentialOption.RARE ->
                    gradeCount.computeIfPresent(PotentialOption.RARE, (k, v) -> v + 1);

            }
        }

        if (gradeCount.isEmpty()){
            return null;
        }

        return gradeCount;
    }



    /**
     * 프리셋 별 아이템 평균 추옵 계산
     * @param myItemEquipment 캐릭터 장비 정보
     * @param character 캐릭터 기본 정보(주스탯 확보용)
     * @return 각 프리셋 별 아이템 평균 추가옵션
     */
    public List<Map<Integer, Float>> getPresetAverageAddOption(MyItemEquipment myItemEquipment, Character character){
        String mainStat = getMainStat(character);

        List<List<MyItem>> presets = getPresets(myItemEquipment);

        List<Map<Integer, Float>> presetLevelAverageAddOption = new ArrayList<>();

        for(List<MyItem> preset : presets){
            presetLevelAverageAddOption.add(getAverageAddOption(preset, mainStat));
        }

        return presetLevelAverageAddOption;
    }

    /**
     * 하나의 프리셋에서 평균 추가옵션 수치
     * @param preset 프리셋 내의 아이템 리스트
     * @param mainStat 캐릭터 주스탯
     * @return 장비 렙제 별 평균 추가 옵션
     */
    public Map<Integer, Float> getAverageAddOption(List<MyItem> preset, String mainStat){

        Map<Integer, Float> levelTotalAddOptionStat = new HashMap<>(); // 레벨대 별 추가옵션 총합
        Map<Integer, Float> levelTotalAddOptionItems = new HashMap<>(); // 레벨대 별 아이템 개수
        Map<Integer, Float> levelAverageAddOptionStat = new HashMap<>(); // 레벨대 별 평균 추가옵션, 순서 정렬 X


        for(MyItem myItem : preset){

            if(myItem.getItemEquipmentSlot().equals("무기")){
                continue;
            }

            MyItemOption itemAddOption = myItem.getItemAddOption();

            if(itemAddOption.getStr() == 0 && itemAddOption.getDex() == 0 && itemAddOption.getLuk() == 0 && itemAddOption.getIntel() == 0 && itemAddOption.getMaxHp() == 0){
                // 추가옵션이 없는 아이템 제외 (견장)
                continue;
            }

            // 아이템 추가 옵션, 레벨대
            int itemAddOptionStats = getAddOptionStat(itemAddOption, mainStat);

            int equipmentLevelCategory = myItem.getItemBaseOption().getBaseEquipmentLevel() / 10 * 10;

            // 레벨대 별로 추가옵션 총합, 카운트
            float currentLevelTotalAddOptionStat = levelTotalAddOptionStat.getOrDefault(equipmentLevelCategory, 0F);
            float currentLevelTotalAddOptionItems = levelTotalAddOptionItems.getOrDefault(equipmentLevelCategory, 0F);

            levelTotalAddOptionStat.put(equipmentLevelCategory, currentLevelTotalAddOptionStat + itemAddOptionStats);
            levelTotalAddOptionItems.put(equipmentLevelCategory, currentLevelTotalAddOptionItems + 1);
        }

        // 평균 추가옵션 계산
        for (Integer level : levelTotalAddOptionStat.keySet()) {
            Float totalStat = levelTotalAddOptionStat.get(level);
            Float itemCounts = levelTotalAddOptionItems.get(level);

            float average = totalStat / itemCounts;
            Float roundedAvg = Math.round(average * 10) / 10F;

            levelAverageAddOptionStat.put(level, roundedAvg);

        }

        // 레벨대 순서 정렬
        Map<Integer, Float> orderedLevelAverageAddOptionStat = new LinkedHashMap<>();
        List<Integer> levelList = levelAverageAddOptionStat.keySet().stream().sorted().toList();
        for(Integer level : levelList) {
            Float addOption = levelAverageAddOptionStat.get(level);
            if(addOption == 0F){
                continue;
            }
            orderedLevelAverageAddOptionStat.put(level, levelAverageAddOptionStat.get(level));
        }

        return orderedLevelAverageAddOptionStat;
    }

    /**
     * 하나의 아이템의 유효 추가옵션 계산
     * 올스탯 = 10 * 주스탯 효율
     * 공/마 = 4 * 주스탯 효율
     * @param itemAddOption 아이템의 추가옵션 객체
     * @param mainStat 조회하는 캐릭터의 주스탯
     * @return 아이템의 유효 추가옵션 수치
     */
    private Integer getAddOptionStat(MyItemOption itemAddOption, String mainStat){
        int mainStatPoint = 0;
        int power;
        int allStat;

        int itemAddOptionStats;

        switch (mainStat) {
            case ClassMainStat.STR -> mainStatPoint = itemAddOption.getStr();
            case ClassMainStat.DEX -> mainStatPoint = itemAddOption.getDex();
            case ClassMainStat.LUK -> mainStatPoint = itemAddOption.getLuk();
            case ClassMainStat.INT -> mainStatPoint = itemAddOption.getIntel();
            case ClassMainStat.HP -> mainStatPoint = itemAddOption.getMaxHp();
        }

        if(mainStat.equals("INT")){
            power = itemAddOption.getMagicPower();
        } else {
            power = itemAddOption.getAttackPower();
        }

        allStat = itemAddOption.getAllStat();

        itemAddOptionStats = mainStat.equals(ClassMainStat.HP) ? mainStatPoint : mainStatPoint + allStat * 10 + power * 4;

        return itemAddOptionStats;
    }

    public List<Map<String, List<Float>>> getPresetAverageEtcOption(MyItemEquipment myItemEquipment, Character character){

        List<Map<String, List<Float>>> presetAverageEtcOptionCategory = new ArrayList<>();

        String mainStat = getMainStat(character);
        List<List<MyItem>> presets = getPresets(myItemEquipment);

        for(List<MyItem> preset : presets) {
            presetAverageEtcOptionCategory.add(getAverageEtcOption(preset, mainStat));
        }

        return presetAverageEtcOptionCategory;
    }

    public Map<String, List<Float>> getAverageEtcOption(List<MyItem> preset, String mainStat) {

        Map<String, float[]> averageEtcOptionTotal = new HashMap<>(); // 장비 카테고리 별 주문서 옵션 합

        for(String key : ItemSlot.SLOT_CATEGORY){ // ["무보엠", "방어구", "장신구", "기타 장비"]
            averageEtcOptionTotal.put(key, new float[]{0, 0});
        }

        Map<String, Integer> averageEtcOptionCount = new HashMap<>(); // 장비 카테고리 별 주문서 업그레이드 횟수

        for (MyItem myItem : preset){
            // 주문서 적용하지 않은 아이템 제외
            if(myItem.getScrollUpgrade() == 0){
                continue;
            }

            MyItemOption itemEtcOption = myItem.getItemEtcOption();

            String itemEquipmentSlot = myItem.getItemEquipmentSlot();

            String itemCategory;
            if(ItemSlot.WEAPONS.contains(itemEquipmentSlot)){
                itemCategory = ItemSlot.CATEGORY_WEAPONS;
            } else if (ItemSlot.ARMORS.contains(itemEquipmentSlot)) {
                itemCategory = ItemSlot.CATEGORY_ARMORS;
            } else if (ItemSlot.ACCESSORIES.contains(itemEquipmentSlot)) {
                itemCategory = ItemSlot.CATEGORY_ACCESSORIES;
            } else {
                itemCategory = ItemSlot.CATEGORY_OTHERS;
            }

            List<Integer> starforceScrollValue = getStarforceScrollValue(myItem);

            int etcStatOption;
            switch (mainStat){
                case ClassMainStat.STR -> etcStatOption = itemEtcOption.getStr();
                case ClassMainStat.DEX -> etcStatOption = itemEtcOption.getDex();
                case ClassMainStat.INT -> etcStatOption = itemEtcOption.getIntel();
                case ClassMainStat.LUK -> etcStatOption = itemEtcOption.getLuk();
                case ClassMainStat.HP -> etcStatOption = itemEtcOption.getMaxHp();
                default -> etcStatOption = 0;
            }

            int etcOption = (etcStatOption - starforceScrollValue.get(1) < 0) ? etcStatOption : etcStatOption - starforceScrollValue.get(1);
            averageEtcOptionTotal.get(itemCategory)[1] += etcOption;

            if(mainStat.equals("INT")){
                averageEtcOptionTotal.get(itemCategory)[0] += itemEtcOption.getMagicPower() - starforceScrollValue.get(0);
            } else {
                averageEtcOptionTotal.get(itemCategory)[0] += itemEtcOption.getAttackPower() - starforceScrollValue.get(0);
            }

            averageEtcOptionCount.putIfAbsent(itemCategory, 0);
            averageEtcOptionCount.computeIfPresent(itemCategory, (k, v) -> v + myItem.getScrollUpgrade());
        }

        Map<String, List<Float>> result = new LinkedHashMap<>();

        for(String key : ItemSlot.SLOT_CATEGORY){
            int count = Optional.ofNullable(averageEtcOptionCount.get(key)).orElse(0);

            if (count == 0) {
                return null;
                //result.put(key, new ArrayList<>(List.of(0F, 0F)));
            } else {
                float avgPower = Math.round((averageEtcOptionTotal.get(key)[0] / count) * 10) / 10F;
                float avgStat = Math.round((averageEtcOptionTotal.get(key)[1] / count) * 10) / 10F;

                result.put(key, new ArrayList<>(List.of(avgPower, avgStat)));
            }
        }

        return result;
    }


    /**
     * 프리셋 별 아이템 평균 스타포스 별 개수
     * @param myItemEquipment 캐릭터 장비 정보
     * @return 프리셋 별 평균 별 개수 ex) [21, 18, 21]
     */
    public List<Float> getPresetAverageStarforce(MyItemEquipment myItemEquipment) {

        List<List<MyItem>> presets = getPresets(myItemEquipment);

        List<Float> presetAverageStarforce = new ArrayList<>();

        for(List<MyItem> preset : presets){
            presetAverageStarforce.add(getAverageStarforce(preset));
        }

        return presetAverageStarforce;
    }

    /**
     * 프리셋 별 아이템 최대 스타포스 별 개수
     * @param myItemEquipment 캐릭터 장비 정보
     * @return 프리셋 별 최대 별 개수 ex) [22, 22, 22]
     */
    public List<Integer> getPresetMaximumStarforce (MyItemEquipment myItemEquipment) {
        List<List<MyItem>> presets = getPresets(myItemEquipment);

        List<Integer> presetMaximumStarforce = new ArrayList<>();

        for(List<MyItem> preset : presets) {
            presetMaximumStarforce.add(getMaximumStarforce(preset));
        }

        return presetMaximumStarforce;
    }

    /**
     * 프리셋 별 아이템 최소 스타포스 별 개수
     * @param myItemEquipment 캐릭터 장비 정보
     * @return 프리셋 별 최소 별 개수 ex) [18, 15, 18]
     */
    public List<Integer> getPresetMinimumStarforce (MyItemEquipment myItemEquipment) {
        List<List<MyItem>> presets = getPresets(myItemEquipment);

        List<Integer> presetMinimumStarforce = new ArrayList<>();

        for(List<MyItem> preset : presets) {
            presetMinimumStarforce.add(getMinimumStarforce(preset));
        }

        return presetMinimumStarforce;
    }

    /**
     * 하나의 프리셋에서 평균 스타포스 별 개수
     * @param preset 캐릭터의 프리셋 (List<MyItem>)
     * @return 평균 스타포스 별 개수
     */
    public Float getAverageStarforce(List<MyItem> preset){

        float starforceItems = 0;
        float totalStarforce = 0L;
        for (MyItem myItem : preset) {
            if(myItem.getItemEquipmentPart().equals("기계 심장")){
                continue;
            }

            Integer starforce = myItem.getStarforce();
            if(starforce != 0){

                if(myItem.getItemName().contains("타일런트") || myItem.getStarforceScrollFlag().equals("사용")){
                    starforce += 10;
                }

                totalStarforce += starforce;
                starforceItems++;
            }
        }


        if(starforceItems == 0F){
            return 0F;
        }

        return Math.round(totalStarforce/starforceItems * 10) / 10F;
    }

    /**
     * 하나의 프리셋에서 최대 스타포스 별 개수
     * @param preset 캐릭터의 프리셋 (List<MyItem>)
     * @return 최대 스타포스 별 개수
     */
    public Integer getMaximumStarforce(List<MyItem> preset){
        int maxStarforce = 0;
        for (MyItem myItem : preset) {
            if(myItem.getItemEquipmentPart().equals("기계 심장")){
                continue;
            }

            int starforce = myItem.getStarforce();
            if(myItem.getItemName().contains("타일런트") || myItem.getStarforceScrollFlag().equals("사용")){
                starforce += 10;
            }

            if(starforce > maxStarforce){
                maxStarforce = starforce;
            }
        }
        return maxStarforce;
    }

    /**
     * 하나의 프리셋에서 최소 스타포스 별 개수 (기계심장 제외)
     * @param preset 캐릭터의 프리셋 (List<MyItem>)
     * @return 최소 스타포스 별 개수
     */
    public Integer getMinimumStarforce(List<MyItem> preset) {
        int minStarforce = 25;
        for (MyItem myItem : preset) {
            if(myItem.getItemEquipmentPart().equals("기계 심장")){
                continue;
            }

            int starforce = myItem.getStarforce();
            if(myItem.getItemName().contains("타일런트") || myItem.getStarforceScrollFlag().equals("사용")){
                starforce += 10;
            }

            if(starforce != 0 && starforce < minStarforce){
                minStarforce = starforce;
            }
        }

        return minStarforce;
    }

    private List<List<MyItem>> getPresets(MyItemEquipment myItemEquipment) {
        List<List<MyItem>> presets = new ArrayList<>();
        presets.add(myItemEquipment.getPreset1());
        presets.add(myItemEquipment.getPreset2());
        presets.add(myItemEquipment.getPreset3());
        return presets;
    }
}
