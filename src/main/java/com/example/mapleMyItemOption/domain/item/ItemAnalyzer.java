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
