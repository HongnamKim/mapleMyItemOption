package com.example.mapleMyItemOption.web.controller.homeController;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.characterSearch.CharacterSearchService;
import com.example.mapleMyItemOption.domain.item.ItemSlot;
import com.example.mapleMyItemOption.domain.item.MyItemData.Item;
import com.example.mapleMyItemOption.domain.item.PresetItemAnalyzer;
import com.example.mapleMyItemOption.domain.item.itemSearch.ItemSearchService;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.itemSearch.PresetTotalStat;
import com.example.mapleMyItemOption.domain.item.PotentialOption;
import com.example.mapleMyItemOption.domain.searchHistory.SearchHistoryService;
import com.example.mapleMyItemOption.web.SessionConst;
import com.example.mapleMyItemOption.web.argumentResolver.SessionCharacter;
import com.example.mapleMyItemOption.web.argumentResolver.SessionMyItemEquipment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final CharacterSearchService characterSearchService;
    private final ItemSearchService itemSearchService;
    private final SearchHistoryService searchHistoryService;

    @GetMapping("/")
    public String home(@ModelAttribute("characterDto") CharacterDto dto, Model model){
        int hour = LocalTime.now().getHour();
        int minusDay = 1;
        if(hour < 1) {
            minusDay = 2;
        }
        String maxDate = LocalDate.now().minusDays(minusDay).toString();

        dto.setDate(maxDate);
        model.addAttribute("maxDate", maxDate);
        return "home";
    }

    @PostMapping("/")
    public String homeSearch(HttpServletRequest request, HttpServletResponse response,
                             @Validated @ModelAttribute("characterDto") CharacterDto dto, BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()){ // 캐릭터 이름 검증
            log.info("SEARCH ERROR = {}", bindingResult);

            return "home";
        }

        LocalDate inputDate = LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_DATE);
        LocalDate maxDate = LocalDate.now().minusDays(1);

        if(inputDate.isAfter(maxDate)) { // 기준 날짜 검증
            log.info("DATE ERROR = {}", bindingResult);
            bindingResult.reject("searchDateError", "검색할 수 없는 날짜");

            return "home";
        }

        // 입력 이름 내 공백 삭제
        String characterName = dto.characterName.strip().replace(" ","");

        //POST 로 들어온 캐릭터 이름, 날짜로 캐릭터 조회, 필요한 객체 생성
        //세선에 객체 넣기
        try {
            String date = dto.getMaximumAssaultDate() ? characterSearchService.findMaximumAssaultDate(characterName, dto.getDate()) : dto.getDate();

            //searchHistoryService.saveSearchHistory(dto.getCharacterName(), dto.getDate(), dto.getMaximumAssaultDate());

            /*if(dto.getMaximumAssaultDate()){
                date = characterSearchService.findMaximumAssaultDate(characterName, dto.getDate());
            }*/

            Character character = characterSearchService.searchMyCharacter(characterName, date);
            MyItemEquipment myItemEquipment = itemSearchService.searchMyItemEquipment(characterName, date);

            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.CHARACTER, character);
            session.setAttribute(SessionConst.MY_ITEM_EQUIPMENT, myItemEquipment);

            Integer presetNo = myItemEquipment.getPresetNo();

            return "redirect:/my-character/" + presetNo;

        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            response.sendError(e.getStatusCode().value());
        }

        return null;
    }

    @GetMapping("/my-character/{preset}")
    public String name(@PathVariable("preset") Integer preset,
                       @ModelAttribute("characterDto") CharacterDto dto, Model model,
                       @SessionCharacter Character character,
                       @SessionMyItemEquipment MyItemEquipment myItemEquipment){


        if(character == null || myItemEquipment == null) { // 세션 만료 시 초기화면
            return "redirect:/";
        }


        // 검색 input 초기값 설정
        int hour = LocalTime.now().getHour();
        int minusDay = 1;
        if(hour < 1) {
            minusDay = 2;
        }
        String maxDate = LocalDate.now().minusDays(minusDay).toString();

        model.addAttribute("maxDate", maxDate);
        dto.setCharacterName(character.getCharacterName());
        dto.setDate(character.getDate().toString());

        if(character.getWorldName().contains("리부트")){ // 리부트 유저면 주문서, 에디 렌더링 X
            model.addAttribute("isNormalServer", false);
        }else{
            model.addAttribute("isNormalServer", true);
        }

        // 전투력 자릿수 분리
        List<Long> numberUnits = separateNumberUnits(character.getAssault());
        model.addAttribute("numberUnits", numberUnits);

        model.addAttribute("savedPresetNo", myItemEquipment.getPresetNo()); // API 에 저장된 프리셋 번호
        model.addAttribute("presetNo", preset); // 조회하려는 프리셋 번호
        model.addAttribute(character);

        // 장비 프리셋의 평균 수치
        List<PresetTotalStat> presetTotalStats = itemSearchService.getPresetTotalStats(myItemEquipment, character);
        PresetTotalStat presetTotalStat = presetTotalStats.get(preset - 1);
        model.addAttribute("presetTotalStat", presetTotalStat);




        // 장비 프리셋 아이템 목록
        model.addAttribute("weaponList", new ArrayList<>(List.of("무기", "보조무기", "엠블렘")));
        //model.addAttribute("presetItemEquipment", itemSearchService.getPresetItemStats(myItemEquipment, character, preset));
        Map<String, Item> presetItemStatsWeapons = itemSearchService.getPresetItemStats(myItemEquipment, character, preset, ItemSlot.WEAPONS);
        Map<String, Item> presetItemStatsArmors = itemSearchService.getPresetItemStats(myItemEquipment, character, preset, ItemSlot.ARMORS);
        Map<String, Item> presetItemStatsAccessories = itemSearchService.getPresetItemStats(myItemEquipment, character, preset, ItemSlot.ACCESSORIES);
        Map<String, Item> presetItemStatsOthers = itemSearchService.getPresetItemStats(myItemEquipment, character, preset, ItemSlot.OTHERS);

        PresetItemAnalyzer presetItemAnalyzer = new PresetItemAnalyzer();
        Map<Integer, Float> averageAddOption = presetTotalStat.getAverageAddOption();
        presetItemAnalyzer.compareAddOption(averageAddOption, presetItemStatsArmors);
        presetItemAnalyzer.compareAddOption(averageAddOption, presetItemStatsAccessories);
        presetItemAnalyzer.compareAddOption(averageAddOption, presetItemStatsOthers);

        Float averageStarforce = presetTotalStat.getAverageStarforce();
        presetItemAnalyzer.compareStarforce(averageStarforce, presetItemStatsWeapons);
        presetItemAnalyzer.compareStarforce(averageStarforce, presetItemStatsArmors);
        presetItemAnalyzer.compareStarforce(averageStarforce, presetItemStatsAccessories);
        presetItemAnalyzer.compareStarforce(averageStarforce, presetItemStatsOthers);

        model.addAttribute("itemWeapons", presetItemStatsWeapons);
        model.addAttribute("itemArmors", presetItemStatsArmors);
        model.addAttribute("itemAccessories", presetItemStatsAccessories);
        model.addAttribute("itemOthers", presetItemStatsOthers);


        model.addAttribute("averageList", PotentialOption.AVERAGE_LIST_SHORTEN); // 잠재 옵션 표시 조건문을 위함
        model.addAttribute("totalList", PotentialOption.TOTAL_LIST_SHORTEN); // 잠재 옵션 표시 조건문을 위함

        return "my-character";
    }

    private List<Long> separateNumberUnits(Long assault){
        List<Long> numberUnits = new ArrayList<>();

        Long assault_a = assault / 100_000_000;

        assault = assault % 100_000_000;
        Long assault_b = assault / 10_000;
        Long assault_c = assault % 10_000;

        numberUnits.add(assault_a);
        numberUnits.add(assault_b);
        numberUnits.add(assault_c);

        return numberUnits;
    }

}
