package com.example.mapleMyItemOption.domain.item.rawItemData;

import lombok.Data;

import java.util.Date;

@Data
public class RawTitle {
    String titleName;
    String titleIcon;
    String titleDescription;
    Date dateExpire; // 칭호 자체의 유효기간
    Date dateOptionExpire; // 옵션의 유효기간
}
