package com.vestingCustodyApp.vca.service;

import com.vestingCustodyApp.vca.dto.*;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.Review;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.Status;
import com.vestingCustodyApp.vca.mapper.MediaMapper;
import com.vestingCustodyApp.vca.mapper.ReviewMapper;
import com.vestingCustodyApp.vca.mapper.ReviewRequestMediaMapper;
import com.vestingCustodyApp.vca.repository.MediaRepository;
import com.vestingCustodyApp.vca.repository.ReviewRepository;
import com.vestingCustodyApp.vca.repository.ReviewRequestMediaRepository;
import com.vestingCustodyApp.vca.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MediaService {
    private MediaRepository repository;
    private UserRepository userRepository;
    private ReviewRequestMediaRepository reviewRequestMediaRepository;
    private ReviewRepository reviewRepository;
    private HashService hashService;
    private StorageService storageService;

    @PreAuthorize("hasRole('CREATOR')")
    public MediaResponseDto registerMedia(MultipartFile file, MediaRegisterRequestDto data, Authentication authentication){
        User user = userRepository.findByLogin(authentication.getName()).get();
        Media media = MediaMapper.toMedia(data, user, file);
        media.setVersion(1);
        media.setHash(hashOf(file));
        repository.save(media);
        storeFile(media, file);
        repository.save(media);
        return MediaMapper.toResponseDto(media,null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public List<MediaResponseDto> listAllMedia(){
        return repository.findAll().stream().map(media -> MediaMapper.
                toResponseDto(media,media.getReview()!=null ? ReviewMapper.toResponseDto(media.getReview()):null)).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public List<MediaResponseDto> listAllPendingMedia(){
        return repository.findAllByStatus(Status.PENDING).get().stream().map(media -> MediaMapper.
                toResponseDto(media,media.getReview()!=null ? ReviewMapper.toResponseDto(media.getReview()):null)).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public List<MediaResponseDto> listAllRejectedMedia(){return repository.findAllByStatus(Status.REJECTED).get().stream().map(media -> MediaMapper.
            toResponseDto(media,media.getReview()!=null ? ReviewMapper.toResponseDto(media.getReview()):null)).toList();}

    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public List<MediaResponseDto> listAllVerifiedMedia(){return repository.findAllByStatus(Status.VERIFIED).get().stream().map(media -> MediaMapper.
                toResponseDto(media,media.getReview()!=null ? ReviewMapper.toResponseDto(media.getReview()):null)).toList();}



    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public MediaResponseDto reviewMedia(ReviewRequestDto data,Authentication authentication){
        Media media = repository.findById(data.mediaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"media not found"));
        String login = authentication.getName();
        User user = userRepository.findByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"user not found"));
        Review review = ReviewMapper.toReview(data, user, media);
        reviewRepository.save(review);
        media.setReview(review);
        media.setStatus(review.getStatus());
        repository.save(media);
        return MediaMapper.toResponseDto(media,ReviewMapper.toResponseDto(review));
    }

    public PublicMediaResponse publicSearchMedia(String publicToken){
        Media media = repository.findByPublicToken(publicToken).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "media not found"));

        return MediaMapper.toPublicResponseDto(media);
    }

    @PreAuthorize("hasAnyRole('CREATOR','VERIFIER')")
    public void requestReview(String publicToken, String reason){
        Media media = repository.findByPublicToken(publicToken).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "media not found"));
        reviewRequestMediaRepository.save(ReviewRequestMediaMapper.toReviewMedia(media,reason));
    }

    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public List<RequestReviewMediaDto> listRequestReview(){
        return reviewRequestMediaRepository.findAll()
                .stream()
                .map(reviewMedia -> ReviewRequestMediaMapper.toRequestReviewResponseDto(reviewMedia)).toList()
                ;
    }

    @PreAuthorize("hasRole('CREATOR')")
    public MediaResponseDto newVersion(
            MediaRegisterRequestDto data,
            MultipartFile file,
            Long id,
            Authentication authentication
    ) {
        Media mediaToUpdate = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "media not found"
                ));

        User user = userRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "user not found"
                ));

        if (!mediaToUpdate.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "voce nao é o dono da midia"
            );
        }

        Media current = mediaToUpdate;

        while (true) {
            Media child = repository
                    .findTopByParentMediaOrderByVersionDesc(current)
                    .orElse(null);

            if (child == null) {
                break;
            }

            current = child;
        }

        Media mediaNewVersion = MediaMapper.toMedia(data, user, file);
        mediaNewVersion.setHash(hashOf(file));
        mediaNewVersion.setParentMedia(current);
        mediaNewVersion.setVersion(current.getVersion() + 1);
        repository.save(mediaNewVersion);
        storeFile(mediaNewVersion, file);
        return MediaMapper.toResponseDto(repository.save(mediaNewVersion),null);
    }

    private String hashOf(MultipartFile file){
        try {
            return hashService.sha256(file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "failed to read file", e);
        }
    }

    private void storeFile(Media media, MultipartFile file){
        try {
            media.setFilePath(storageService.store(media.getId(), file));
        } catch (IOException e) {
            repository.delete(media);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "failed to store file", e);
        }
    }
}
