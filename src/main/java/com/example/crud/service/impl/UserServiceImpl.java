package com.example.crud.service.impl;

import com.example.crud.model.User;
import com.example.crud.model.UserHistory;
import com.example.crud.repository.UserHistoryRepository;
import com.example.crud.repository.UserRepository;
import com.example.crud.security.CurrentUserDetailServiceImpl;
import com.example.crud.service.EmailService;
import com.example.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private UserHistoryRepository userHistoryRepository;
    private CurrentUserDetailServiceImpl userDetailsService;
    private EmailService emailService;


    @Value("${spring.artemis.host}")
    private String serverHost;

    @Value("${server.port}")
    private String serverPost;


    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           UserHistoryRepository userHistoryRepository, CurrentUserDetailServiceImpl userDetailsService,
                           EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @Override
    public String register(User user, HttpServletRequest request) {
        String result = null;
        if (userRepository.getByEmail(user.getEmail()) == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String verficationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verficationToken);
            userRepository.save(user);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail().toLowerCase());
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            String verfyUrl = "http://" + serverHost + ":" + serverPost + "/user/verify?v=" + verficationToken;
            String mailText = String.format("Dear %s %s please verify your account: Click to %s", user.getName(), user.getSurname(), verfyUrl);
            emailService.sendSimpleMessage(user.getEmail(), "Email verifiacation", mailText);
            result = "redirect:/index?mailSend=true";
        } else {
            result = "redirect:/index?errorAlias=registerError";
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

    @Override
    public String verifyAccount(String verificationToken, HttpServletRequest request) {
        User user = userRepository.findAllByVerificationToken(verificationToken);
        String result = "redirect:/index";
        if (user != null) {
            user.setVerificationToken(null);
            user.setVerifed(true);
            userRepository.save(user);
            addUserHistory(user, request);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail().toLowerCase());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            result = "redirect:/user/home";
        }
        return result;
    }

    @Override
    public String deleteAccount(User user) {
        SecurityContextHolder.getContext().setAuthentication(null);
        user.setDeleted(true);
        userRepository.save(user);
        return "redirect:/index";
    }

    @Override
    public String activateAccount(User user) {
        if (user.isDeleted()){
            user.setDeleted(false);
        }
        userRepository.save(user);
        return "redirect:/user/home";
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
