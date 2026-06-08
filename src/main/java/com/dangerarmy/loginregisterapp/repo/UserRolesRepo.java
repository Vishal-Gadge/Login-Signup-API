package com.dangerarmy.loginregisterapp.repo;

import com.dangerarmy.loginregisterapp.model.UserRoles;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRolesRepo extends JpaRepository< UserRoles , Integer> {

    @CacheEvict(value = "userRole",key = "#entity.userModel.id")
    @Override
    <S extends UserRoles> S save(S entity);

    @Modifying
    @Cacheable(value = "userRoles", key = "#userid")
    @Query(value = "SELECT roles FROM user_roles WHERE user_id = :userid",nativeQuery = true)
    List<String> getRoles(@Param("userid") int userid);
}
