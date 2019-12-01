package com.example.crud.controller;

import com.example.crud.model.User;
import com.example.crud.model.UserHistory;
import com.example.crud.security.CurrentUser;
import com.example.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * User register api
     *
     * @param user -user model
     * @return user-page.html or some page if usera already exist
     */
    @PostMapping("/register")
    public String addUser(@ModelAttribute User user, HttpServletRequest httpServletRequest) {
        return userService.register(user, httpServletRequest);
    }


    /**
     * Open app first page
     *
     * @return
     */
    @GetMapping("/home")
    public String openUserPage(ModelMap modelMap,
                               @AuthenticationPrincipal CurrentUser currentUser) {

        if (currentUser != null) {
            List<UserHistory> userHistories = userService.getUserHistories(currentUser.getUser());
            modelMap.addAttribute("currentUser", currentUser.getUser());
            modelMap.addAttribute("userHistories", userHistories);
            modelMap.addAttribute("isLoggedIn", currentUser != null);
            return "user-page";
        }
        return "redirect:/index";
    }
}
