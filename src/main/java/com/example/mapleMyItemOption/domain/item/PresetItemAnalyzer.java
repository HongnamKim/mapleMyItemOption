package com.example.mapleMyItemOption.domain.item;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.ClassMainStat;
import com.example.mapleMyItemOption.domain.item.MyItemData.Item;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItem;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 개별 아이템의 주요 능력치만 분석
 */
@Component
public class PresetItemAnalyzer {
    private final List<String> weaponList = new ArrayList<>(List.of("무기", "보조무기", "엠블렘"));

    public void getStarforce(MyItem myItem, Item item) {
        item.setStarforce(myItem.getStarforce());

        if(myItem.getStarforceScrollFlag().equals("사용")){
            item.setStarforceScroll(true);
        } else{
            item.setStarforceScroll(false);
        }
    }

    public void getAddOption(MyItem myItem, Item item, Character character) {

        String mainStat = getMainStat(character);
        MyItemOption itemAddOption = myItem.getItemAddOption();
        if(itemAddOption.getStr() == 0 && itemAddOption.getDex() == 0 && itemAddOption.getLuk() == 0 && itemAddOption.getIntel() == 0){
            // 추가옵션이 없는 아이템 제외 (견장)
            return;
        }

        Integer mainStatPoint = 0;
        switch (mainStat) {
            case ClassMainStat.STR -> mainStatPoint = itemAddOption.getStr();
            case ClassMainStat.DEX -> mainStatPoint = itemAddOption.getDex();
            case ClassMainStat.INT -> mainStatPoint = itemAddOption.getIntel();
            case ClassMainStat.LUK -> mainStatPoint = itemAddOption.getLuk();
            case ClassMainStat.HP -> mainStatPoint = itemAddOption.getMaxHp();
        }
        Integer power;
        if (mainStat.equals(ClassMainStat.INT)) {
            power = itemAddOption.getMagicPower();
        } else {
            power = itemAddOption.getAttackPower();
        }

        Integer allStat = itemAddOption.getAllStat();

        if(weaponList.contains(myItem.getItemEquipmentSlot())){ // 무보엠일 경우
            Integer damage = itemAddOption.getDamage();
            Integer bossDamage = itemAddOption.getBossDamage();

            Item.AddOption addOption = new Item.AddOption();
            addOption.setMainStat(mainStatPoint);
            addOption.setPower(power);
            addOption.setBossDamage(bossDamage);
            addOption.setDamage(damage);
            addOption.setAllStat(allStat);

            item.setAddOption(addOption);
        } else {

            Item.AddOption addOption = new Item.AddOption();
            addOption.setMainStat(mainStatPoint + allStat * 10 + power * 4);

            item.setAddOption(addOption);
        }
    }

    public void getEtcOption(MyItem myItem, Item item, Character character){

        if(myItem.getScrollUpgrade() == 0 &&
                myItem.getScrollUpgradeableCount() == 0 &&
                myItem.getScrollResilienceCount() == 0){
            // 주문서 작 못하는 아이템 제외
            return;
        }
        List<Float> etcOption = new ArrayList<>();

        MyItemOption itemEtcOption = myItem.getItemEtcOption();

        String mainStat = getMainStat(character);

        if(mainStat.equals(ClassMainStat.INT)){
            float power = Float.parseFloat(String.format("%.1f", (float) itemEtcOption.getMagicPower() / myItem.getScrollUpgrade()));
            etcOption.add(power);
        } else {
            float power = Float.parseFloat(String.format("%.1f", (float) itemEtcOption.getAttackPower() / myItem.getScrollUpgrade()));
            etcOption.add(power);
        }

        switch (mainStat){
            case ClassMainStat.STR -> etcOption.add((float) itemEtcOption.getStr() / myItem.getScrollUpgrade());
            case ClassMainStat.DEX -> etcOption.add((float) itemEtcOption.getDex() / myItem.getScrollUpgrade());
            case ClassMainStat.INT -> etcOption.add((float) itemEtcOption.getIntel() / myItem.getScrollUpgrade());
            case ClassMainStat.LUK -> etcOption.add((float) itemEtcOption.getLuk() / myItem.getScrollUpgrade());
            case ClassMainStat.HP -> etcOption.add((float) itemEtcOption.getMaxHp() / myItem.getScrollUpgrade());
        }

        item.setEtcOption(etcOption);
    }

    public void getPotentialValue(MyItem myItem, Item item, Character character){
        if(myItem.getPotentialOptionGrade() == null){
            return;
        }

        Map<String, Float> potentialValue = new HashMap<>();

        String mainStat = getMainStat(character);
        String power = mainStat.equals(ClassMainStat.INT) ? PotentialOption.MAGIC_POWER : PotentialOption.ATTACK_POWER;

        String potentialOption1 = myItem.getPotentialOption_1();
        String potentialOption2 = myItem.getPotentialOption_2();
        String potentialOption3 = myItem.getPotentialOption_3();

        List<String> potentialOptions = new ArrayList<>(List.of(potentialOption1, potentialOption2, potentialOption3));

        String mainStatPercent = mainStat+"%";
        for (String potentialOption : potentialOptions){
            // 주스탯 % 체크
            if((potentialOption.startsWith(mainStat) || potentialOption.startsWith(PotentialOption.ALL_STAT))
                    && potentialOption.endsWith("%")){
                if(potentialOption.contains(mainStat)){
                    String optionValueString = potentialOption.replace(mainStat + " : +", "").replace("%", "");
                    Float optionValue = Float.parseFloat(optionValueString);

                    Float currentValue = potentialValue.getOrDefault(mainStatPercent, 0F);
                    potentialValue.put(mainStatPercent, currentValue + optionValue);
                } else if (!mainStat.equals(ClassMainStat.HP)) {
                    String optionValueString = potentialOption.replace(PotentialOption.ALL_STAT + " : +", "").replace("%", "");
                    Float optionValue = Float.parseFloat(optionValueString);

                    Float currentValue = potentialValue.getOrDefault(mainStatPercent, 0F);
                    potentialValue.put(mainStatPercent, currentValue + optionValue * 1.12F);
                }


            }

            // 그 외 유효 옵션 체크 (크뎀, 공마%, 보공%)
            for(String option : PotentialOption.OPTION_LIST){
                if(potentialOption.startsWith(option)) {
                    if(option.equals(PotentialOption.ATTACK_POWER) || option.equals(PotentialOption.MAGIC_POWER)){
                        // 직업에 따라 공격력 or 마력만 집계
                        if(!power.equals(option)){
                            continue;
                        }
                    }

                    String optionCategory = potentialOption.endsWith("%") ? option + "%" : option; // % 옵션일 경우 뒤에 % 추가
                    // 글자 수 줄이기
                    if(optionCategory.contains(PotentialOption.BOSS_DAMAGE)){
                        optionCategory = "보공%";
                    } else if (optionCategory.contains(PotentialOption.IGNORE_ARMOR)) {
                        optionCategory = "방무%";
                    } else if(optionCategory.contains(PotentialOption.CRITICAL_DAMAGE)){
                        optionCategory = "크뎀%";
                    } else if(optionCategory.contains(PotentialOption.ITEM_DROP)){
                        optionCategory = "아획%";
                    } else if(optionCategory.contains(PotentialOption.MONEY_DROP)){
                        optionCategory = "메획%";
                    }

                    // 퍼센트 옵션일 경우 뒤에 % 제거
                    String optionValueString = potentialOption.endsWith("%") ?
                            potentialOption.replace(option + " : +", "").replace("%", "") :
                            potentialOption.replace(option + " : +", "");

                    Float optionValue = Float.parseFloat(optionValueString);

                    Float currentOptionValue = potentialValue.getOrDefault(optionCategory, 0F);
                    potentialValue.put(optionCategory, currentOptionValue + optionValue);

                }
            }
        }

        item.setPotentialValue(potentialValue);

    }

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
        return ClassMainStat.STR;
    }
}
