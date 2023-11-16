package com.example.Ahi.controller;


import lombok.Data;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class TestController {
    @GetMapping("/test")
    public outputDto getTest(){
        outputDto outputDto = new outputDto();
        outputDto.output = "GET-확인되었습니다.";
        return outputDto;
    }
    @PostMapping("/test")
    public outputDto postTest(@RequestBody String input){
        outputDto outputDto = new outputDto();
        outputDto.output = input+" POST-확인되었습니다.";
        return outputDto;
    }

    @PostMapping("/test/json")
    public outputDto jsonTest(@RequestBody inputDto inputDto){
        outputDto outputDto = new outputDto();
        outputDto.output = inputDto.getInput()+" POST-확인되었습니다.";
        return outputDto;
    }

}


@Data
@Getter
class outputDto{
    String output;
}

@Data
@Getter
class inputDto{
    String input;
}