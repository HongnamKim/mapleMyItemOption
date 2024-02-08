package com.example.mapleMyItemOption.domain.item.MyItemData;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    String itemName;
    String itemImage;
    Integer starforce;
    Integer addOption;
    List<Float> etcOption;
}
