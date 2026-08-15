package com.vestingCustodyApp.vca.service;

import com.vestingCustodyApp.vca.dto.MediaRegisterRequestDto;
import com.vestingCustodyApp.vca.dto.MediaResponseDto;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.Status;
import com.vestingCustodyApp.vca.mapper.MediaMapper;
import com.vestingCustodyApp.vca.repository.MediaRepository;
import com.vestingCustodyApp.vca.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class MediaService {
    private MediaRepository repository;
    private UserRepository userRepository;

    public MediaResponseDto registerMedia(MultipartFile file, MediaRegisterRequestDto data, Authentication authentication){
        User user = userRepository.findByLogin(authentication.getName()).get();
        Media media = MediaMapper.toMedia(data, user, file);
        repository.save(media);
        return MediaMapper.toResponseDto(media);
    }

    public List<Media> listAllMedia(){
        return repository.findAll();
    }

    public List<Media> listAllPendingMedia(){
        return repository.findAllByStatus(Status.PENDING).get();
    }
}
