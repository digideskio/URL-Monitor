package org.urlMonitor.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.urlMonitor.model.User;
import org.urlMonitor.model.UserRoles;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Repository
@Transactional
public class UserRolesDAO extends AbstractDAO<UserRoles, Long>
{
   public static String USER_DEFAULT_ROLE = "ROLE_USER";

   protected UserRolesDAO()
   {
      super(UserRoles.class);
   }

   public void createRole(User user, String role)
   {
      saveOrUpdate(new UserRoles(user, role));
   }

   public void createDefaultRole(User user)
   {
      createRole(user, USER_DEFAULT_ROLE);
   }
}
