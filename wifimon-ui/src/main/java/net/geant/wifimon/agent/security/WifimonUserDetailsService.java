package net.geant.wifimon.agent.security;

import net.geant.wifimon.agent.service.UserService;
import net.geant.wifimon.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by kanakisn on 4/24/16.
 */
@Service
public class WifimonUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public WifimonUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public WifimonUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email: %s was not found", email)));
        return new WifimonUser(user);
    }
}
