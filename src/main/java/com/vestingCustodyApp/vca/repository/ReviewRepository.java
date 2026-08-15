package com.vestingCustodyApp.vca.repository;

import com.vestingCustodyApp.vca.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
