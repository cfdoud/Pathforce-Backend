package com.pathdx.repository;

import com.pathdx.model.UserSlideModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSlideRepository extends JpaRepository<UserSlideModel,Long> {

    @Query("SELECT count(*) FROM UserSlideModel u INNER JOIN u.userModel l WHERE l.id IN (:userId) and u.barcodeId  in " +
            "(:barCodeid)")
    Long findByBarcodeIdAndUserId(String barCodeid, Long userId);
}
