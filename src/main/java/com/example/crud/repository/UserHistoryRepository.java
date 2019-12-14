package com.example.crud.repository;

import com.example.crud.model.User;
import com.example.crud.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory,Integer> {

    @Query(value = "select u FROM UserHistory u where u.user=:user")
    List<UserHistory> findAllByUser(@Param("user") User user);

    @Query(value = "SELECT COUNT(user) FROM UserHistory GROUP BY user")
    List<Integer> getUserHistoryCountOrderByUser(@Param("users") List<User> users);

}
