package model;

import jakarta.persistence.*;
@Entity
@Table(name = "users")
public class User {

        @Id
        @Column(name = "user_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long userId;

        @Column(nullable = false, unique = true)
        private String username;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private String email;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private UserRole role;
        public User() {
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public User(String username, String password, String email, UserRole role) {
                this.username = username;
                this.password = password;
                this.email = email;
                this.role = role;
        }
        public Long getId() {
                return userId;
        }

        public void setId(Long id) {
                this.userId = id;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public UserRole getRole() {
                return role;
        }

        public void setRole(UserRole role) {
                this.role = role;
        }

        public Long getUserId() {
                return userId;
        }

    public void setUserId(Long refereeId) {
    }
}