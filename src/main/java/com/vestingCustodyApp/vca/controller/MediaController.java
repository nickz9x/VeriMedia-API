package com.vestingCustodyApp.vca.controller;

import com.vestingCustodyApp.vca.dto.MediaRegisterRequestDto;
import com.vestingCustodyApp.vca.dto.MediaResponseDto;
import com.vestingCustodyApp.vca.dto.PublicMediaResponse;
import com.vestingCustodyApp.vca.dto.RequestReviewMediaDto;
import com.vestingCustodyApp.vca.service.MediaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<MediaResponseDto>> listAll(){
        return ResponseEntity.ok(service.listAllMedia());
    }

    @GetMapping("/search/pending")
    public ResponseEntity<List<MediaResponseDto>> listAllMediaPending(){return ResponseEntity.ok(service.listAllPendingMedia());}

    @GetMapping("/search/rejected")
    public ResponseEntity<List<MediaResponseDto>> listAllMediaRejected(){return ResponseEntity.ok(service.listAllRejectedMedia());}

    @GetMapping("/search/verified")
    public ResponseEntity<List<MediaResponseDto>> listAllMediaVerified(){return ResponseEntity.ok(service.listAllVerifiedMedia());}

    @PostMapping("/search/{id}/approve")
    public ResponseEntity<MediaResponseDto> aproveMedia(@PathVariable Long id){
        return ResponseEntity.ok(service.aproveMedia(id));
    }


    @PostMapping("/request-review/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void requestReview(@PathVariable Long id,@RequestBody String reason){
        service.requestReview(id,reason);
    }

    @GetMapping("/request-review")
    public ResponseEntity<List<RequestReviewMediaDto>> listRequestResponse(){
        return ResponseEntity.ok(service.listRequestReview());
    }

    @GetMapping("/public/search/{publicToken}")
    public ResponseEntity<PublicMediaResponse> publicSearch(@PathVariable String publicToken){
        return ResponseEntity.ok(service.publicSearchMedia(publicToken));
    }
}
