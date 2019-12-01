package com.example.crud.service.impl;

import com.example.crud.model.User;
import com.example.crud.model.UserHistory;
import com.example.crud.repository.UserHistoryRepository;
import com.example.crud.repository.UserRepository;
import com.example.crud.security.CurrentUserDetailServiceImpl;
import com.example.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private UserHistoryRepository userHistoryRepository;
    private CurrentUserDetailServiceImpl userDetailsService;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           UserHistoryRepository userHistoryRepository, CurrentUserDetailServiceImpl userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String register(User user, HttpServletRequest request) {
        String result = null;
        if (userRepository.getByEmail(user.getEmail()) == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
           // System.out.println("usery save exav");
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail().toLowerCase());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            result = "redirect:/user/home";
        }else {
            result="redirect:/index?errorAlias=registerError";
        }
        return result;
    }

    @Override
    public void addUserHistory(User user, HttpServletRequest httpServletRequest) {
        userHistoryRepository.save(UserHistory.builder()
                .ipAddress(getClientIp(httpServletRequest))
                .user(user)
                .build());
    }

    @Override
    public List<UserHistory> getUserHistories(User user) {
        return userHistoryRepository.findAllByUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(Integer.parseInt(userId));
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
