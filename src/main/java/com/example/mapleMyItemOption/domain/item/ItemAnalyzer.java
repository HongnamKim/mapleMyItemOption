package com.example.mapleMyItemOption.domain.item;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.ClassMainStat;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItem;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemOption;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ItemAnalyzer {

    /**
     * 각각 프리셋 별로 잠재능력 옵션들의 수치를 구하는 메소드
     * @param myItemEquipment 조회하는 캐릭터의 장비
     * @param character 조회하는 캐릭터 정보
     * @param additional 에디셔널을 조회하는지
     * @param specificStat 주스탯% 와 올스탯% 를 분리하여 데이터를 얻을지
     * @param dataOption 평균 / 총합 / 개수
     * @return 프리셋 별 잠재능력과 수치 List<Map<옵션, 수치>>
     */
    public List<Map<String, Float>> getPresetPotentialValues(MyItemEquipment myItemEquipment, Character character, boolean additional, boolean specificStat, PotentialValuesOption dataOption){
        List<Map<String, Float>> presetPotentialValues = new ArrayList<>();

        List<List<MyItem>> presets = getPresets(myItemEquipment);

        String mainStat = getMainStat(character);

        for (List<MyItem> preset : presets) {
            presetPotentialValues.add(getPresetPotentialValue(preset, mainStat, additional, specificStat, dataOption));
        }

        return presetPotentialValues;
    }

    /**
     * 하나의 프리셋에서 잠재능력 별 수치 총합 구하는 함수
     * @param preset MyItemEquipment 의 프리셋 (List<MyItem>)
     * @param mainStat 조회하는 캐릭터의 주스탯
     * @param additional 에디셔널을 조회하는지
     * @param specificStat 주스탯% 와 올스탯% 를 분리하여 데이터를 얻을지
     * @param dataOption 평균 / 총합 / 개수
     * @return Map<옵션종류, 수치>
     */
    private Map<String, Float> getPresetPotentialValue(List<MyItem> preset, String mainStat, boolean additional, boolean specificStat, PotentialValuesOption dataOption) {
        // mainStat == "STR", "DEX", "INT", "LUK", "HP", "올스탯"
        Map<String, Float> presetPotentialValue = new HashMap<>(); // 옵션별 수치 총합
                                                                   // {"STR%" : 150, "공격력%" : 90}
        Map<String, Float> presetPotentialItems = new HashMap<>(); // 해당 옵션이 있는 아이템 개수
                                                                     // {"STR%" : 10, "공격력%" : 3}
        Map<String, Float> presetPotentialLines = new HashMap<>(); // 해당 옵션이 몇 줄인지
                                                                     // {"공격력%" : 9}

        for (MyItem myItem : preset) {

            List<String> potentialOptions = new ArrayList<>();
            String potentialOption1; // 윗잠 첫째줄
            String potentialOption2; // 윗잠 둘째줄
            String potentialOption3; // 윗잠 셋째줄

            if(additional) {
                potentialOption1 = Optional.ofNullable(myItem.getAdditionalPotentialOption_1()).orElse("");
                potentialOption2 = Optional.ofNullable(myItem.getAdditionalPotentialOption_2()).orElse("");
                potentialOption3 = Optional.ofNullable(myItem.getAdditionalPotentialOption_3()).orElse("");

            }else{
                potentialOption1 = Optional.ofNullable(myItem.getPotentialOption_1()).orElse("");
                potentialOption2 = Optional.ofNullable(myItem.getPotentialOption_2()).orElse("");
                potentialOption3 = Optional.ofNullable(myItem.getPotentialOption_3()).orElse("");

            }
            potentialOptions.add(potentialOption1);
            potentialOptions.add(potentialOption2);
            potentialOptions.add(potentialOption3);

            Set<String> potentialOptionCategory = new HashSet<>(); // 하나의 아이템이 갖고 있는 옵션 종류

            // 윗잠 1,2,3 번째 줄마다 순회하면서 수치 계산
            for(String potentialOption : potentialOptions){
                if(potentialOption.length() == 0) { // 잠재 없을 경우 생략
                    continue;
                }

                String mainStatPercent = mainStat+"%";
                String power = mainStat.equals(ClassMainStat.INT) ? PotentialOption.MAGIC_POWER : PotentialOption.ATTACK_POWER;
                String perLevelStat = PotentialOption.PER_LEVEL + " " + mainStat;

                if((potentialOption.startsWith(mainStat) || potentialOption.startsWith(PotentialOption.ALL_STAT)
                        && potentialOption.endsWith("%"))){ // 주스탯, 올스탯 퍼센트 옵션

                    potentialOptionCategory.add(PotentialOption.TOTAL_STAT_PERCENT); // 옵션 종류 추가
                    // "STR : +4%" or "올스탯 : +6%"
                    String optionValueString;
                    Float optionValue = 0F;

                    if(potentialOption.contains(mainStat)) { // 주스탯

                        optionValueString = potentialOption.replace(mainStat + " : +", "").replace("%", "");
                        optionValue = Float.parseFloat(optionValueString);

                        if(specificStat){
                            potentialOptionCategory.add(mainStatPercent); // 옵션 종류 추가
                            Float currentOptionValue = presetPotentialValue.getOrDefault(mainStatPercent, 0F);
                            presetPotentialValue.put(mainStatPercent, currentOptionValue + optionValue);

                            Float currentOptionLine = presetPotentialLines.getOrDefault(mainStatPercent, 0F);
                            presetPotentialLines.put(mainStatPercent, currentOptionLine + 1);
                        }

                    } else if (!mainStat.equals(ClassMainStat.HP)){ // 올스탯, 데벤일 때는 제외


                        optionValueString = potentialOption.replace(PotentialOption.ALL_STAT + " : +", "").replace("%", "");
                        optionValue = Float.parseFloat(optionValueString);

                        if(specificStat) {
                            potentialOptionCategory.add(PotentialOption.ALL_STAT_PERCENT);
                            Float currentOptionValue = presetPotentialValue.getOrDefault(PotentialOption.ALL_STAT_PERCENT, 0F);
                            presetPotentialValue.put(PotentialOption.ALL_STAT_PERCENT, currentOptionValue + optionValue);

                            Float currentOptionLine = presetPotentialLines.getOrDefault(PotentialOption.ALL_STAT_PERCENT, 0F);
                            presetPotentialLines.put(PotentialOption.ALL_STAT_PERCENT, currentOptionLine + 1);
                        }

                        // 총 스탯 계산용
                        optionValue = optionValue * 1.12F;
                    }
                    Float currentOptionValue = presetPotentialValue.getOrDefault(PotentialOption.TOTAL_STAT_PERCENT, 0F);

                    presetPotentialValue.put(PotentialOption.TOTAL_STAT_PERCENT, currentOptionValue + optionValue);

                    Float currentOptionLine = presetPotentialLines.getOrDefault(PotentialOption.TOTAL_STAT_PERCENT, 0F);
                    presetPotentialLines.put(PotentialOption.TOTAL_STAT_PERCENT, currentOptionLine + 1);

                } else if (potentialOption.startsWith(perLevelStat)) { // 렙당 주스탯
                    String mapKey = PotentialOption.PER_LEVEL + " 주스탯";

                    potentialOptionCategory.add(mapKey);
                    //"캐릭터 기준 9레벨 당 STR : +1"
                    String optionValueString = potentialOption.replace(perLevelStat + " : +", "");
                    Float optionValue = Float.parseFloat(optionValueString);

                    Float currentOptionValue = presetPotentialValue.getOrDefault(mapKey, 0F);
                    presetPotentialValue.put(mapKey, currentOptionValue + optionValue);

                    Float currentOptionLine = presetPotentialLines.getOrDefault(mapKey, 0F);
                    presetPotentialLines.put(mapKey, currentOptionLine + 1);

                } else { // 주스탯, 올스탯 제외 옵션 수치 파악
                    for (String option : PotentialOption.OPTION_LIST){

                        if(potentialOption.startsWith(option)) {
                            if(option.equals(PotentialOption.ATTACK_POWER) || option.equals(PotentialOption.MAGIC_POWER)){
                                // 직업에 따라 공격력 or 마력만 집계
                                if(!power.equals(option)){
                                    continue;
                                }
                            }

                            String optionCategory = potentialOption.endsWith("%") ? option + "%" : option; // % 옵션일 경우 뒤에 % 추가

                            potentialOptionCategory.add(optionCategory);

                            // 퍼센트 옵션일 경우 뒤에 % 제거
                            String optionValueString = potentialOption.endsWith("%") ?
                                    potentialOption.replace(option + " : +", "").replace("%", "") :
                                    potentialOption.replace(option + " : +", "");

                            Float optionValue = Float.parseFloat(optionValueString);

                            Float currentOptionValue = presetPotentialValue.getOrDefault(optionCategory, 0F);
                            presetPotentialValue.put(optionCategory, currentOptionValue + optionValue);

                            Float currentOptionLine = presetPotentialLines.getOrDefault(optionCategory, 0F);
                            presetPotentialLines.put(optionCategory, currentOptionLine + 1);

                        }
                    }
                }
            }

            // ex) 템이 크힘올이면 각각 개수 1개씩 추가
            for(String option : potentialOptionCategory) {
                Float currentItemCount = presetPotentialItems.getOrDefault(option, 0F);
                presetPotentialItems.put(option, currentItemCount + 1);
            }
        }




        // 순서 정렬
        Map<String, Float> presetAveragePotentialValue = new LinkedHashMap<>();
        for(String option : PotentialOption.AVERAGE_LIST){
            Float totalValue = presetPotentialValue.getOrDefault(option, 0F);
            Float itemCount = presetPotentialItems.getOrDefault(option, 0F);

            if(totalValue != 0 && itemCount != 0) {
                presetAveragePotentialValue.put(option, Float.parseFloat(String.format("%.1f", totalValue / itemCount)));
            }
        }
        // 순서 정렬
        Map<String, Float> orderedPresetPotentialLines = new LinkedHashMap<>();
        Map<String, Float> orderedPresetPotentialValue = new LinkedHashMap<>();
        for(String option : PotentialOption.TOTAL_LIST) {
            Float totalLines = presetPotentialLines.getOrDefault(option, 0F);
            Float totalValue = presetPotentialValue.getOrDefault(option, 0F);
            if(totalLines != 0){
                orderedPresetPotentialLines.put(option, totalLines);
                orderedPresetPotentialValue.put(option, totalValue);
            }
        }

        switch (dataOption){
            case AVERAGE -> {return presetAveragePotentialValue;}
            case LINES -> {return orderedPresetPotentialLines;}
            case TOTAL -> {return orderedPresetPotentialValue;}
        }
        return presetAveragePotentialValue;
    }




    /**
     * 프리셋 별 잠재능력 등급 개수 확인
     * @param myItemEquipment
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
    private Map<String, Integer> countPotentialGrade(List<MyItem> preset, boolean additional) {
        Map<String, Integer> gradeCount = new LinkedHashMap<>();

        for(String grade : PotentialOption.GRADE_LIST){
            gradeCount.put(grade, 0);
        }

        for(MyItem myItem : preset) { // 잠재능력 등급 카운트

            String potentialOptionGrade = additional ? myItem.getAdditionalPotentialOptionGrade() : myItem.getPotentialOptionGrade();
            //updatePotentialGradeMap(gradeCount, potentialOptionGrade);
            if(potentialOptionGrade == null) {
                continue;
            }
            switch (potentialOptionGrade){
                case PotentialOption.LEGENDARY -> {
                    int currentCount = gradeCount.get(PotentialOption.LEGENDARY);
                    gradeCount.put(PotentialOption.LEGENDARY, currentCount + 1);
                }
                case PotentialOption.UNIQUE -> {
                    int currentCount = gradeCount.get(PotentialOption.UNIQUE);
                    gradeCount.put(PotentialOption.UNIQUE, currentCount + 1);
                }
                case PotentialOption.EPIC -> {
                    int currentCount = gradeCount.get(PotentialOption.EPIC);
                    gradeCount.put(PotentialOption.EPIC, currentCount + 1);
                }
                case PotentialOption.RARE -> {
                    int currentCount = gradeCount.get(PotentialOption.RARE);
                    gradeCount.put(PotentialOption.RARE, currentCount + 1);
                }
            }
        }

        // 카운트 0 인 등급 제외하여 반환
        Map<String, Integer> resultGradeCount = new LinkedHashMap<>();

        for (String grade : gradeCount.keySet()){
            if(gradeCount.get(grade) != 0){
                resultGradeCount.put(grade, gradeCount.get(grade));
            }
        }

        return resultGradeCount;
    }



    /**
     * 프리셋 별 아이템 평균 추옵 계산
     * @param myItemEquipment 캐릭터 장비 정보
     * @param character 캐릭터 기본 정보(주스탯 확보용)
     * @return
     */
    public List<Map<Integer, Float>> getPresetAverageAddOption(MyItemEquipment myItemEquipment, Character character){
        String mainStat = getMainStat(character);

        List<List<MyItem>> presets = getPresets(myItemEquipment);

        //List<Float> presetAverageAddOption = new ArrayList<>();
        List<Map<Integer, Float>> presetLevelAverageAddOption = new ArrayList<>();

        for(List<MyItem> preset : presets){
            presetLevelAverageAddOption.add(getAverageAddOption(preset, mainStat));
        }

        return presetLevelAverageAddOption;
    }

    /**
     * 하나의 프리셋에서 평균 추가옵션 수치
     * @param preset
     * @param mainStat
     * @return
     */
    private Map<Integer, Float> getAverageAddOption(List<MyItem> preset, String mainStat){

        Map<Integer, Float> levelTotalAddOptionStat = new HashMap<>(); // 레벨대 별 추가옵션 총합
        Map<Integer, Float> levelTotalAddOptionItems = new HashMap<>(); // 레벨대 별 아이템 개수
        Map<Integer, Float> levelAverageAddOptionStat = new HashMap<>(); // 레벨대 별 평균 추가옵션, 순서 정렬 X


        for(MyItem myItem : preset){

            if(myItem.getItemEquipmentSlot().equals("무기")){ // 무기 제외
                continue;
            }

            MyItemOption itemAddOption = myItem.getItemAddOption();

            if(itemAddOption.getStr() == 0 && itemAddOption.getDex() == 0 && itemAddOption.getLuk() == 0 && itemAddOption.getIntel() == 0){
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

            levelAverageAddOptionStat.put(level, Float.parseFloat(String.format("%.1f",totalStat / itemCounts)));
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
            case "STR" -> mainStatPoint = itemAddOption.getStr();
            case "DEX" -> mainStatPoint = itemAddOption.getDex();
            case "LUK" -> mainStatPoint = itemAddOption.getLuk();
            case "INT" -> mainStatPoint = itemAddOption.getIntel();
        }

        if(mainStat.equals("INT")){
            power = itemAddOption.getMagicPower();
        } else {
            power = itemAddOption.getAttackPower();
        }

        allStat = itemAddOption.getAllStat();

        itemAddOptionStats = mainStatPoint + allStat * 10 + power * 4;

        return itemAddOptionStats;
    }

    public List<List<Float>> getPresetAverageEtcOption(MyItemEquipment myItemEquipment, Character character){
        List<List<Float>> presetAverageEtcOption = new ArrayList<>();

        String mainStat = getMainStat(character);
        List<List<MyItem>> presets = getPresets(myItemEquipment);


        for(List<MyItem> preset : presets) {
            presetAverageEtcOption.add(getAverageEtcOption(preset, mainStat));
        }

        //presetAverageEtcOption.add(getAverageEtcOption(presets.get(0), mainStat));

        return presetAverageEtcOption;
    }

    private List<Float> getAverageEtcOption(List<MyItem> preset, String mainStat) {
        Float totalMainStatOption = 0F; // 주스탯 총합
        Float totalPowerOption = 0F; // 공마 총합
        Float totalUpgradeCount = 0F; // 총 업그레이드 횟수

        for (MyItem myItem : preset) {
            MyItemOption itemEtcOption = myItem.getItemEtcOption();

            if(myItem.getItemName().contains("제네시스") || myItem.getItemEquipmentSlot().equals("기계 심장")){ // 제네 무기 제외
                continue;
            }

            if(myItem.getScrollUpgrade() == 0 &&
                    myItem.getScrollUpgradeableCount() == 0 &&
                    myItem.getScrollResilienceCount() == 0){
                // 주문서 작 못하는 아이템 제외
                continue;
            }

            switch (mainStat) { // 주스탯에 따라서 총합에 반영
                case "STR" -> totalMainStatOption += itemEtcOption.getStr();
                case "DEX" -> totalMainStatOption += itemEtcOption.getDex();
                case "LUK" -> totalMainStatOption += itemEtcOption.getLuk();
                case "INT" -> totalMainStatOption += itemEtcOption.getIntel();
            }

            if(mainStat.equals("INT")){
                totalPowerOption += itemEtcOption.getMagicPower();
            } else {
                totalPowerOption += itemEtcOption.getAttackPower();
            }

            //System.out.println(myItem.getItemName() + " : " + itemEtcOption.getStr() + "/" + itemEtcOption.getAttackPower());

            totalUpgradeCount += myItem.getScrollUpgrade();
        }
        if(totalUpgradeCount == 0){
            return new ArrayList<>(List.of(0F, 0F));
        }

        float avgStat = totalMainStatOption / totalUpgradeCount;
        float avgPower = totalPowerOption / totalUpgradeCount;

        List<Float> averageEtcPowerStat = new ArrayList<>();
        averageEtcPowerStat.add(avgPower);
        averageEtcPowerStat.add(avgStat);

        return averageEtcPowerStat;
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
    private Float getAverageStarforce(List<MyItem> preset){

        float starforceItems = 0;
        float totalStarforce = 0L;
        for (MyItem myItem : preset) {
            if(myItem.getItemEquipmentPart().equals("기계 심장")){
                continue;
            }

            Integer starforce = myItem.getStarforce();
            if(starforce != 0){

                if(myItem.getItemName().contains("타일런트")){
                    starforce += 10;
                }

                //System.out.println(myItem.getItemName() + " : " + starforce);
                totalStarforce += starforce;
                starforceItems++;
            }
        }

        return totalStarforce/starforceItems;
    }

    /**
     * 하나의 프리셋에서 최대 스타포스 별 개수
     * @param preset 캐릭터의 프리셋 (List<MyItem>)
     * @return 최대 스타포스 별 개수
     */
    private Integer getMaximumStarforce(List<MyItem> preset){
        int maxStarforce = 0;
        for (MyItem myItem : preset) {
            if(myItem.getItemEquipmentPart().equals("기계 심장")){
                continue;
            }

            int starforce = myItem.getStarforce();
            if(myItem.getItemName().contains("타일런트")){
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
    private Integer getMinimumStarforce(List<MyItem> preset) {
        int minStarforce = 25;
        for (MyItem myItem : preset) {
            if(myItem.getItemEquipmentPart().equals("기계 심장")){
                continue;
            }

            int starforce = myItem.getStarforce();
            if(myItem.getItemName().contains("타일런트")){
                starforce += 10;
            }
            if(starforce != 0 && starforce < minStarforce){
                minStarforce = starforce;
            }
        }

        return minStarforce;
    }




    /**
     * 직업의 주스탯 파악
     * @param character Character 객체
     * @return 직업의 주스탯 STR, DEX, LUK, INT, HP, 올스탯
     */
    private String getMainStat(Character character){

        String characterClass = character.getCharacterClass();

        if (ClassMainStat.STR_CLASS.contains(characterClass)){
            return ClassMainStat.STR;

        } else if (ClassMainStat.DEX_CLASS.contains(characterClass)){
            return ClassMainStat.DEX;

        } else if (ClassMainStat.LUK_CLASS.contains(characterClass)){
            return ClassMainStat.LUK;

        } else if (ClassMainStat.INT_CLASS.contains(characterClass)) {
            return ClassMainStat.INT;

        } else if (ClassMainStat.HP_CLASS.contains(characterClass)) {
            return ClassMainStat.HP;

        } else if (ClassMainStat.ALL_STAT_CLASS.contains(characterClass)) {
            return ClassMainStat.ALL_STAT;
        }

        // 초보자 계열은 str
        return ItemOptionStat.STR;
    }

    private List<List<MyItem>> getPresets(MyItemEquipment myItemEquipment) {
        List<List<MyItem>> presets = new ArrayList<>();
        presets.add(myItemEquipment.getPreset1());
        presets.add(myItemEquipment.getPreset2());
        presets.add(myItemEquipment.getPreset3());
        return presets;
    }
}
