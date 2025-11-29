package auth.service;

import auth.config.JwtConfig;
import auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    public String login(String username, String password) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!userService.validatePassword(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        
        return jwtConfig.generateToken(user.getId(), user.getUsername(), roles);
    }
}
