package com.example.mapleMyItemOption.domain.character.characterSearch;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.api.ApiService;

import com.example.mapleMyItemOption.domain.character.charaterDataDto.*;
import com.example.mapleMyItemOption.exceptions.IllegalDateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CharacterSearchService {

    private final ApiService apiService;

    /**
     * 캐틱터의 이름으로 기본 정보를 불러오는 메소드
     * @param characterName 검색하고자 하는 캐릭터 닉네임
     * @param date 검색 기준 날짜
     * @return Character 객체
     */
    public Character searchMyCharacter(String characterName, String date) throws IllegalDateException{
        String ocid = apiService.fetchCharacterOcid(characterName);

        // 캐릭터 정보 fetch
        CharacterBasicInfo characterBasicInfo = apiService.fetchCharacterBasicInfo(ocid, date);

        if(characterBasicInfo.getCharacterName() == null) {
            throw new IllegalDateException();
        }

        CharacterTotalStat characterTotalStat = apiService.fetchCharacterTotalStat(ocid, date);
        CharacterDojang characterDojang = apiService.fetchCharacterDojang(ocid, date);
        CharacterPopularity characterPopularity = apiService.fetchCharacterPopularity(ocid, date);
        CharacterUnion characterUnion = apiService.fetchCharacterUnion(ocid, date);
        CharacterAbility characterAbility = apiService.fetchCharacterAbility(ocid, date);

        // Character 객체로 변환하여 return
        return rawDataToCharacter(ocid, characterBasicInfo, characterTotalStat, characterPopularity, characterUnion, characterAbility, characterDojang);
    }

    /**
     * 최근 1주동안 가장 높은 전투력의 날짜를 조회
     * @param characterName 조회할 캐릭터의 이름
     * @param date 기준 날짜
     * @return 기준 날짜 1주 전 ~ 기준 날짜 사이에 가장 높은 전투력의 날짜
     */
    public String findMaximumAssaultDate(String characterName, String date) {
        String ocid = apiService.fetchCharacterOcid(characterName);

        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        LocalDate minDate = LocalDate.of(2024, 1, 17);

        LocalDate maximumAssaultDate = localDate;
        long maximumAssault = apiService.fetchCharacterTotalStat(ocid, date)
                .getFinalStat()
                .get(FinalStatOrder.ASSAULT.getOrder()).getStatValue().longValue();

        for(int i = 0; i < 8; i++){
            LocalDate beforeDate = localDate.minusDays(i);
            if(beforeDate.isEqual(minDate)){
                break;
            }

            CharacterTotalStat characterTotalStat = apiService.fetchCharacterTotalStat(ocid, beforeDate.toString());

            // 캐릭터 생성 이전 날짜일 경우
            if(characterTotalStat.getCharacterClass() == null && characterTotalStat.getFinalStat().isEmpty()){
                break;
            }

            long assault = characterTotalStat
                    .getFinalStat()
                    .get(FinalStatOrder.ASSAULT.getOrder())
                    .getStatValue()
                    .longValue();

            if(assault >= maximumAssault){
                maximumAssault = assault;
                maximumAssaultDate = beforeDate;
            }
        }

        return maximumAssaultDate.toString();
    }

    /**
     * api 호출로 불러온 캐릭터 기본 정보와 스탯 정보로 시스템 내에서 사용할 캐릭터 정보로 변환
     * @param ocid 캐릭터 ocid
     * @param basicInfo 캐릭터 기본 정보
     * @param totalStat 캐릭터 종합 능력치 정보
     * @return Character 객체 (기본정보 + 전투력)
     */
    public Character rawDataToCharacter(String ocid, CharacterBasicInfo basicInfo,
                                        CharacterTotalStat totalStat, CharacterPopularity characterPopularity,
                                        CharacterUnion characterUnion, CharacterAbility characterAbility, CharacterDojang dojang){
        Character character = new Character();

        character.setOcid(ocid);
        character.setCharacterImage(basicInfo.getCharacterImage());
        character.setCharacterName(basicInfo.getCharacterName());
        character.setWorldName(basicInfo.getWorldName());
        character.setCharacterClass(basicInfo.getCharacterClass());
        character.setCharacterLevel(basicInfo.getCharacterLevel());
        character.setCharacterGuildName(basicInfo.getCharacterGuildName());

        character.setDojangFloor(dojang.getDojangBestFloor());
        character.setCharacterPopularity(characterPopularity.getPopularity());
        character.setCharacterUnionLevel(characterUnion.getUnionLevel());
        character.setCharacterAbility(characterAbility.getAbilityInfo());

        character.setDate(basicInfo.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        character.setAssault(totalStat.getFinalStat()
                .get(FinalStatOrder.ASSAULT.getOrder())
                .getStatValue()
                .longValue());

        return character;
    }
}
