package com.vestingCustodyApp.vca.mapper;

import com.vestingCustodyApp.vca.dto.MediaRegisterRequestDto;
import com.vestingCustodyApp.vca.dto.MediaResponseDto;
import com.vestingCustodyApp.vca.dto.PublicMediaResponse;
import com.vestingCustodyApp.vca.dto.ReviewResponseDto;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class MediaMapper {
    public static Media toMedia(MediaRegisterRequestDto data, User user, MultipartFile file){
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
        media.setPublicToken(UUID.randomUUID().toString());
        return media;
    }

    public static MediaResponseDto toResponseDto(Media media, ReviewResponseDto dto){
        return new MediaResponseDto(media.getId(),
                media.getUser().getId(),
                media.getMediaName(),
                media.getMediaType(),
                media.getOrigin(),
                media.getTool(),
                media.getPurpose(),
                media.getStatus(),
                media.getHash(),
                media.getPublicToken(),
                dto);
    }

    public static PublicMediaResponse toPublicResponseDto(Media media){
         return new PublicMediaResponse(media.getMediaName(),
                media.getMediaType(),
                media.getOrigin(),
                media.getStatus(), media.getPublicToken());
    }
}
