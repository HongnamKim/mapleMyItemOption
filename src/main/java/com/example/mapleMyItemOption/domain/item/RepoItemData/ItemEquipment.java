package com.example.mapleMyItemOption.domain.item.RepoItemData;

import lombok.Data;

import java.util.Date;


@Data
public class ItemEquipment {
    private Long itemEquipmentId; // Primary key
    private String ocid; // character ocid (Foreign key)

    private Date date;
    private String characterGender;
    private String characterClass;
    private Integer presetNo; // 조회 당시 적용 프리셋

    private Long preset1Id;
    private Long preset2Id;
    private Long preset3Id;

    /*
    List<Item> itemEquipmentPreset1;
    List<Item> itemEquipmentPreset2;
    List<Item> itemEquipmentPreset3;*/
}
