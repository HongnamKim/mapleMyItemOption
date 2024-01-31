package com.example.mapleMyItemOption.controller.homeController;

import com.example.mapleMyItemOption.domain.character.Character;
import com.example.mapleMyItemOption.domain.character.CharacterService;
import com.example.mapleMyItemOption.domain.item.ItemService;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.MyItemData.PresetTotalStat;
import com.example.mapleMyItemOption.domain.item.PotentialOption;
import jakarta.servlet.http.HttpServletResponse;
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
    public String homeSearch(@Validated @ModelAttribute("characterDto") CharacterDto dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()){
            log.info("SEARCH ERROR = {}", bindingResult);
            return "home";
        }

        LocalDate inputDate = LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_DATE);
        LocalDate maxDate = LocalDate.now().minusDays(1);

        if(inputDate.isAfter(maxDate)) {
            bindingResult.reject("searchDateError", "검색할 수 없는 날짜");

            log.info("DATE ERROR = {}", bindingResult);

            return "home";
        }

        redirectAttributes.addAttribute("character", dto.getCharacterName());
        redirectAttributes.addAttribute("date", dto.getDate());

        return "redirect:/my-character";
    }


    @GetMapping("/my-character")
    public String name(HttpServletResponse response,
                       @ModelAttribute("characterDto") CharacterDto dto,
                       @RequestParam("character") String characterName,
                       @RequestParam("date") String date, Model model) throws IOException {

        // 2주간 최대 전투력의 캐릭터 정보/날짜 불러오기
        Character character;
        try {
            character = characterService.searchMyCharacter(characterName, date);
            if(character.getWorldName().contains("리부트")){
                model.addAttribute("isNormalServer", false);
            }else{
                model.addAttribute("isNormalServer", true);
            }

            String maximumAssaultDate = character.getDate().toString();
            model.addAttribute(character);

            // 전투력 자릿수 분리
            Long assault = character.getAssault();
            Long assault_a = assault / 100_000_000;
            assault = assault % 100_000_000;
            Long assault_b = assault / 10_000;
            Long assault_c = assault % 10_000;
            model.addAttribute("assault_a", assault_a);
            model.addAttribute("assault_b", assault_b);
            model.addAttribute("assault_c", assault_c);

            // 2주간 최대 전투력의 장비 불러오기
            MyItemEquipment myItemEquipment = itemService.searchMyItemEquipment(characterName, maximumAssaultDate);

            model.addAttribute("presetNo", myItemEquipment.getPresetNo());

            List<PresetTotalStat> presetTotalStats = itemService.getPresetTotalStats(myItemEquipment, character, false);
            model.addAttribute("presetTotalStats",presetTotalStats);
            model.addAttribute("averageList", PotentialOption.AVERAGE_LIST);
            model.addAttribute("totalList", PotentialOption.TOTAL_LIST);

            return "my-character";

        } catch (HttpClientErrorException e) {
            response.sendError(e.getStatusCode().value());
        }

        return null;
    }

}
