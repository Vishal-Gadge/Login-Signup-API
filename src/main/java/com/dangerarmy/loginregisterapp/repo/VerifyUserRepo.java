package com.dangerarmy.loginregisterapp.repo;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.model.VerifyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifyUserRepo extends JpaRepository<VerifyUser, Integer> {

    VerifyUser findByToken(String token);
    VerifyUser findByUserModel(UserModel userModel);

}
