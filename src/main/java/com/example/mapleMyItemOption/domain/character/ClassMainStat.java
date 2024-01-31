package com.example.mapleMyItemOption.domain.character;

import java.util.ArrayList;
import java.util.List;

public interface ClassMainStat {
    List<String> STR_CLASS = new ArrayList<>(List.of("히어로", "팔라딘", "다크나이트", "소울마스터", "미하일", "블래스터", "데몬 슬레이어", "아란", "카이저", "아델", "제로", "바이퍼", "캐논슈터", "스트라이커", "은월", "아크"));
    List<String> DEX_CLASS = new ArrayList<>(List.of("보우마스터", "신궁", "패스파인더", "윈드브레이커", "와일드헌터", "메르세데스", "카인", "캡틴", "메카닉", "엔젤릭버스터"));
    List<String> LUK_CLASS = new ArrayList<>(List.of("나이트로드", "섀도어", "듀얼블레이드", "나이트워커", "팬텀", "카데나", "칼리", "호영"));
    List<String> INT_CLASS = new ArrayList<>(List.of("아크메이지(불,독)", "아크메이지(썬,콜)", "비숍", "플레임위자드", "베틀메이지", "에반", "루미너스", "일리움", "라라", "키네시스"));

    List<String> HP_CLASS = new ArrayList<>(List.of("데몬 어벤져"));

    List<String> ALL_STAT_CLASS = new ArrayList<>(List.of("제논"));

    String STR = "STR";
    String DEX = "DEX";
    String LUK = "LUK";
    String INT = "INT";
    String HP = "최대 HP";
    String ALL_STAT = "올스탯";
}
