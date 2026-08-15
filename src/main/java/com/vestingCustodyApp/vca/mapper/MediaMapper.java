package com.vestingCustodyApp.vca.mapper;

import com.vestingCustodyApp.vca.config.PasswordEncoder;
import com.vestingCustodyApp.vca.dto.MediaRegisterRequestDto;
import com.vestingCustodyApp.vca.dto.MediaResponseDto;
import com.vestingCustodyApp.vca.dto.PublicMediaResponse;
import com.vestingCustodyApp.vca.dto.RequestReviewMediaDto;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.RequestReviewMedia;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.Status;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MediaMapper {
    public static Media toMedia(MediaRegisterRequestDto data, User user, MultipartFile file,String timestamp){
        Media media = new Media();
        media.setMediaType(data.type());
        media.setOrigin(data.origin());
        media.setMediaName(file.getOriginalFilename());
        if (data.origin() != MediaOrigin.HUMAN){
            media.setTool(data.tool());
        }
        media.setPurpose(data.purpose());
        media.setUser(user);
        media.setStatus(Status.PENDING);
        PasswordEncoder encoder = new PasswordEncoder();
        try {
            media.setHash(encoder.encoder().encode(file.getBytes().toString()));
            media.setPublicToken(encoder.encoder().encode(timestamp).substring(7,19));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return media;
    }

    public static MediaResponseDto toResponseDto(Media media){
        return new MediaResponseDto(media.getId(),
                media.getUser().getId(),
                media.getMediaName(),
                media.getMediaType(),
                media.getOrigin(),
                media.getTool(),
                media.getPurpose(),
                media.getStatus(),
                media.getHash(),
                media.getPublicToken());
    }

    public static PublicMediaResponse toPublicResponseDto(Media media){
         return new PublicMediaResponse(media.getMediaName(),
                media.getMediaType(),
                media.getOrigin(),
                media.getStatus(), media.getPublicToken());
    }
}
