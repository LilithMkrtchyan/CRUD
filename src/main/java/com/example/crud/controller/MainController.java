package com.example.crud.controller;

import com.example.crud.model.User;
import com.example.crud.model.enums.UserRole;
import com.example.crud.security.CurrentUser;
import com.example.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    private UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String index(ModelMap map, HttpServletRequest httpServletRequest,
                        @AuthenticationPrincipal CurrentUser currentUser,
                        @RequestParam(name = "errorAlias", required = false, defaultValue = "") String errorAlias) {
        if (currentUser == null) {
            System.out.println("errorAlias " + errorAlias);
            map.addAttribute("user_obj", new User());
            map.addAttribute("errorAlias", errorAlias);
            return "index";
        } else {
            if (currentUser.getUser().getRole() == UserRole.ADMIN) {
                return "redirect:/admin/home";
            } else {
                return "redirect:/user/home";
            }
        }
    }

    @GetMapping("/loginSuccess")
    public ModelAndView loginSuccess(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        System.out.println("+++++loginSuccess+++");
        CurrentUser currentUser = (CurrentUser) userDetails;
        if (currentUser != null) {
            userService.addUserHistory(currentUser.getUser(), httpServletRequest);
            System.out.println("loginSuccess currentUser!=null");
            User user = currentUser.getUser();
            if (user.getRole() == UserRole.USER) {
                return new ModelAndView("redirect:/user/home");
            } else {
                return new ModelAndView("redirect:/admin/home");
            }
        } else {
            System.out.println("loginSuccess currentUser==null");
            redirectAttributes.addAttribute("errorAlias", "Invalid credentials");
            return new ModelAndView("redirect:/index");
        }
    }

}
