package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserUtility {

    @Autowired
    private UserService userService;

    public Long getUserIdByUsername(String username) {
        System.out.println("Getting user ID for username: " + username);
        return userService.getUserIdByUsername(username);
    }
}
