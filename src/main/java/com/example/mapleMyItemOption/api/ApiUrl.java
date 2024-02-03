package com.example.mapleMyItemOption.api;

public enum ApiUrl {
    OCID ("/id?character_name={}"), // 캐릭터 식별자(ocid) 조회
    CHARACTER_BASIC("/character/basic?ocid={}&date={}"), // 기본 정보 조회
    CHARACTER_STAT("/character/stat?ocid={}&date={}"), // 종합 능력치 정보 조회
    ITEM_EQUIPMENT("/character/item-equipment?ocid={}&date={}"), // 장착 장비 정보 조회 (캐시 장비 제외)
    CHARACTER_RANKING("/ranking/overall?date={}&page={}"),
    POPULARITY("/character/popularity?ocid={}&date={}"),
    UNION("/user/union?ocid={}&date={}"),
    ABILITY("/character/ability?ocid={}&date={}"),
    DOJANG("/character/dojang?ocid={}&date={}");


    private final String url;

    ApiUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

}
