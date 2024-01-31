package com.example.mapleMyItemOption.domain.item.MyItemData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(value = {"item_equipment", "dragon_equipment", "mechanic_equipment"})
public class MyItemEquipment {
    Date date;
    String characterGender;
    String characterClass;
    Integer presetNo;

    @JsonProperty("item_equipment_preset1")
    List<MyItem> preset1;
    @JsonProperty("item_equipment_preset2")
    List<MyItem> preset2;
    @JsonProperty("item_equipment_preset3")
    List<MyItem> preset3;

    MyTitle title;
}
