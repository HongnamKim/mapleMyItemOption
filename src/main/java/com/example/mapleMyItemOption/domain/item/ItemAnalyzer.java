package com.example.mapleMyItemOption.domain.item;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.ClassMainStat;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItem;

import java.util.*;

public class ItemAnalyzer {

    public static final Map<String, String> shortOptionCategory = new HashMap<>();
    String getMainStat(Character character){

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
        return ClassMainStat.STR;
    }

    /**
     * MyItem 의 잠재옵션을 리스트로 받아오는 메소드
     * @param myItem 분석할 아이템
     * @param additional 에디셔널일 경우 true, 그 외 false
     * @return 아이템이 갖고 있는 잠재옵션 List<String>
     */
    List<String> initPotentialOptions(MyItem myItem, boolean additional){
        String potentialOption1;
        String potentialOption2;
        String potentialOption3;

        if(additional){
            potentialOption1 = Optional.ofNullable(myItem.getAdditionalPotentialOption_1()).orElse("");
            potentialOption2 = Optional.ofNullable(myItem.getAdditionalPotentialOption_2()).orElse("");
            potentialOption3 = Optional.ofNullable(myItem.getAdditionalPotentialOption_3()).orElse("");
        } else {
            potentialOption1 = Optional.ofNullable(myItem.getPotentialOption_1()).orElse("");
            potentialOption2 = Optional.ofNullable(myItem.getPotentialOption_2()).orElse("");
            potentialOption3 = Optional.ofNullable(myItem.getPotentialOption_3()).orElse("");
        }

        return new ArrayList<>(List.of(potentialOption1, potentialOption2, potentialOption3));
    }

    /**
     * 잠재능력 옵션 별 수치 반환하는 메소드
     * @param myItem 분석할 아이템
     * @param character 캐릭터 직업
     * @param additional 에디셔널이면 true, 그 외 false
     * @return Map<옵션명(String), 수치(Float)>
     */
    Map<String, Float> getPotentialValue(MyItem myItem, Character character, boolean additional){
        // 에디 잠재 없는 경우
        if(additional && myItem.getAdditionalPotentialOptionGrade() == null){
            return null;
        }

        // 잠재 없는 경우
        if(!additional && myItem.getPotentialOptionGrade() == null){
            return null;
        }

        Map<String, Float> potentialValue = new HashMap<>();

        String mainStat = getMainStat(character);
        String power = mainStat.equals(ClassMainStat.INT) ? PotentialOption.MAGIC_POWER : PotentialOption.ATTACK_POWER;

        String mainStatPercent = mainStat+"%"; // STR%
        String perLevelStat = PotentialOption.PER_LEVEL + " " + mainStat; // 캐릭터 기준 9레벨 당 STR

        List<String> optionList = new ArrayList<>(PotentialOption.OPTION_LIST);
        optionList.add(perLevelStat);

        List<String> potentialOptions = initPotentialOptions(myItem, additional);

        for (String potentialOption : potentialOptions){
            if(potentialOption.length() == 0) { // 잠재 2줄일 경우 3번째 생략
                continue;
            }

            // 주스탯% or 올스탯% 체크
            if((potentialOption.startsWith(mainStat) || potentialOption.startsWith(PotentialOption.ALL_STAT))
                    && potentialOption.endsWith("%")){

                if(potentialOption.contains(mainStat)){
                    // 주스탯 % 수치 추가
                    Float optionValue = getPotentialOptionValue(potentialOption, mainStat);

                    potentialValue.putIfAbsent(mainStatPercent, 0F);
                    potentialValue.computeIfPresent(mainStatPercent, (k, v) -> v + optionValue);

                } else if (!mainStat.equals(ClassMainStat.HP)) {
                    //데몬어벤져일 경우 올스탯 제외
                    // 올스탯 % 수치를 주스탯 수치로 변환하여 추가
                    Float optionValue = getPotentialOptionValue(potentialOption, PotentialOption.ALL_STAT);

                    potentialValue.putIfAbsent(mainStatPercent, 0F);
                    potentialValue.computeIfPresent(mainStatPercent, (k, v) -> {

                        if(ClassMainStat.TWO_SUB_STAT_CLASS.contains(character.getCharacterClass())) {
                            return v + optionValue * 1.23F;
                        }
                        return v + optionValue * 1.12F;
                    });

                }
            }
            // 그 외 유효 옵션 체크 (크뎀, 공마%, 보공%, 렙당 주스탯)
            for(String option : optionList){
                if(!potentialOption.startsWith(option)) {
                    continue;
                }

                if(option.equals(PotentialOption.ATTACK_POWER) || option.equals(PotentialOption.MAGIC_POWER)){
                    // 직업에 따라 공격력 or 마력만 집계
                    if(!power.equals(option)){
                        continue;
                    }
                }

                String optionCategory = getShortOptionCategory(potentialOption, option);

                Float optionValue = getPotentialOptionValue(potentialOption, option);

                potentialValue.putIfAbsent(optionCategory, 0F);
                potentialValue.computeIfPresent(optionCategory, (k, v) -> v + optionValue);

            }
        }

        return potentialValue;
    }

    /**
     * 잠재 옵션의 수치를 반환하는 메소드
     * 잠재옵션이 어떤 종류인지 파악 후 사용
     * @param potentialOption 잠재옵션 String
     * @param option 잠재옵션이 어떤 종류의 옵션인지
     * @return 잠재옵션 수치 Float
     */
    Float getPotentialOptionValue(String potentialOption, String option){
        if(potentialOption.endsWith("%")){
            String optionValueString = potentialOption.replace(option+ " : +","").replace("%","");
            return Float.parseFloat(optionValueString);
        }

        if(potentialOption.contains(PotentialOption.SKILL_COOL_TIME)){
            String optionValueString = potentialOption.replace(option + " : ", "")
                    .replace("초(10초 이하는 10%감소, 5초 미만으로 감소 불가)", "")
                    .replace("초(10초 이하는 5%감소, 5초 미만으로 감소 불가)", "");
            return Float.parseFloat(optionValueString);
        }

        // 공/마력, 렙당 주스탯
        String optionValueString = potentialOption.replace(option + " : +", "");
        return Float.parseFloat(optionValueString);
    }


    String getShortOptionCategory(String potentialOption, String option) {
        String optionCategory = potentialOption.endsWith("%") ? option + "%" : option;

        for(String shortOption : PotentialOption.SHORT_OPTION) {
            if(optionCategory.contains(shortOption)){
                return shortOptionCategory.get(shortOption);
            }
        }

        return optionCategory;
    }

    List<Integer> getStarforceScrollValue(MyItem myItem){
        if(myItem.getStarforceScrollFlag().equals("미사용")){
            return new ArrayList<>(List.of(0, 0));
        }

        Integer equipmentLevel = myItem.getItemBaseOption().getBaseEquipmentLevel();
        Integer starforce = myItem.getStarforce();

        int statTwoCount = (int) Math.ceil((double) (equipmentLevel - 80) / 20);
        int statThreeCount = (int) Math.floor((double) (equipmentLevel - 80) / 20);
        int baseStat = 2 + statTwoCount * 2 + statThreeCount * 3;

        int basePower = 2 + (equipmentLevel - 80) / 10;

        int totalStat = 0;
        int totalPower = 0;

        for (int i = 1; i <= starforce; i++) {
            if (i < 6) {
                int addStat = baseStat + i * (i-1) / 2;
                totalStat += addStat;
            } else{
                int addPower = i - 6 + basePower;
                if(i == 12){
                    addPower++;
                }
                totalPower += addPower;
            }
        }

        List<Integer> starforceScrollValue = new ArrayList<>();
        starforceScrollValue.add(totalPower);
        starforceScrollValue.add(totalStat);

        return starforceScrollValue;
    }
}
