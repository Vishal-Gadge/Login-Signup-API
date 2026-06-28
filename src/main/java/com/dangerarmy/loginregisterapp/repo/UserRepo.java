package com.dangerarmy.loginregisterapp.repo;

import java.util.List;
import java.util.Optional;

import com.dangerarmy.loginregisterapp.dto.AdminReqUsers;
import com.dangerarmy.loginregisterapp.model.UserModel;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<UserModel,Integer>{

    @Cacheable(value = "user" ,unless = "#result == null", key = "#email")
    Optional<UserModel> findByEmail(String email);

    @CachePut(value = "user" , key = "#entity.email")
    @Override
    <S extends UserModel> S save(S entity);

    @Modifying
    @Transactional
    @CacheEvict(value = "user" , key = "#email")
    @Query("UPDATE UserModel u SET u.password = :encodedPassword WHERE u.email = :email AND u.username = :username")
    int updatePasswordByEmailAndUsername(@Param("email") String email,
                                         @Param("username") String username,
                                         @Param("encodedPassword") String encodedPassword);



    //to show to admin 
    //get all users
    @Modifying
    @Cacheable(value = "allUsers")
    @Query(value = "SELECT id as id, username as username, email as email FROM user_model",nativeQuery = true)
    List<AdminReqUsers> getAllUsersExceptPass();

    //get all admins
    @Modifying
    @Cacheable(value = "allAdmins")
    @Query(value ="SELECT u.id,u.username,u.email FROM user_model u JOIN user_roles r ON u.id=r.user_id where r.roles = 'ADMIN'",
                nativeQuery = true)
    List<AdminReqUsers> getAllAdmins();
}














