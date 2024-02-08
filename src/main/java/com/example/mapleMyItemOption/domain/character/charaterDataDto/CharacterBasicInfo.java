package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import lombok.Data;

import java.util.Date;

@Data
public class CharacterBasicInfo {
    private Date date;
    private String characterName;
    private String worldName;
    private String characterGender;
    private String characterClass;
    private Integer characterClassLevel;
    private Integer characterLevel;
    private Long characterExp;
    private Float characterExpRate;
    private String characterGuildName;
    private String characterImage;
}

/*
{
  "date": "2023-12-21T00:00+09:00",
  "character_name": "콩주",
  "world_name": "루나",
  "character_gender": "여",
  "character_class": "소울마스터",
  "character_class_level": "6",
  "character_level": 284,
  "character_exp": 23588594186139,
  "character_exp_rate": "47.883",
  "character_guild_name": "콩주르",
  "character_image": "https://open.api.nexon.com/static/maplestory/Character/IPBKKINAPLKCMMAHNGEIGOLILHBNAEFEFBKCGPCMCAOFNJPJJIHNPGJMNJBGBDMLABPMNOEDLEPLHGBMGPHPBHPKFNEGOOEBNDJBKKLOCAGEKBNICAPPDCLCLCNDECBEOLGOOCAIHCJPIEAIELHNEIODPIKJEHCEDKOAFGFCGNIDGPAONNEHHCJJGFOEIAMOINBDNHJKCPGCHIFKJPAOILEENCFKJPMJCKEIEGEOOCJOEOAFPPBJLDAONBHEFEBI.png"
}
 */
