package com.example.mapleMyItemOption.domain.item;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.ClassMainStat;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItem;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 개별 아이템의 주요 능력치만 분석
 */
@Component
public class PresetItemAnalyzer {

    public Integer getAddOption(MyItem myItem, Character character){
        String mainStat = getMainStat(character);

        MyItemOption itemAddOption = myItem.getItemAddOption();

        Integer mainStatPoint;
        switch (mainStat){
            case ClassMainStat.STR -> mainStatPoint = itemAddOption.getStr();
            case ClassMainStat.DEX -> mainStatPoint = itemAddOption.getDex();
            case ClassMainStat.INT -> mainStatPoint = itemAddOption.getIntel();
            case ClassMainStat.LUK -> mainStatPoint = itemAddOption.getLuk();
            case ClassMainStat.HP -> mainStatPoint = itemAddOption.getMaxHp();
            default -> mainStatPoint = itemAddOption.getStr();
        }

        Integer mainPower;
        if(mainStat.equals(ClassMainStat.INT)){
            mainPower = itemAddOption.getMagicPower();
        } else {
            mainPower = itemAddOption.getAttackPower();
        }

        Integer allStatPoint = itemAddOption.getAllStat();

        return mainStatPoint + mainPower * 4 + allStatPoint * 10;
    }

    public List<Float> getEtcOption(MyItem myItem, Character character){
        List<Float> etcOption = new ArrayList<>();

        String mainStat = getMainStat(character);

        MyItemOption itemEtcOption = myItem.getItemEtcOption();

        Integer mainStatPoint;
        switch (mainStat){
            case ClassMainStat.STR -> mainStatPoint = itemEtcOption.getStr();
            case ClassMainStat.DEX -> mainStatPoint = itemEtcOption.getDex();
            case ClassMainStat.INT -> mainStatPoint = itemEtcOption.getIntel();
            case ClassMainStat.LUK -> mainStatPoint = itemEtcOption.getLuk();
            case ClassMainStat.HP -> mainStatPoint = itemEtcOption.getMaxHp();
            default -> mainStatPoint = itemEtcOption.getStr();
        }

        Integer mainPower;
        if(mainStat.equals(ClassMainStat.INT)){
            mainPower = itemEtcOption.getMagicPower();
        } else {
            mainPower = itemEtcOption.getAttackPower();
        }

        Integer scrollUpgrade = myItem.getScrollUpgrade();

        etcOption.add((float) mainPower / scrollUpgrade);
        etcOption.add((float) mainStatPoint / scrollUpgrade);

        return etcOption;
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
        return ItemOptionStat.STR;
    }
}
