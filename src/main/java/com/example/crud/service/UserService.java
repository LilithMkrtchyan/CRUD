package com.example.crud.service;

import com.example.crud.model.User;
import com.example.crud.model.UserHistory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {

    String register(User user, HttpServletRequest httpServletRequest);

    void addUserHistory(User user, HttpServletRequest httpServletRequest);

    List<UserHistory> getUserHistories(User user);

    List<User> getAllUsers();

    void deleteUser(String userId);

    String verifyAccount(String verificationToken, HttpServletRequest httpServletRequest);

    String deleteAccount(User user);
}
