package com.example.mapleMyItemOption.domain.character.characterStatistics;

import com.example.mapleMyItemOption.api.ApiService;
import com.example.mapleMyItemOption.domain.character.charaterDataDto.CharacterRanking;
import com.example.mapleMyItemOption.domain.character.charaterDataDto.Ranking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CharacterStatisticsService {
    // UserBasic 테이블을 만드는 클래스
    /*
    1. 랭킹 조회 --> 닉네임 리스트
    2. 닉네임 리스트 --> 닉네임 : ocid map 으로 변환
    3. ocid 리스트 --> ocid 별로 basicInfo, totalStat 조회
    4. Character 객체 저장
     */

    private final ApiService apiService;


    /**
     * 게임 내 limiteLevel 이상의 랭킹 정보 가져오기
     * 1페이지 당 200 명
     * @param limitLevel 랭킹 내 최소 레벨 제한
     * @return 캐릭터 이름 리스트
     */
    public List<String> getRanking(long limitLevel) {
        String date = LocalDate.now().minusDays(1).toString();
        List<String> characterRankingList = new ArrayList<>();

        // test 용 1 페이지 데이터만 fetch
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

    /**
     * 외부 api 로 불러온 닉네임 리스트로 ocid 를 불러오는 메소드
     * @param rankingList ocid 를 불러올 캐릭터 닉네임 리스트
     * @return Map<characterName, ocid> 형태의 맵
     */
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

}
