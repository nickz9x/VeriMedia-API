package com.vestingCustodyApp.vca.service;

import com.vestingCustodyApp.vca.dto.MediaRegisterRequestDto;
import com.vestingCustodyApp.vca.dto.MediaResponseDto;
import com.vestingCustodyApp.vca.dto.PublicMediaResponse;
import com.vestingCustodyApp.vca.dto.RequestReviewMediaDto;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.RequestReviewMedia;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.Status;
import com.vestingCustodyApp.vca.mapper.MediaMapper;
import com.vestingCustodyApp.vca.mapper.ReviewRequestMediaMapper;
import com.vestingCustodyApp.vca.repository.MediaRepository;
import com.vestingCustodyApp.vca.repository.ReviewRequestMediaRepository;
import com.vestingCustodyApp.vca.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class MediaService {
    private MediaRepository repository;
    private UserRepository userRepository;
    private ReviewRequestMediaRepository reviewRequestMediaRepository;

    public MediaResponseDto registerMedia(MultipartFile file, MediaRegisterRequestDto data, Authentication authentication){
        User user = userRepository.findByLogin(authentication.getName()).get();
        Media media = MediaMapper.toMedia(data, user, file);
        repository.save(media);
        return MediaMapper.toResponseDto(media);
    }

    public List<MediaResponseDto> listAllMedia(){
        return repository.findAll().stream().map(media -> MediaMapper.toResponseDto(media)).toList();
    }

    public List<MediaResponseDto> listAllPendingMedia(){
        return repository.findAllByStatus(Status.PENDING).get().stream().map(media -> MediaMapper.toResponseDto(media)).toList();
    }

    public List<MediaResponseDto> listAllRejectedMedia(){return repository.findAllByStatus(Status.REJECTED).get().stream().map(media -> MediaMapper.toResponseDto(media)).toList();}

    public List<MediaResponseDto> listAllVerifiedMedia(){return repository.findAllByStatus(Status.VERIFIED).get().stream().map(media -> MediaMapper.toResponseDto(media)).toList();}

    public MediaResponseDto aproveMedia(Long id){
        Media media = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        if (media.getStatus() == Status.VERIFIED){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        media.setStatus(Status.VERIFIED);
        repository.save(media);
        return MediaMapper.toResponseDto(media);
    }

    public PublicMediaResponse publicSearchMedia(Long id){
        Media media = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "media not found"));
        return MediaMapper.toPublicResponseDto(media);
    }

    public void contestMedia(Long id){
        Media media = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "media not found"));
        media.setStatus(Status.PENDING);
        repository.save(media);
    }

    public MediaResponseDto denyMedia(Long id){
        Media media = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "media not found"));
        media.setStatus(Status.REJECTED);
        return MediaMapper.toResponseDto(repository.save(media));
    }

    public void requestReview(Long id, String reason){
        Media media = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "media not found"));
        reviewRequestMediaRepository.save(ReviewRequestMediaMapper.toReviewMedia(media,reason));
    }

    public List<RequestReviewMediaDto> listRequestReview(){
        return reviewRequestMediaRepository.findAll()
                .stream()
                .map(reviewMedia -> MediaMapper.toRequestReviewResponseDto(reviewMedia)).toList()
                ;
    }
}
