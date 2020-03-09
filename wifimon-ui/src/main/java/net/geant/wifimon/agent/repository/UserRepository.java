package net.geant.wifimon.agent.repository;

import net.geant.wifimon.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Created by kanakisn on 4/24/16.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByEmail(String email);

    @Query(value = "SELECT users.role FROM users WHERE email = :email", nativeQuery = true)
    String getRoleByEmail(@Param("email") String email);
}