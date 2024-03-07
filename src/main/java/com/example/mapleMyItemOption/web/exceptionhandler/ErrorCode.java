package com.example.mapleMyItemOption.web.exceptionhandler;

public enum ErrorCode {
    OPENAPI00001 ("OPENAPI00001","서버 내부 오류"),
    OPENAPI00002 ("OPENAPI00002", "권한 없음"),
    OPENAPI00003 ("OPENAPI00003", "유효하지 않은 식별자"),
    OPENAPI00004("OPENAPI00004", "파라미터 누락 또는 유효하지 않음"),
    OPENAPI0005("OPENAPI00005", "유효하지 않은 API KEY"),
    OPENAPI0006("OPENAPI00006", "유효하지 않은 게임 또는 API PATH"),
    OPENAPI0007("OPENAPI00007", "API 호출량 초과"),
    OPENAPI0009("OPENAPI00009", "데이터 준비 중"),
    OPENAPI0010("OPENAPI00010", "서비스 점검 중"),

    SERVICE00001("SERVICE00001", "캐릭터 생성 이전 날짜 조회"),
    SERVICE00002("SERVICE00002","조회 가능 이전 날짜 조회");

    private final String errorCode;
    private final String description;
    private ErrorCode(String errorCode, String description){
        this.errorCode = errorCode;
        this.description = description;
    }

    public String getErrorCode(){
        return errorCode;
    }

    public String getDescription(){
        return description;
    }
}
