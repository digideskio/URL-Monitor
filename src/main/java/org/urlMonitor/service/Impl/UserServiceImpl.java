package org.urlMonitor.service.Impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Service;
import org.urlMonitor.dao.UserDAO;
import org.urlMonitor.model.User;
import org.urlMonitor.model.UserRole;
import org.urlMonitor.service.AppConfiguration;
import org.urlMonitor.service.UserService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Service
@Scope("singleton")
public class UserServiceImpl implements
        AuthenticationUserDetailsService<OpenIDAuthenticationToken>,
        UserDetailsService, UserService {
    public static String USER_ROLE = "ROLE_USER";
    public static String USER_ADMIN = "ROLE_ADMIN";

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        List<SimpleGrantedAuthority> authorities =
                Lists.newArrayList(new SimpleGrantedAuthority(USER_ROLE));
        return new org.springframework.security.core.userdetails.User(username,
                "", true, true, true, true, authorities);
    }

    @Override
    public UserDetails loadUserDetails(OpenIDAuthenticationToken token)
            throws UsernameNotFoundException {
        String email = null;
        String firstName = null;
        String lastName = null;
        String fullName = null;

        List<OpenIDAttribute> attributes = token.getAttributes();
        for (OpenIDAttribute attribute : attributes) {
            if (attribute.getName().equals("email")) {
                email = attribute.getValues().get(0);
            }
            if (attribute.getName().equals("firstName")) {
                firstName = attribute.getValues().get(0);
            }
            if (attribute.getName().equals("lastName")) {
                lastName = attribute.getValues().get(0);
            }
            if (attribute.getName().equals("fullname")) {
                fullName = attribute.getValues().get(0);
            }
        }
        fullName = getFullName(firstName, lastName, fullName);

        User user = userDAO.findByEmail(email);
        if (user == null) {
            user = createUserWithRoles(fullName, email);
        }

        List<SimpleGrantedAuthority> authorities = Lists.newArrayList();
        for (UserRole role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), "", user.isEnabled(), true, true, true,
                authorities);
    }

    /**
     * Create user with enabled=false, with role {ROLE_USER} and {ROLE_ADMIN} if
     * username predefined in urlmonitor.properties
     * 
     * @param fullName
     * @param email
     * @return
     */
    public User createUserWithRoles(String fullName, String email) {
        Set<String> roles = Sets.newHashSet(USER_ROLE);

        if (isUserPredefinedAdmin(email)) {
            roles.add(USER_ADMIN);
        }

        User user = userDAO.createUser(fullName, email, true, roles);

        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public User updateUserByEmail(String email, String updatedName) {
        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(updatedName)) {
            User user = findByEmail(email);
            if (user != null && !user.getName().equals(updatedName)) {
                user.setName(updatedName);
                userDAO.saveOrUpdate(user);
            }
            return user;
        }
        return null;
    }

    @Override
    public User updateUserByEmail(String email, String updatedName,
            Map<String, Boolean> roles) {
        User user = updateUserByEmail(email, updatedName);
        if (user != null) {
            boolean changed = false;
            for (Map.Entry<String, Boolean> entry : roles.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    if (user.addRole(entry.getKey())) {
                        changed = true;
                    }
                } else {
                    if (user.removeRole(entry.getKey())) {
                        changed = true;
                    }
                }
            }
            if (changed) {
                userDAO.saveOrUpdate(user);
            }
        }
        return user;
    }

    @Override
    public Map<String, Boolean> getUserRoles(String email) {
        User user = findByEmail(email);
        Map<String, Boolean> roles = getAllRoles();
        if (user == null) {
            return roles;
        } else {
            for (UserRole userRole : user.getRoles()) {
                roles.put(userRole.getRole(), true);
            }
            return roles;
        }
    }

    /**
     * Load all roles in application
     * 
     * @return
     */
    private Map<String, Boolean> getAllRoles() {
        Map<String, Boolean> roles = Maps.newHashMap();
        roles.put(USER_ROLE, false);
        roles.put(USER_ADMIN, false);
        return roles;
    }

    private boolean isUserPredefinedAdmin(String username) {
        for (String predefinedAdminUser : appConfiguration.getAdminUsers()) {
            if (username.equals(predefinedAdminUser)) {
                return true;
            }
        }
        return false;
    }

    private String getFullName(String firstName, String lastName,
            String fullName) {
        if (StringUtils.isEmpty(fullName)) {
            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(firstName)) {
                sb.append(firstName);
            }
            if (!StringUtils.isEmpty(lastName)) {
                sb.append(" ").append(lastName);
            }
            return sb.toString();
        }
        return fullName;
    }
}