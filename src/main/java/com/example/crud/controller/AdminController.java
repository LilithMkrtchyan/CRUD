package com.example.crud.controller;

import com.example.crud.model.enums.UserRole;
import com.example.crud.security.CurrentUser;
import com.example.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;

    @Autowired
    public AdminController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/home")
    public String getAdminPage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap){
        if(currentUser != null && currentUser.getUser().getRole() == UserRole.ADMIN){
            modelMap.addAttribute("currentUser", currentUser.getUser());
            modelMap.addAttribute("allUsers",userService.getAllUsers());
            modelMap.addAttribute("isLoggedIn", currentUser != null);
            return "admin-page";
        }else {
            return "redirect:/index";
        }
    }

    @GetMapping("delete/userId/{userId}")
    public String deleteUser(@AuthenticationPrincipal CurrentUser currentUser,
                             @PathVariable("userId")String userId){
        if(currentUser != null && currentUser.getUser().getRole() == UserRole.ADMIN){
            userService.deleteUser(userId);
            return "redirect:/admin/home";
        }else {
            return "redirect:/index";
        }
    }

   /* @GetMapping("delete/userId/{userId}")
    public String deleteUser(@AuthenticationPrincipal CurrentUser currentUser,
                             @PathVariable("userId")String userId){
        if(currentUser != null && currentUser.getUser().getRole() == UserRole.ADMIN){
            userService.deleteUser(userId);
            return "redirect:/admin/home";
        }else {
            return "redirect:/index";
        }
    }*/

}
