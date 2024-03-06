package com.example.mapleMyItemOption.api;

import com.example.mapleMyItemOption.domain.character.charaterDataDto.Ranking;
import com.example.mapleMyItemOption.domain.character.charaterDataDto.*;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Getter
@Setter
@RequiredArgsConstructor
@Service
public class ExternalApiService implements ApiService{
    /*
    ApiService Interface 에서 정의함.
    private final String API_URL = "https://open.api.nexon.com/maplestory/v1";
    private final  String HEADER_NAME = "x-nxopen-api-key";
     */

    // application.properties 정의
    @Value("${api.key}")
    private String API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    /**
     * 캐릭터 식별자(ocid) 조회
     * 조회 실패 시 응답 코드 반환 400(캐릭터 존재 X) 429(너무 많은 요청)
     * https://open.api.nexon.com/maplestory/v1/id?character_name={characterName}
     * @param characterName 캐릭터 이름
     * @return character OCID String
     */
    @Override
    public String fetchCharacterOcid(String characterName) {

        String fullApiUrl = API_URL + replaceParameters(ApiUrl.OCID.getUrl(), characterName);

        try {
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, CharacterOcid.class).getOcid();

        } catch (HttpClientErrorException e){ // 429 코드 수신 시

            String message = e.getMessage();
            String statusCode = message.substring(0, 3);

            return statusCode;

        } catch (JsonProcessingException e) { // 객체 변환 실패 시
            throw new RuntimeException(e);
        }
    }

    /**
     * 종합 랭킹 정보 조회
     * https://open.api.nexon.com/maplestory/v1/ranking/overall?date={date}
     * @param date 조회 기준 날짜 ("yyyy-mm-dd", 이전 날짜만 가능)
     * @return Ranking json
     */
    @Override
    public Ranking fetchCharacterRanking(String date, int page) {
        String fullApiUrl = API_URL + replaceParameters(ApiUrl.CHARACTER_RANKING.getUrl(), date, String.valueOf(page));

        try{
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, Ranking.class);
        } catch (HttpClientErrorException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 캐릭터 기본 정보 조회
     * https://open.api.nexon.com/maplestory/v1/character/basic?ocid={ocid}&date={date}
     * @param ocid 조회할 캐릭터 ocid
     * @param date 조회 기준 날짜 ("yyyy-mm-dd", 이전 날짜만 가능)
     * @return 캐릭터 기본 정보 객체
     */
    @Override
    public CharacterBasicInfo fetchCharacterBasicInfo(String ocid, String date){

        String fullApiUrl = API_URL + replaceParameters(ApiUrl.CHARACTER_BASIC.getUrl(), ocid, date);

        try {
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);

            String result = responseEntity.getBody();

            return objectMapper.readValue(result, CharacterBasicInfo.class);


        } catch (HttpClientErrorException e){
            /*try{
                clientErrorExHandler(e);
            } catch (JsonProcessingException ex){
                throw new RuntimeException();
            }*/

            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void clientErrorExHandler(HttpClientErrorException exception) throws JsonProcessingException {
        String responseBodyAsString = exception.getResponseBodyAsString();
        System.out.println(responseBodyAsString);
        ErrorMessage errorMessage = objectMapper.readValue(responseBodyAsString, ErrorMessage.class);
        System.out.println(errorMessage);
        HttpStatusCode statusCode = exception.getStatusCode();
        //System.out.println(statusCode.value());

    }

    /**
     * 캐릭터 종합 능력치 조회
     * https://open.api.nexon.com/maplestory/v1/character/stat?ocid={ocid}&date={date}
     * @param ocid 캐릭터 ocid
     * @param date 조회 기준 날짜 ("yyyy-mm-dd", 이전 날짜만 가능)
     * @return 캐릭터 종합 능력치 객체
     */
    @Override
    public CharacterTotalStat fetchCharacterTotalStat(String ocid, String date){
        String fullApiUrl = API_URL + replaceParameters(ApiUrl.CHARACTER_STAT.getUrl(), ocid, date);

        try{
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, CharacterTotalStat.class);

        } catch (HttpClientErrorException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CharacterPopularity fetchCharacterPopularity(String ocid, String date) {
        String fullApiUrl = API_URL + replaceParameters(ApiUrl.POPULARITY.getUrl(), ocid, date);

        try{
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, CharacterPopularity.class);
        } catch (HttpClientErrorException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CharacterDojang fetchCharacterDojang(String ocid, String date) {
        String fullApiUrl = API_URL + replaceParameters(ApiUrl.DOJANG.getUrl(), ocid, date);


        try {
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, CharacterDojang.class);
        } catch(HttpClientErrorException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CharacterUnion fetchCharacterUnion(String ocid, String date) {
        String fullApiUrl = API_URL + replaceParameters(ApiUrl.UNION.getUrl(), ocid, date);

        try {
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, CharacterUnion.class);
        } catch(HttpClientErrorException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CharacterAbility fetchCharacterAbility(String ocid, String date) {
        String fullApiUrl = API_URL + replaceParameters(ApiUrl.ABILITY.getUrl(), ocid, date);

        try{
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, CharacterAbility.class);

        } catch (HttpClientErrorException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MyItemEquipment fetchMyItemEquipment(String ocid, String date) {
        String fullApiUrl = API_URL + replaceParameters(ApiUrl.ITEM_EQUIPMENT.getUrl(), ocid, date);

        try{
            ResponseEntity<String> responseEntity = getResponseEntity(fullApiUrl);
            String result = responseEntity.getBody();

            return objectMapper.readValue(result, MyItemEquipment.class);
        } catch (HttpClientErrorException e){
            throw e;
        } catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    private HttpEntity<String> createRequestEntityWithApiKeyHeader(){
        // API 키를 포함한 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, API_KEY);
        // HttpEntity 에 헤더 포함
        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> getResponseEntity (String fullApiUrl) {
        HttpEntity<String> requestEntity = createRequestEntityWithApiKeyHeader();

        return restTemplate.exchange(fullApiUrl, HttpMethod.GET, requestEntity, String.class);
    }

    private String replaceParameters(String baseUrl, String... parameters){
        for (String parameter : parameters) {
            baseUrl = baseUrl.replaceFirst("\\{}", parameter);
        }

        return baseUrl;
    }
}
