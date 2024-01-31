package com.example.mapleMyItemOption.api;

import com.example.mapleMyItemOption.domain.character.rawCharaterData.CharacterBasicInfo;
import com.example.mapleMyItemOption.domain.character.rawCharaterData.CharacterTotalStat;
import com.example.mapleMyItemOption.domain.character.rawCharaterData.Ranking;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.rawItemData.RawItemEquipment;

public interface ApiService {
    String API_URL = "https://open.api.nexon.com/maplestory/v1";
    String HEADER_NAME = "x-nxopen-api-key";

    String fetchCharacterOcid(String characterName);

    default Ranking fetchCharacterRanking(String date, int page) { return null;}

    CharacterBasicInfo fetchCharacterBasicInfo(String ocid, String date);

    CharacterTotalStat fetchCharacterTotalStat(String ocid, String date);

    RawItemEquipment fetchItemEquipment(String ocid, String date);

    MyItemEquipment fetchMyItemEquipment(String ocid, String date);
}
