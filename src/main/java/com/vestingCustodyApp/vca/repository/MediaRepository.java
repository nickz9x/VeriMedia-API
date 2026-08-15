package com.vestingCustodyApp.vca.repository;

import com.vestingCustodyApp.vca.entity.Media;
import com.vestingCustodyApp.vca.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media,Long> {
    Optional<List<Media>> findAllByStatus(Status status);
}
