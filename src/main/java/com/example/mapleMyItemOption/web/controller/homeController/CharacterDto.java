package com.example.mapleMyItemOption.web.controller.homeController;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CharacterDto {
    @NotBlank
    String characterName;
    @NotBlank
    String date;
}
