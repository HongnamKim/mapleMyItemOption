package com.example.mapleMyItemOption.domain.item;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.ClassMainStat;
import com.example.mapleMyItemOption.domain.item.MyItemData.Item;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItem;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemOption;
import com.example.mapleMyItemOption.domain.item.itemSearch.PresetTotalStat;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 개별 아이템의 주요 능력치만 분석
 */
@Component
public class PresetItemAnalyzer extends ItemAnalyzer{
    private final List<String> weaponList = new ArrayList<>(List.of("무기", "보조무기", "엠블렘"));

    public Item analyzeItem(MyItem myItem, Character character){
        Item item = new Item();
        initItem(myItem, item);

        setItemStarforce(myItem, item);
        setItemAddOption(myItem, item, character);
        setItemEtcOption(myItem, item, character);

        setItemPotentialValue(myItem, item, character, false);
        setItemPotentialValue(myItem, item, character, true);

        return item;
    }

    private void initItem(MyItem myItem, Item item){
        item.setItemName(myItem.getItemName());
        item.setItemImage(myItem.getItemIcon());
        item.setItemEquipmentSlot(myItem.getItemEquipmentSlot());
        item.setItemBaseEquipLevel(myItem.getItemBaseOption().getBaseEquipmentLevel());
        item.setSpecialRingLevel(myItem.getSpecialRingLevel());

        item.setPotentialGrade(myItem.getPotentialOptionGrade());
        item.setAdditionalPotentialGrade(myItem.getAdditionalPotentialOptionGrade());
    }

    private void setItemStarforce(MyItem myItem, Item item) {
        item.setStarforce(myItem.getStarforce());

        item.setStarforceScroll(myItem.getStarforceScrollFlag().equals("사용"));
    }

    private void setItemAddOption(MyItem myItem, Item item, Character character) {

        String mainStat = getMainStat(character);
        MyItemOption itemAddOption = myItem.getItemAddOption();
        if(itemAddOption.getStr() == 0 && itemAddOption.getDex() == 0 && itemAddOption.getLuk() == 0 && itemAddOption.getIntel() == 0 && itemAddOption.getMaxHp() == 0){
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

        Integer power = mainStat.equals(ClassMainStat.INT) ? itemAddOption.getMagicPower() : itemAddOption.getAttackPower();

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
            if(mainStat.equals(ClassMainStat.HP)){
                addOption.setMainStat(mainStatPoint);
            }else{
                addOption.setMainStat(mainStatPoint + allStat * 10 + power * 4);
            }

            item.setAddOption(addOption);
        }
    }

    public void compareAddOption(Map<Integer, Float> averageAddOption, Map<String, Item> presetItems){
        for (Map.Entry<String, Item> presetItem : presetItems.entrySet()) {
            Item item = presetItem.getValue();
            int itemLevel = item.getItemBaseEquipLevel() / 10 * 10;
            //System.out.println(itemLevel);
            String itemName = item.getItemName();
            if(item.getAddOption() == null){
                continue;
            }

            Item.AddOption addOption = item.getAddOption();

            Integer addOptionValue = addOption.getMainStat();

            if(averageAddOption.get(itemLevel) > addOptionValue){
                item.setCompareAddOption(-1);
            } else if(averageAddOption.get(itemLevel) < addOptionValue){
                item.setCompareAddOption(1);
            } else {
                item.setCompareAddOption(0);
            }


        }
    }

    private void setItemEtcOption(MyItem myItem, Item item, Character character){

        // 주문서 작 못하는 아이템 제외
        if(myItem.getScrollUpgrade() == 0){
            return;
        }

        MyItemOption itemEtcOption = myItem.getItemEtcOption();
        List<Integer> starforceScrollValue = getStarforceScrollValue(myItem);

        List<Float> etcOption = new ArrayList<>();
        String mainStat = getMainStat(character);

        // 직업에 맞는 주문서의 공/마 옵션 추가
        if(mainStat.equals(ClassMainStat.INT)){
            float avgPower = (float) (itemEtcOption.getMagicPower() - starforceScrollValue.get(0)) / myItem.getScrollUpgrade();
            Float roundedAvgPower = Math.round(avgPower * 10) / 10F;

            etcOption.add(roundedAvgPower);
        } else {
            float avgPower = (float) (itemEtcOption.getAttackPower() - starforceScrollValue.get(0)) / myItem.getScrollUpgrade();
            Float roundedAvgPower = Math.round(avgPower * 10) / 10F;

            etcOption.add(roundedAvgPower);
        }

        // 주스탯에 맞는 주문서 옵션 추가
        switch (mainStat){
            case ClassMainStat.STR -> {
                if (itemEtcOption.getStr() - starforceScrollValue.get(1) < 0) {
                    etcOption.add((float) itemEtcOption.getStr() / myItem.getScrollUpgrade());
                } else {
                    etcOption.add((float) (itemEtcOption.getStr() - starforceScrollValue.get(1)) / myItem.getScrollUpgrade());
                }
            }
            case ClassMainStat.DEX -> {
                if (itemEtcOption.getDex() - starforceScrollValue.get(1) < 0) {
                    etcOption.add((float) itemEtcOption.getDex() / myItem.getScrollUpgrade());
                } else {
                    etcOption.add((float) (itemEtcOption.getDex() - starforceScrollValue.get(1)) / myItem.getScrollUpgrade());
                }
            }
            case ClassMainStat.INT -> {
                if (itemEtcOption.getIntel() - starforceScrollValue.get(1) < 0) {
                    etcOption.add((float) itemEtcOption.getIntel() / myItem.getScrollUpgrade());
                } else {
                    etcOption.add((float) (itemEtcOption.getIntel() - starforceScrollValue.get(1)) / myItem.getScrollUpgrade());
                }
            }
            case ClassMainStat.LUK -> {
                if (itemEtcOption.getLuk() - starforceScrollValue.get(1) < 0) {
                    etcOption.add((float) itemEtcOption.getLuk() / myItem.getScrollUpgrade());
                } else {
                    etcOption.add((float) (itemEtcOption.getLuk() - starforceScrollValue.get(1)) / myItem.getScrollUpgrade());
                }
            }
            case ClassMainStat.HP -> {
                if (itemEtcOption.getMaxHp() - starforceScrollValue.get(1) < 0) {
                    etcOption.add((float) itemEtcOption.getMaxHp() / myItem.getScrollUpgrade());
                } else {
                    etcOption.add((float) (itemEtcOption.getMaxHp() - starforceScrollValue.get(1)) / myItem.getScrollUpgrade());
                }
            }
        }

        item.setEtcOption(etcOption);
    }

    private void setItemPotentialValue(MyItem myItem, Item item, Character character, boolean additional){

        /*// 에디 잠재 없는 경우
        if(additional && myItem.getAdditionalPotentialOptionGrade() == null){
            return;
        }

        // 잠재 없는 경우
        if(!additional && myItem.getPotentialOptionGrade() == null){
            return;
        }

        Map<String, Float> potentialValue = new HashMap<>();

        String mainStat = getMainStat(character);
        String power = mainStat.equals(ClassMainStat.INT) ? PotentialOption.MAGIC_POWER : PotentialOption.ATTACK_POWER;

        String mainStatPercent = mainStat+"%";
        String perLevelStat = PotentialOption.PER_LEVEL + " " + mainStat;

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
                            System.out.println("듀섀카");
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
        }*/

        Map<String, Float> potentialValue = getPotentialValue(myItem, character, additional, false);

        if(additional){
            item.setAdditionalPotentialValue(potentialValue);
        } else {
            item.setPotentialValue(potentialValue);
        }
    }
}
