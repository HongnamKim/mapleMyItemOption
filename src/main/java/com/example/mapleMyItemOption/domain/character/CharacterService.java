package com.example.mapleMyItemOption.domain.character;

import com.example.mapleMyItemOption.domain.character.rawCharaterData.*;
import com.example.mapleMyItemOption.api.ApiService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CharacterService {
    // UserBasic 테이블을 만드는 클래스
    /*
    1. 랭킹 조회 --> 닉네임 리스트
    2. 닉네임 리스트 --> 닉네임 : ocid map 으로 변환
    3. ocid 리스트 --> ocid 별로 basicInfo, totalStat 조회
    4. Character 객체 저장
     */
    private final ApiService apiService;

    public List<String> getRanking(long limitLevel) {
        String date = LocalDate.now().minusDays(1).toString();
        List<String> characterRankingList = new ArrayList<>();

        int page = 1;
        while(true){ // 260 레벨 이상 랭킹 닉네임 fetch (임시로 10 페이지 제한)
            Ranking ranking = apiService.fetchCharacterRanking(date, page);

            // 받아온 페이지의 랭킹 정보 List
            List<CharacterRanking> rankingList = ranking.getRanking();
            // 페이지의 마지막 정보
            CharacterRanking lastRanking = rankingList.get(rankingList.size() - 1);
            if(lastRanking.getCharacterLevel() < limitLevel || page > 1){
                break; // 260 이하면 api 요청 중지, 테스트 용으로 1 페이지 제한
            }
            page++;

            for(CharacterRanking characterRanking : rankingList){
                characterRankingList.add(characterRanking.getCharacterName());
            }
            // (요청 횟수)/대기 시간 으로 효율 계산
            // 0.5초 대기 시 429 : 91개 -> 45.5초 대기 --> 2.20/sec
            // 1초 대기 시 429 : 47개 -> 47초 대기     --> 2.13/sec
            // 3초 대기 시 429 : 16개 -> 48초 대기     --> 2.08/sec
            // 5초 대기 시 429 : 10개 -> 50초 대기     --> 2/sec
            // 10초 대기 시 429 : 5개 -> 50초 대기     --> 2/sec
            // 20초 대기 시 429 : 3개 -> 60초 대기     --> 1.67/sec

            // 요청 처리 큐에 한계 이상으로 요청이 들어올 경우 바로 429 를 보내고 요청을 처리하지 않는 것 같음 --> 요청 횟수에 포함되지 않음
            // 대기 시간으로 요청 처리 큐를 비우는
        }

        return characterRankingList;
    }

    public Map<String, String> getOcid(List<String> rankingList){
        Map<String, String> ocidMap = new LinkedHashMap<>();

        for(String characterName : rankingList){
            String ocid = apiService.fetchCharacterOcid(characterName);
            while(ocid.equals("429")){ // 429 too many request 응답 시 재요청
                try{
                    TimeUnit.MILLISECONDS.sleep(500);
                    ocid = apiService.fetchCharacterOcid(characterName);
                } catch (InterruptedException e){
                    System.out.println(e.getMessage());
                }
            }

            ocidMap.put(characterName, ocid);
        }
        return ocidMap;
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
                                        CharacterUnion characterUnion,CharacterDojang dojang){
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

        character.setDate(basicInfo.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        character.setAssault(totalStat.getFinalStat()
                .get(FinalStatOrder.ASSAULT.getOrder())
                .getStatValue()
                .longValue());

        return character;
    }

    /**
     * 최근 1주동안 가장 높은 전투력의 정보를 가져오는 메소드
     * @param characterName 검색하고자 하는 캐릭터 닉네임
     * @param date 기준 날짜
     * @return Character 객체
     */
    public Character searchMyCharacter(String characterName, String date) {
        String ocid = apiService.fetchCharacterOcid(characterName);
        String maximumAssaultDate = findMaximumAssaultDate(ocid, date);

        CharacterBasicInfo characterBasicInfo = apiService.fetchCharacterBasicInfo(ocid, maximumAssaultDate);
        CharacterTotalStat characterTotalStat = apiService.fetchCharacterTotalStat(ocid, maximumAssaultDate);
        CharacterDojang characterDojang = apiService.fetchCharacterDojang(ocid, maximumAssaultDate);
        CharacterPopularity characterPopularity = apiService.fetchCharacterPopularity(ocid, maximumAssaultDate);
        CharacterUnion characterUnion = apiService.fetchCharacterUnion(ocid, maximumAssaultDate);

        return rawDataToCharacter(ocid, characterBasicInfo, characterTotalStat, characterPopularity, characterUnion, characterDojang);
    }

    /**
     * 최근 2주동안 가장 높은 전투력의 날짜를 조회
     * @param ocid 조회할 캐릭터의 ocid
     * @param date 기준 날짜
     * @return 기준 날짜 2주 전 ~ 기준 날짜 사이에 가장 높은 전투력의 날짜
     */
    public String findMaximumAssaultDate(String ocid, String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        LocalDate minDate = LocalDate.of(2024, 1, 17);

        LocalDate maximumAssaultDate = localDate;
        long maximumAssault = apiService.fetchCharacterTotalStat(ocid, date)
                .getFinalStat()
                .get(FinalStatOrder.ASSAULT.getOrder()).getStatValue().longValue();

        for(int i = 0; i < 7; i++){
            LocalDate beforeDate = localDate.minusDays(i);
            if(beforeDate.isEqual(minDate)){
                break;
            }
            long assault = apiService.fetchCharacterTotalStat(ocid, beforeDate.toString())
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
}
