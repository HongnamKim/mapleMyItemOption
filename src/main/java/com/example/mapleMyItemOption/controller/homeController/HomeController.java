package com.example.mapleMyItemOption.controller.homeController;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.CharacterService;
import com.example.mapleMyItemOption.domain.item.ItemService;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.MyItemData.PresetTotalStat;
import com.example.mapleMyItemOption.domain.item.PotentialOption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final CharacterService characterService;
    private final ItemService itemService;
//    private final ItemAnalyzer itemAnalyzer;

    @GetMapping("/")
    public String home(@ModelAttribute("characterDto") CharacterDto dto, Model model){
        String maxDate = LocalDate.now().minusDays(1).toString();
        dto.setDate(maxDate);
        model.addAttribute("maxDate", maxDate);
        return "home";
    }

    @PostMapping("/")
    public String homeSearch(HttpServletRequest request, HttpServletResponse response,
                             @Validated @ModelAttribute("characterDto") CharacterDto dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {

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
            session.setAttribute("character", character);
            session.setAttribute("myItemEquipment", myItemEquipment);

            return "redirect:/my-character";
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

    @GetMapping("/my-character")
    public String name(HttpServletRequest request, HttpServletResponse response,
                       @ModelAttribute("characterDto") CharacterDto dto, Model model) throws IOException {

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.sendError(400, "Bad Request");
        }

        try{
            assert session != null;
            Character character = (Character) session.getAttribute("character");
            MyItemEquipment myItemEquipment = (MyItemEquipment) session.getAttribute("myItemEquipment");

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

            model.addAttribute("presetNo", myItemEquipment.getPresetNo());
            model.addAttribute(character);

            List<PresetTotalStat> presetTotalStats = itemService.getPresetTotalStats(myItemEquipment, character, false);
            model.addAttribute("presetTotalStats",presetTotalStats);
            model.addAttribute("averageList", PotentialOption.AVERAGE_LIST); // 잠재 옵션 표시 조건문을 위함
            model.addAttribute("totalList", PotentialOption.TOTAL_LIST); // 잠재 옵션 표시 조건문을 위함

            // return "my-character-" + pathVariable --> 경로 변수에 따라서 view 선택 반환
            return "my-character";

        } catch (NullPointerException exception) {
            response.sendError(400, "Bad Request");
        } catch (AssertionError exception) {
            response.sendError(400, "Session Expire");
        }

        return null;
    }

}
