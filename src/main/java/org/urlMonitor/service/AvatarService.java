package org.urlMonitor.service;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.urlMonitor.util.HashUtil;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Service
@Scope("singleton")
@Slf4j
public class AvatarService {
    private static final String GRAVATAR_URL =
            "http://www.gravatar.com/avatar/";

    private static final String DEFAULT_AVATAR = "resources/images/user.png";

    public String getUserAvatar(String email, Integer size) {
        if (StringUtils.isEmpty(email)) {
            return DEFAULT_AVATAR;
        }

        if (size != null) {
            return GRAVATAR_URL + HashUtil.md5Hex(email.toLowerCase()) + "?s="
                    + size;
        } else {
            return GRAVATAR_URL + HashUtil.md5Hex(email.toLowerCase());
        }

    }

}
