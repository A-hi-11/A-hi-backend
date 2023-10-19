package com.example.Ahi.controller;


// Controller
import com.example.Ahi.service.DiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiffusionController {

    @Autowired
    private DiffusionService diffusionService;

    @PostMapping("/diffusion")
    public ResponseEntity<byte[]> getDiffusion(@RequestBody String prompt) {
        return diffusionService.getDiffusion(prompt);
    }
}