package com.dangerarmy.loginregisterapp.repo;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.model.UserRoles;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRolesRepo extends JpaRepository< UserRoles , Integer> {

//    @Cacheable(value = "roles", key = "#userid")
    @Query(value = "SELECT roles FROM user_roles WHERE user_id = :userid",nativeQuery = true)
    List<String> getRoles(@Param("userid") int userid);

}
