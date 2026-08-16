package com.vestingCustodyApp.vca.service;

import com.vestingCustodyApp.vca.dto.MediaRegisterRequestDto;
import com.vestingCustodyApp.vca.dto.MediaResponseDto;
import com.vestingCustodyApp.vca.dto.ReviewRequestDto;
import com.vestingCustodyApp.vca.dto.VerifyResponse;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.MediaType;
import com.vestingCustodyApp.vca.enums.Status;
import com.vestingCustodyApp.vca.repository.MediaRepository;
import com.vestingCustodyApp.vca.repository.ReviewRepository;
import com.vestingCustodyApp.vca.repository.ReviewRequestMediaRepository;
import com.vestingCustodyApp.vca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MediaServiceTest {

    private MediaRepository mediaRepository;
    private UserRepository userRepository;
    private ReviewRequestMediaRepository reviewRequestMediaRepository;
    private ReviewRepository reviewRepository;
    private HashService hashService;
    private StorageService storageService;
    private MediaService mediaService;

    private User owner;
    private User other;
    private MockMultipartFile file;

    @BeforeEach
    void setUp(){
        mediaRepository = mock(MediaRepository.class);
        userRepository = mock(UserRepository.class);
        reviewRequestMediaRepository = mock(ReviewRequestMediaRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        hashService = mock(HashService.class);
        storageService = mock(StorageService.class);
        mediaService = new MediaService(mediaRepository, userRepository, reviewRequestMediaRepository, reviewRepository, hashService, storageService);

        owner = new User();
        owner.setId(1L);
        owner.setLogin("owner");

        other = new User();
        other.setId(2L);
        other.setLogin("other");

        file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "bytes".getBytes());

        when(mediaRepository.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Media mediaOf(User user, Integer version){
        Media media = new Media();
        media.setId(10L);
        media.setUser(user);
        media.setVersion(version);
        media.setStatus(Status.PENDING);
        media.setMediaName("foto.jpg");
        media.setHash("stored-hash");
        return media;
    }

    private Authentication authAs(String login){
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(login);
        return auth;
    }

    private MediaRegisterRequestDto registerData(){
        return new MediaRegisterRequestDto(MediaOrigin.HUMAN, MediaType.IMAGE, null, "teste");
    }

    @Test
    void newVersionThrows403WhenUserIsNotOwner(){
        Media media = mediaOf(owner, 1);
        when(mediaRepository.findById(10L)).thenReturn(Optional.of(media));
        when(userRepository.findByLogin("other")).thenReturn(Optional.of(other));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mediaService.newVersion(registerData(), file, 10L, authAs("other")));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(mediaRepository, never()).save(any(Media.class));
    }

    @Test
    void newVersionUsesLatestVersionAndLinksParent() throws Exception {
        Media v1 = mediaOf(owner, 1);
        v1.setId(10L);
        Media v2 = mediaOf(owner, 2);
        v2.setId(11L);
        v2.setParentMedia(v1);
        when(mediaRepository.findById(10L)).thenReturn(Optional.of(v1));
        when(mediaRepository.findTopByParentMediaOrderByVersionDesc(v1)).thenReturn(Optional.of(v2));
        when(mediaRepository.findTopByParentMediaOrderByVersionDesc(v2)).thenReturn(Optional.empty());
        when(userRepository.findByLogin("owner")).thenReturn(Optional.of(owner));
        when(hashService.sha256(any(byte[].class))).thenReturn("novo-hash");
        when(storageService.store(any(Long.class), any())).thenReturn("storage/12/x.jpg");

        MediaResponseDto response = mediaService.newVersion(registerData(), file, 10L, authAs("owner"));

        assertNotNull(response);
        assertEquals("novo-hash", response.hash());
        verify(mediaRepository, times(2)).save(any(Media.class));
        Media saved = mediaRepositorySavedMedia();
        assertEquals(3, saved.getVersion());
        assertEquals(v2, saved.getParentMedia());
    }

    private Media mediaRepositorySavedMedia(){
        org.mockito.ArgumentCaptor<Media> captor = org.mockito.ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository, atLeastOnce()).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void verifyMediaThrows404ForUnknownToken(){
        when(mediaRepository.findByPublicToken("token-x")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mediaService.verifyMedia("token-x", file));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void verifyMediaReturnsMatchesTrueForIdenticalFile(){
        Media media = mediaOf(owner, 1);
        when(mediaRepository.findByPublicToken("token-x")).thenReturn(Optional.of(media));
        when(hashService.matches(any(byte[].class), eq("stored-hash"))).thenReturn(true);

        VerifyResponse response = mediaService.verifyMedia("token-x", file);

        assertTrue(response.matches());
        assertEquals("foto.jpg", response.mediaName());
        assertEquals(Status.PENDING, response.status());
    }

    @Test
    void verifyMediaReturnsMatchesFalseForTamperedFile(){
        Media media = mediaOf(owner, 1);
        when(mediaRepository.findByPublicToken("token-x")).thenReturn(Optional.of(media));
        when(hashService.matches(any(byte[].class), eq("stored-hash"))).thenReturn(false);

        VerifyResponse response = mediaService.verifyMedia("token-x", file);

        assertFalse(response.matches());
    }

    @Test
    void reviewMediaThrows403WhenReviewerIsOwner(){
        Media media = mediaOf(owner, 1);
        when(mediaRepository.findById(10L)).thenReturn(Optional.of(media));
        when(userRepository.findByLogin("owner")).thenReturn(Optional.of(owner));
        ReviewRequestDto data = new ReviewRequestDto(10L, "ok", Status.VERIFIED);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mediaService.reviewMedia(data, authAs("owner")));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void reviewMediaThrows409WhenMediaAlreadyFinalized(){
        Media media = mediaOf(owner, 1);
        media.setStatus(Status.VERIFIED);
        when(mediaRepository.findById(10L)).thenReturn(Optional.of(media));
        when(userRepository.findByLogin("other")).thenReturn(Optional.of(other));
        ReviewRequestDto data = new ReviewRequestDto(10L, "ok", Status.VERIFIED);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mediaService.reviewMedia(data, authAs("other")));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void reviewMediaThrows400WhenReviewStatusIsPending(){
        Media media = mediaOf(owner, 1);
        when(mediaRepository.findById(10L)).thenReturn(Optional.of(media));
        when(userRepository.findByLogin("other")).thenReturn(Optional.of(other));
        ReviewRequestDto data = new ReviewRequestDto(10L, "ok", Status.PENDING);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mediaService.reviewMedia(data, authAs("other")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
