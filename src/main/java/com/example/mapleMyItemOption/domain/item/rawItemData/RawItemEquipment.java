package com.example.mapleMyItemOption.domain.item.rawItemData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(value = {"dragon_equipment", "mechanic_equipment", "item_equipment_preset1", "item_equipment_preset2", "item_equipment_preset3"}) // dragon_equipment, mechanic_equipment 제외
public class RawItemEquipment {
    private Date date;
    private String characterGender;
    private String characterClass;
    private Integer presetNo;

    @JsonProperty("item_equipment")
    private List<RawItem> rawItemEquipment;

    @JsonProperty("item_equipment_preset_1")
    private List<RawItem> rawItemEquipmentPreset1;
    @JsonProperty("item_equipment_preset_2")
    private List<RawItem> rawItemEquipmentPreset2;
    @JsonProperty("item_equipment_preset_3")
    private List<RawItem> rawItemEquipmentPreset3;

    @JsonProperty("title")
    private RawTitle rawTitle;
}
