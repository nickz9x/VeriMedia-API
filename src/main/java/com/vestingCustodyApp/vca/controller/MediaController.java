package com.vestingCustodyApp.vca.controller;

import com.vestingCustodyApp.vca.dto.MediaRegisterRequestDto;
import com.vestingCustodyApp.vca.dto.MediaResponseDto;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.service.MediaService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/media")
public class MediaController {
    private MediaService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/register")
    public ResponseEntity<MediaResponseDto> createMedia(@RequestPart("file")MultipartFile file, @ModelAttribute MediaRegisterRequestDto data, Authentication authentication){
        return ResponseEntity.ok(service.registerMedia(file,data,authentication));
    }

    @GetMapping
    public ResponseEntity<List<Media>> listAll(){
        return ResponseEntity.ok(service.listAllMedia());
    }
}
