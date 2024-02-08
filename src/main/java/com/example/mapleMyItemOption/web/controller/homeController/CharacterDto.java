package com.example.mapleMyItemOption.web.controller.homeController;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class CharacterDto {
    @NotEmpty
    String characterName;
    @NotBlank
    String date;

    Boolean maximumAssaultDate;
}
