package com.example.mapleMyItemOption.domain.item;


import com.example.mapleMyItemOption.api.ApiService;
import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.MyItemData.PresetTotalStat;
import com.example.mapleMyItemOption.domain.item.RepoItemData.Item;
import com.example.mapleMyItemOption.domain.item.RepoItemData.ItemEquipment;
import com.example.mapleMyItemOption.domain.item.RepoItemData.ItemOption;
import com.example.mapleMyItemOption.domain.item.rawItemData.RawItem;
import com.example.mapleMyItemOption.domain.item.rawItemData.RawItemEquipment;
import com.example.mapleMyItemOption.domain.item.rawItemData.RawItemOption;
import com.example.mapleMyItemOption.domain.repository.item.MemoryItemEquipmentRepository;
import com.example.mapleMyItemOption.domain.repository.item.MemoryItemOptionRepository;
import com.example.mapleMyItemOption.domain.repository.item.MemoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * API 응답으로 받은 Item Equipment 데이터를 ItemEquipment, Item, ItemOption 으로 분리
 * ItemEquipment 는 ocid 로 캐릭터 관계 설정
 * Item 은 ocid 와 preset_id 로 관계 설정 --> 어떤 캐릭터의 어떤 프리셋에 포함된 아이템인지
 * ItemOption 은 item_id 로 아이템 관계 설정 --> 어떤 아이템에 붙은 옵션인지
 *
 * 캐릭터 검색으로 착용 아이템을 불러올 경우 ItemEquipment, MyItem, ItemOption 으로 분리
 * ItemEquipment 는 ocid 로 캐릭터 관계 설정
 */
@Service
@RequiredArgsConstructor
public class ItemService {
    private static Long presetId = 0L;

    private final MemoryItemEquipmentRepository memoryItemEquipmentRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final MemoryItemOptionRepository memoryItemOptionRepository;

    private final ApiService apiService;
    private final ItemAnalyzer itemAnalyzer;

    public MyItemEquipment searchMyItemEquipment(String characterName, String date){
        String ocid = apiService.fetchCharacterOcid(characterName);
        return apiService.fetchMyItemEquipment(ocid, date);
    }

    /**
     *
     * @param myItemEquipment
     * @param character
     * @param specificStat 주스탯% 올스탯% 따로할지, 합쳐서 할지
     * @return
     */
    public List<PresetTotalStat> getPresetTotalStats (MyItemEquipment myItemEquipment, Character character, boolean specificStat){
        List<PresetTotalStat> presetTotalStats = new ArrayList<>();

        List<Float> presetAverageStarforce = itemAnalyzer.getPresetAverageStarforce(myItemEquipment);
        List<Integer> presetMaximumStarforce = itemAnalyzer.getPresetMaximumStarforce(myItemEquipment);
        List<Integer> presetMinimumStarforce = itemAnalyzer.getPresetMinimumStarforce(myItemEquipment);

        List<Map<Integer, Float>> presetAverageAddOption = itemAnalyzer.getPresetAverageAddOption(myItemEquipment, character);

        List<List<Float>> presetAverageEtcOption = itemAnalyzer.getPresetAverageEtcOption(myItemEquipment, character);

        List<Map<String, Integer>> presetPotentialGradeCount = itemAnalyzer.getPresetPotentialGradeCount(myItemEquipment, false);
        List<Map<String, Integer>> presetAdditionalPotentialGradeCount = itemAnalyzer.getPresetPotentialGradeCount(myItemEquipment, true);

        List<Map<String, Float>> presetAveragePotentialValues = itemAnalyzer.getPresetPotentialValues(myItemEquipment, character, false, specificStat, PotentialValuesOption.AVERAGE);
        List<Map<String, Float>> presetAverageAdditionalPotentialValues = itemAnalyzer.getPresetPotentialValues(myItemEquipment, character, true, specificStat, PotentialValuesOption.AVERAGE);

        List<Map<String, Float>> presetTotalPotentialValues = itemAnalyzer.getPresetPotentialValues(myItemEquipment, character, false, specificStat, PotentialValuesOption.TOTAL);
        List<Map<String, Float>> presetTotalAdditionalPotentialValues = itemAnalyzer.getPresetPotentialValues(myItemEquipment, character, true, specificStat, PotentialValuesOption.TOTAL);

        List<Map<String, Float>> presetPotentialLines = itemAnalyzer.getPresetPotentialValues(myItemEquipment, character, false, specificStat, PotentialValuesOption.LINES);
        List<Map<String, Float>> presetAdditionalPotentialLines = itemAnalyzer.getPresetPotentialValues(myItemEquipment, character, true, specificStat, PotentialValuesOption.LINES);



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

    // raw item data 를 db 에 넣을 수 있는 형태로 변환

    /**
     * RawItemEquipment 를 ItemEquipment 로 변환, repository 에 등록
     * @param rawData ApiService 의 fetchItemEquipment 의 반환값
     * @param ocid 조회한, 등록할 캐릭터의 ocid
     * @return ItemEquipment
     */
    public ItemEquipment rawDataToItemEquipment(RawItemEquipment rawData, String ocid){
        ItemEquipment itemEquipment = new ItemEquipment();

        transferBasicItemEquipmentInfo(rawData, ocid, itemEquipment);

        itemEquipment.setPreset1Id(++presetId);
        itemEquipment.setPreset2Id(++presetId);
        itemEquipment.setPreset3Id(++presetId);

        memoryItemEquipmentRepository.save(itemEquipment);


        List<RawItem> rawPreset1 = rawData.getRawItemEquipmentPreset1();
        List<RawItem> rawPreset2 = rawData.getRawItemEquipmentPreset2();
        List<RawItem> rawPreset3 = rawData.getRawItemEquipmentPreset3();

        List<Item> preset1 = new ArrayList<>();
        List<Item> preset2 = new ArrayList<>();
        List<Item> preset3 = new ArrayList<>();

        for (RawItem rawItem : rawPreset1) {
            Item item = rawDataToItem(rawItem, ocid, itemEquipment.getPreset1Id());
            preset1.add(item);
        }

        for (RawItem rawItem : rawPreset2) {
            Item item = rawDataToItem(rawItem, ocid, itemEquipment.getPreset2Id());
            preset2.add(item);
        }

        for (RawItem rawItem : rawPreset3) {
            Item item = rawDataToItem(rawItem, ocid, itemEquipment.getPreset3Id());
            preset3.add(item);
        }

        return itemEquipment;
    }

    /**
     * rawItem 을 Item 객체로 변환, repository 에 등록
     * @param rawItem
     * @param ocid
     * @return Item
     */
    private Item rawDataToItem(RawItem rawItem, String ocid, Long presetId){
        Item item = new Item();
        item.setOcid(ocid);
        item.setPresetId(presetId);

        transferItemInfo(rawItem, item);

        memoryItemRepository.save(item);

        Long itemId = item.getItemId();

        // 아이템 옵션 repository 에 등록, 아이템에 옵션 id 넣기
        Long itemTotalOptionId = rawDataToItemOption(rawItem.getItemTotalOption(), itemId).getItemOptionId();
        item.setItemTotalOptionId(itemTotalOptionId);

        Long itemBaseOptionId = rawDataToItemOption(rawItem.getItemBaseOption(), itemId).getItemOptionId();
        item.setItemBaseOptionId(itemBaseOptionId);

        Long itemAddOptionId = rawDataToItemOption(rawItem.getItemAddOption(), itemId).getItemOptionId();
        item.setItemAddOptionId(itemAddOptionId);

        Long itemEtcOptionId = rawDataToItemOption(rawItem.getItemEtcOption(), itemId).getItemOptionId();
        item.setItemEtcOptionId(itemEtcOptionId);

        Long itemExceptionalOptionId = rawDataToItemOption(rawItem.getItemExceptionalOption(), itemId).getItemOptionId();
        item.setItemExceptionalOptionId(itemExceptionalOptionId);

        Long itemStarforceOptionId = rawDataToItemOption(rawItem.getItemStarforceOption(), itemId).getItemOptionId();
        item.setItemStarforceOptionId(itemStarforceOptionId);

        return item;
    }

    /**
     * rawItemOption 을 ItemOption 으로 변환, repository 에 등록
     * @param rawItemOption
     * @param itemId
     * @return ItemOption
     */
    private ItemOption rawDataToItemOption(RawItemOption rawItemOption, Long itemId){
        ItemOption itemOption = new ItemOption();

        // 해당 옵션이 어떤 아이템에 붙어있는 옵션인지
        itemOption.setItemId(itemId);

        // 옵션 정보 옮기기
        transferItemOptionInfo(rawItemOption, itemOption);

        // repository 에 저장
        memoryItemOptionRepository.save(itemOption);

        return itemOption;
    }

    private void transferBasicItemEquipmentInfo(RawItemEquipment rawData, String ocid, ItemEquipment itemEquipment) {
        itemEquipment.setOcid(ocid);
        itemEquipment.setDate(rawData.getDate());
        itemEquipment.setCharacterClass(rawData.getCharacterClass());
        itemEquipment.setCharacterGender(rawData.getCharacterGender());
        itemEquipment.setPresetNo(rawData.getPresetNo());
    }

    private void transferItemInfo(RawItem rawItem, Item item){
        item.setDateExpire(rawItem.getDateExpire());
        item.setItemEquipmentPart(rawItem.getItemEquipmentPart());
        item.setItemEquipmentSlot(rawItem.getItemEquipmentSlot());
        item.setItemName(rawItem.getItemName());
        item.setItemIcon(rawItem.getItemIcon());
        item.setItemDescription(rawItem.getItemDescription());
        item.setItemShapeName(rawItem.getItemShapeName());
        item.setItemShapeIcon(rawItem.getItemShapeIcon());
        item.setItemGender(rawItem.getItemGender());

        item.setStarforce(rawItem.getStarforce());
        item.setStarforceScrollFlag(rawItem.getStarforceScrollFlag().equals("사용"));

        item.setPotentialOptionGrade(rawItem.getPotentialOptionGrade());
        item.setPotentialOption_1(rawItem.getPotentialOption_1());
        item.setPotentialOption_2(rawItem.getPotentialOption_2());
        item.setPotentialOption_3(rawItem.getPotentialOption_3());

        item.setAdditionalPotentialOptionGrade(rawItem.getAdditionalPotentialOptionGrade());
        item.setAdditionalPotentialOption_1(rawItem.getAdditionalPotentialOption_1());
        item.setAdditionalPotentialOption_2(rawItem.getAdditionalPotentialOption_2());
        item.setAdditionalPotentialOption_3(rawItem.getAdditionalPotentialOption_3());

        item.setEquipmentLevelIncrease(rawItem.getEquipmentLevelIncrease());
        item.setGrowthExp(rawItem.getGrowthExp());
        item.setGrowthLevel(rawItem.getGrowthLevel());

        item.setScrollUpgrade(rawItem.getScrollUpgrade());
        item.setScrollUpgradeableCount(rawItem.getScrollUpgradeableCount());
        item.setGoldenHammerFlag(rawItem.getGoldenHammerFlag());

        item.setCuttableCount(rawItem.getCuttableCount());
        item.setScrollResilienceCount(rawItem.getScrollResilienceCount());

        item.setSoulName(rawItem.getSoulName());
        item.setSoulOption(rawItem.getSoulOption());

        item.setSpecialRingLevel(rawItem.getSpecialRingLevel());
    }

    private void transferItemOptionInfo(RawItemOption rawItemOption, ItemOption itemOption){
        itemOption.setStr(rawItemOption.getStr());
        itemOption.setDex(rawItemOption.getDex());
        itemOption.setIntel(rawItemOption.getIntel());
        itemOption.setLuk(rawItemOption.getLuk());

        itemOption.setMaxHp(rawItemOption.getMaxHp());
        itemOption.setMaxMp(rawItemOption.getMaxMp());

        itemOption.setAttackPower(rawItemOption.getAttackPower());
        itemOption.setMagicPower(rawItemOption.getMagicPower());

        itemOption.setArmor(rawItemOption.getArmor());
        itemOption.setSpeed(rawItemOption.getSpeed());
        itemOption.setJump(rawItemOption.getJump());
        itemOption.setBossDamage(rawItemOption.getBossDamage());
        itemOption.setIgnoreMonsterArmor(rawItemOption.getIgnoreMonsterArmor());
        itemOption.setAllStat(rawItemOption.getAllStat());
        itemOption.setDamage(rawItemOption.getDamage());
        itemOption.setBaseEquipmentLevel(rawItemOption.getBaseEquipmentLevel());
        itemOption.setEquipmentLevelDecrease(rawItemOption.getEquipmentLevelDecrease());
        itemOption.setMaxHpRate(rawItemOption.getMaxHpRate());
        itemOption.setMaxMpRate(rawItemOption.getMaxMpRate());
    }

}
