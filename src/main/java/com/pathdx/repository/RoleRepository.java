package com.pathdx.repository;

import com.pathdx.model.CaseDetails;
import com.pathdx.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    @Query(value = "Select RoleName from role_info where id in ( select role_id from pathdx.users_roles where user_id = ?1)",nativeQuery = true)
    public List<String> getRoleNames(Long id);

}
