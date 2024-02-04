package com.example.mapleMyItemOption.web.controller.homeController;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.CharacterService;
import com.example.mapleMyItemOption.domain.item.ItemService;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.MyItemData.PresetTotalStat;
import com.example.mapleMyItemOption.domain.item.PotentialOption;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final CharacterService characterService;
    private final ItemService itemService;

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
            bindingResult.reject("searchDateError", "검색할 수 없는 날짜");

            log.info("DATE ERROR = {}", bindingResult);

            return "home";
        }

        //POST 로 들어온 캐릭터 이름, 날짜로 캐릭터 조회, 필요한 객체 생성
        //세선에 객체 넣기
        try {
            Character character = characterService.searchMyCharacter(dto.characterName, dto.getDate());
            MyItemEquipment myItemEquipment = itemService.searchMyItemEquipment(dto.characterName, dto.getDate());

            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.CHARACTER, character);
            session.setAttribute(SessionConst.MY_ITEM_EQUIPMENT, myItemEquipment);

            Integer presetNo = myItemEquipment.getPresetNo();

            return "redirect:/my-character/" + presetNo;

            //return "redirect:/my-character";
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            response.sendError(e.getStatusCode().value());
        }
        /*
        Integer presetNo = myItemEquipment.getPresetNo();
        return "redirect:/my-character/" + presetNo;
         */

        //redirectAttributes.addAttribute("character", dto.getCharacterName());
        //redirectAttributes.addAttribute("date", dto.getDate());

        return null;
    }

    // Character, MyItemEquipment 객체를 PostMapping("/") 에서 생성
    // 두 객체를 session 에 넣고 저장된 프리셋 번호로 redirect
    // ex) return "redirect:/my-character/" + myEquipment.getPresetNo()
    // GetMapping("/my-character/{presetNo}")
    // session 에서 character, myItemEquipment 꺼내서 model 에 담고 view 에 전달

    @GetMapping("/my-character/{preset}")
    public String name(HttpServletResponse response,
                       @PathVariable("preset") Integer preset,
                       @ModelAttribute("characterDto") CharacterDto dto, Model model,
                       @SessionCharacter Character character,
                       @SessionMyItemEquipment MyItemEquipment myItemEquipment) throws IOException {


        if(character == null || myItemEquipment == null) {
            return "redirect:/";
        }

        try{
            // 검색 input 초기값 설정
            dto.setCharacterName(character.getCharacterName());
            dto.setDate(character.getDate().toString());

            if(character.getWorldName().contains("리부트")){
                model.addAttribute("isNormalServer", false);
            }else{
                model.addAttribute("isNormalServer", true);
            }

            // 전투력 자릿수 분리
            Long assault = character.getAssault();
            Long assault_a = assault / 100_000_000;
            assault = assault % 100_000_000;
            Long assault_b = assault / 10_000;
            Long assault_c = assault % 10_000;
            model.addAttribute("assault_a", assault_a);
            model.addAttribute("assault_b", assault_b);
            model.addAttribute("assault_c", assault_c);

            //model.addAttribute("presetNo", myItemEquipment.getPresetNo());
            model.addAttribute("presetNo", preset);
            model.addAttribute(character);

            List<PresetTotalStat> presetTotalStats = itemService.getPresetTotalStats(myItemEquipment, character, false);
            PresetTotalStat presetTotalStat = presetTotalStats.get(preset - 1);
            model.addAttribute("presetTotalStat", presetTotalStat);

            switch (preset){
                case 1 -> model.addAttribute("presetItemEquipment", myItemEquipment.getPreset1());
                case 2 -> model.addAttribute("presetItemEquipment", myItemEquipment.getPreset2());
                case 3 -> model.addAttribute("presetItemEquipment", myItemEquipment.getPreset3());
            }


            model.addAttribute("presetTotalStats",presetTotalStats);
            model.addAttribute("averageList", PotentialOption.AVERAGE_LIST); // 잠재 옵션 표시 조건문을 위함
            model.addAttribute("totalList", PotentialOption.TOTAL_LIST); // 잠재 옵션 표시 조건문을 위함

            // return "my-character-" + pathVariable --> 경로 변수에 따라서 view 선택 반환
            return "my-character-1";

        } catch (NullPointerException exception) {
            System.out.println(exception.getMessage());
            response.sendError(400, "Bad Request");
            return "home";
        }
    }

}
