package com.example.mapleMyItemOption.web.exceptionhandler;

public enum ErrorCode {
    OPENAPI00001 ("서버 내부 오류"),
    OPENAPI00002 ("권한 없음"),
    OPENAPI00003 ("유요하지 않은 식별자"),
    OPENAPI00004("파라미터 누락 또는 유효하지 않음"),
    OPENAPI0005("유효하지 않은 API KEY"),
    OPENAPI0006("유효하지 않은 게임 또는 API PATH"),
    OPENAPI0007("API 호출량 초과"),
    OPENAPI0009("데이터 준비 중"),
    OPENAPI0010("서비스 점검 중"),

    SERVICE0001("캐릭터 생성 이전 날짜 조회"),
    SERVICE0002("조회 가능 이전 날짜 조회");

    private final String description;
    ErrorCode(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
