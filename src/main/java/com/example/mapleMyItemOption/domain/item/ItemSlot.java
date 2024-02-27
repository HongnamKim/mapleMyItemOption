package com.example.mapleMyItemOption.domain.item;

import java.util.ArrayList;
import java.util.List;

public interface ItemSlot {

    String FACE_ACC = "얼굴장식";
    String EYE_ACC = "눈장식";
    String EAR_RING = "귀고리";
    String RING_1 = "반지1";
    String RING_2 = "반지2";
    String RING_3 = "반지3";
    String RING_4 = "반지4";
    String PENDANT_1 = "펜던트";
    String PENDANT_2 = "펜던트2";
    String HEART = "기계 심장";
    String HAT = "모자";
    String TOP = "상의";
    String PANTS = "하의";
    String CLOAK = "망토";
    String SHOES = "신발";
    String GLOVE = "장갑";
    String SUB_WEAPON = "보조무기";
    String WEAPON = "무기";
    String MEDAL = "훈장";
    String BELT = "벨트";
    String SHOULDER = "어깨장식";
    String POCKET = "포켓 아이템";
    String BADGE = "뱃지";
    String EMBLEM = "엠블렘";


    List<String> WEAPONS = new ArrayList<>(List.of(WEAPON, SUB_WEAPON, EMBLEM));
    List<String> ARMORS = new ArrayList<>(List.of(HAT, TOP, PANTS, SHOES, GLOVE, CLOAK, SHOULDER));
    List<String> ACCESSORIES = new ArrayList<>(List.of(FACE_ACC, EYE_ACC, EAR_RING, RING_1, RING_2, RING_3, RING_4, PENDANT_1, PENDANT_2, BELT, POCKET, BADGE));
    List<String> OTHERS = new ArrayList<>(List.of(HEART, MEDAL));

    List<String> SLOT_CATEGORY = new ArrayList<>(List.of("무보엠", "방어구", "장신구", "기타 장비"));

}

/*
모자
얼굴장식
눈장식
귀고리
반지1
반지2
반지3
반지4
펜던트
기계 심장
펜던트2
상의
하의
신발
장갑
망토
보조무기
무기
훈장
벨트
어깨장식
포켓 아이템
뱃지
엠블렘
 */