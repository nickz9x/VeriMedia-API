package com.vestingCustodyApp.vca.mapper;

import com.vestingCustodyApp.vca.dto.ReviewRequestDto;
import com.vestingCustodyApp.vca.dto.ReviewResponseDto;
import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.entity.Review;
import com.vestingCustodyApp.vca.entity.User;

import java.time.Instant;

public class ReviewMapper {


    public static Review toReview(ReviewRequestDto data, User user, Media media){
        Review review = new Review();
        review.setReviewer(user);
        review.setReviewDate(Instant.now());
        review.setMedia(media);
        review.setStatus(data.status());
        if (data.observation() == null){
            review.setObservation("sem observacao");
        }
        else {
            review.setObservation(data.observation());
        }
        return review;
    }

    public static ReviewResponseDto toResponseDto(Review review){
        return new ReviewResponseDto(review.getId(),review.getMedia().getId(),review.getReviewer().getId(), review.getObservation(),review.getStatus(),review.getReviewDate());
    }
}
