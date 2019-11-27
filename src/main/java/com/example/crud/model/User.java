package com.example.crud.model;

import com.example.crud.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String password;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private Timestamp createdAt;

    @Column
    private boolean deleted;

    @Column
    private boolean verifed;

//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private Set<UserHistory> userHistories;

    @PrePersist
    public void prePersist(){
        if(role == null){
            role = UserRole.USER;
        }
        createdAt = Timestamp.valueOf(LocalDateTime.now());
    }

}
