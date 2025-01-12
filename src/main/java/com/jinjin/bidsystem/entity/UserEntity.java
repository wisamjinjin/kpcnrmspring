package com.jinjin.bidsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * 사용자 테이블블
 */
@Entity
@Table(name = "biduser")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성
    private Long id; // 엔티티의 고유 식별자
    private String username; // 사용자 이름
    private String email;    // 이메일
    private String password; // 비밀번호
    private String telno;    // 전화번호
    private String role;     // 사용자 권한

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시간
    
    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", telno='" + telno + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
    // Getter와 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Entity가 persist되기 전 실행
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
