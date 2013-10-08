package org.urlMonitor.controller;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.urlMonitor.component.MessageResource;
import org.urlMonitor.controller.form.JsonResponse;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
@Slf4j
public class ValidatorController {

    @Autowired
    private MessageResource messageResource;

    @RequestMapping(value = "/validateName", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse validateName(
            @RequestParam(value = "name", required = true) String name) {

        String error = "";
        if (StringUtils.isEmpty(name)) {
            error =
                    messageResource
                            .getMessage("org.hibernate.validator.constraints.NotEmpty.message");
        } else if (StringUtils.length(name) > 255) {
            error =
                    messageResource.getMessage(
                            "validation.constraints.Size.message", "1", "255");
        }

        JsonResponse response = null;

        if (StringUtils.isEmpty(error)) {
            response = new JsonResponse(JsonResponse.STATUS_SUCCESS, null);
        } else {
            response = new JsonResponse(JsonResponse.STATUS_FAILED, error);
        }
        return response;
    }
}
