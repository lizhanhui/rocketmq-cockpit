package com.ndpmedia.rocketmq.authentication;

import com.ndpmedia.rocketmq.cockpit.model.CockpitRole;
import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CockpitUserDetails extends CockpitUser implements UserDetails {

    public CockpitUserDetails() {
    }

    public CockpitUserDetails(CockpitUser cockpitUser) {
        setEmail(cockpitUser.getEmail());
        setId(cockpitUser.getId());
        setPassword(cockpitUser.getPassword());
        setTeam(cockpitUser.getTeam());
        setUsername(cockpitUser.getUsername());
        setStatus(cockpitUser.getStatus());
        setCockpitRoles(cockpitUser.getCockpitRoles());
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired), <code>false</code> if no longer valid
     * (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return Status.ACTIVE == getStatus();
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
     * authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired), <code>false</code> if no longer
     * valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
        return Status.ACTIVE == getStatus();
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(getCockpitRoles().size());
        for (CockpitRole role : getCockpitRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

}
