package com.pathdx.repository;

import com.pathdx.model.LabDetail;
import com.pathdx.model.Role;
import com.pathdx.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface UsersRepository extends JpaRepository<UserModel,Long> {
    Optional<UserModel> findUserModelByEmail(String email);


    List<UserModel> findUserModelByLabDetails(LabDetail labDetail);

    @Query(value = "select * from\n" +
            "         designation_master d  JOIN pf_user_designation u\n" +
            "        on u.designationdesignation_master_id=d.id \n" +
            " JOIN user_info u1\n" +
            "       on u.user_id=u1.id where d.designation_name=?1",nativeQuery = true)
    List<UserModel> findUserModelByDesignation(String designation);

    List<UserModel> findByLabDetails(LabDetail labDetail);

    @Query(value = "select email_id from user_info where id not in (select user_id\n" +
            " from pf_user_lab_detail where lab_id=(:labId))", nativeQuery = true)
    List<String> getUsersList(@Param("labId")String labId);

}