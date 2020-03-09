package net.geant.wifimon.agent.security;

import net.geant.wifimon.model.entity.Role;
import net.geant.wifimon.model.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Created by kanakisn on 4/24/16.
 */
public class WifimonUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public WifimonUser(final User user) {
        super(user.getEmail(), user.getPasswordHash(),
                AuthorityUtils.createAuthorityList(String.join("_", "ROLE", user.getRole().toString())));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }
}
