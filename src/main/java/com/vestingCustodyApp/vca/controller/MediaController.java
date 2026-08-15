package com.vestingCustodyApp.vca.controller;

import com.vestingCustodyApp.vca.dto.*;
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

    @PostMapping("/review")
    public ResponseEntity<MediaResponseDto> reviewMedia(@RequestBody ReviewRequestDto data, Authentication authentication){
        return ResponseEntity.ok(service.reviewMedia(data,authentication));
    }


    @PostMapping("/request-review/{publicToken}")
    @ResponseStatus(HttpStatus.OK)
    public void requestReview(@PathVariable String publicToken,@RequestBody String reason){
        service.requestReview(publicToken,reason);
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
