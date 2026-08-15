package com.vestingCustodyApp.vca.mapper;

import com.vestingCustodyApp.vca.dto.RequestReviewMediaDto;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.RequestReviewMedia;

import java.time.Instant;

public class ReviewRequestMediaMapper {

    public static RequestReviewMedia toReviewMedia(Media media,String reason){
        RequestReviewMedia reviewMedia = new RequestReviewMedia();
        reviewMedia.setRequestedReviewDate(Instant.now());
        reviewMedia.setMedia(media);
        reviewMedia.setReason(reason);
        return reviewMedia;
    }

    public static RequestReviewMediaDto toRequestReviewResponseDto(RequestReviewMedia reviewMedia){
        return new RequestReviewMediaDto(reviewMedia.getId(),reviewMedia.getRequestedReviewDate(),reviewMedia.getMedia().getId(), reviewMedia.getReason());
    }
}
