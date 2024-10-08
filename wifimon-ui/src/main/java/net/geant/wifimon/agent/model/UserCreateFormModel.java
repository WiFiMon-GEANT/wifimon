package net.geant.wifimon.agent.model;

import net.geant.wifimon.model.entity.Role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by kanakisn on 4/24/16.
 */
public class UserCreateFormModel implements Serializable {

    @NotNull
    private String email;

    @NotNull
    @Size(min = 6, max = 30)
    private String password;

    @NotNull
    @Size(min = 6, max = 30)
    private String passwordRepeated;

    @NotNull
    private Role role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeated() {
        return passwordRepeated;
    }

    public void setPasswordRepeated(String passwordRepeated) {
        this.passwordRepeated = passwordRepeated;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
