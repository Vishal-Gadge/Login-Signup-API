package com.dangerarmy.loginregisterapp.repo;

import java.util.Optional;

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

    @Cacheable(value = "users" , key = "#email")
    Optional<UserModel> findByEmail(String email);

    @CachePut(value = "users" , key = "#entity.email")
    @Override
    <S extends UserModel> S save(S entity);

    @Modifying
    @Transactional
    @CacheEvict(value = "users" , key = "#email")
    @Query("UPDATE UserModel u SET u.password = :encodedPassword WHERE u.email = :email AND u.username = :username")
    int updatePasswordByEmailAndUsername(@Param("email") String email,
                                         @Param("username") String username,
                                         @Param("encodedPassword") String encodedPassword);
}
