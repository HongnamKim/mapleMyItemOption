package com.example.mapleMyItemOption.api;

import com.example.mapleMyItemOption.domain.character.rawCharaterData.CharacterBasicInfo;
import com.example.mapleMyItemOption.domain.character.rawCharaterData.CharacterOcid;
import com.example.mapleMyItemOption.domain.character.rawCharaterData.CharacterTotalStat;
import com.example.mapleMyItemOption.domain.item.MyItemData.MyItemEquipment;
import com.example.mapleMyItemOption.domain.item.rawItemData.RawItemEquipment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InternalApiService implements ApiService{

    private final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @Override
    public String fetchCharacterOcid(String characterName) {
        String jsonFilePath = "testJson/ocid.json";

        Resource resource = new ClassPathResource(jsonFilePath);
        try {
            String jsonString = readJsonFile(resource);
            return objectMapper.readValue(jsonString, CharacterOcid.class).getOcid();


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public CharacterBasicInfo fetchCharacterBasicInfo(String ocid, String date){
        String jsonFilePath = "testJson/basicInfo.json";
        Resource resource = new ClassPathResource(jsonFilePath);
        try{
            String jsonString = readJsonFile(resource);

            return objectMapper.readValue(jsonString, CharacterBasicInfo.class);

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public CharacterTotalStat fetchCharacterTotalStat(String ocid, String date){
        String jsonFilePath = "testJson/totalStat.json";

        Resource resource = new ClassPathResource(jsonFilePath);
        try{
            String jsonString = readJsonFile(resource);

            return objectMapper.readValue(jsonString, CharacterTotalStat.class);

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public RawItemEquipment fetchItemEquipment(String ocid, String date){
        String jsonFilePath = "testJson/itemEquipment.json";

        Resource resource = new ClassPathResource(jsonFilePath);

        try{
            String jsonString = readJsonFile(resource);

            return objectMapper.readValue(jsonString, RawItemEquipment.class);

        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public MyItemEquipment fetchMyItemEquipment(String ocid, String date) {
        String jsonFilePath = "testJson/itemEquipment.json";

        Resource resource = new ClassPathResource(jsonFilePath);

        try{
            String jsonString = readJsonFile(resource);


            return objectMapper.readValue(jsonString, MyItemEquipment.class);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private String readJsonFile(Resource resource) throws IOException{
        try{
            byte[] jsonBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return new String(jsonBytes, StandardCharsets.UTF_8);
        } catch (IOException e){
            throw e;
        }
    }
}
