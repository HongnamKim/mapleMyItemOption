package com.example.mapleMyItemOption.domain.item.MyItemData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(value = {"date_expire", "date_option_expire"})
public class MyTitle {
    String titleName;
    String titleIcon;
    String titleDescription;
    Date dateExpire; // 칭호 자체의 유효기간
    Date dateOptionExpire; // 옵션의 유효기간
}
