package org.openmrs.module.cfl.web.controller;

import org.openmrs.User;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/cfl/cflLink.form'.
 */
@Controller("${rootrootArtifactId}.CFLModuleController")
@RequestMapping(value = "module/cfl/cfl.form")
public class CFLModuleController {

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    /**
     * Success form view name
     */
    private static final String VIEW = "/module/cfl/cfl";

    /**
     * Initially called after the getUsers method to get the landing form name
     *
     * @return String form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String onGet() {
        return VIEW;
    }

    /**
     * All the parameters are optional based on the necessity
     *
     * @param httpSession
     * @param anyRequestObject
     * @param errors
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public String onPost(HttpSession httpSession, @ModelAttribute("anyRequestObject") Object anyRequestObject,
                         BindingResult errors) {

        if (errors.hasErrors()) {
            // todo: return error view
            return VIEW;
        }

        return VIEW;
    }

    /**
     * This class returns the form backing object. This can be a string, a boolean, or a normal java
     * pojo. The bean name defined in the ModelAttribute annotation and the type can be just defined
     * by the return type of this method
     */
    @ModelAttribute("users")
    protected List<User> getUsers() {
        return userService.getAllUsers();
    }
}
