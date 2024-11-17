package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.UserChangePasswordFormModel;
import net.geant.wifimon.agent.model.UserCreateFormModel;
import net.geant.wifimon.model.entity.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by kanakisn on 4/24/16.
 */
public interface UserService {

    Optional<User> getUserById(long id);

    Optional<User> getUserByEmail(String email);

    Collection<User> getAllUsers();

    User create(UserCreateFormModel form);

    User changePassword(UserChangePasswordFormModel form);

    void delete(Long id);
}