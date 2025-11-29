package auth.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import auth.model.Role;
import auth.model.User;
import auth.repository.RoleRepository;
import auth.repository.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeRoles();
        initializeUsers();
    }
    
    private void initializeRoles() {
        if (roleRepository.findByName("admin").isEmpty()) {
            roleRepository.save(new Role(null, "admin"));
        }
        if (roleRepository.findByName("customer").isEmpty()) {
            roleRepository.save(new Role(null, "customer"));
        }
        if (roleRepository.findByName("staff").isEmpty()) {
            roleRepository.save(new Role(null, "staff"));
        }
    }
    
    private void initializeUsers() {
        // Create admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPasswordHash(passwordEncoder.encode("testpassword123"));
            
            Role adminRole = roleRepository.findByName("admin")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);
            
            userRepository.save(adminUser);
        }
        
        // Create customer user
        if (userRepository.findByUsername("customer").isEmpty()) {
            User customerUser = new User();
            customerUser.setUsername("customer");
            customerUser.setEmail("customer@example.com");
            customerUser.setPasswordHash(passwordEncoder.encode("testpassword123"));
            
            Role customerRole = roleRepository.findByName("customer")
                    .orElseThrow(() -> new RuntimeException("Customer role not found"));
            
            Set<Role> roles = new HashSet<>();
            roles.add(customerRole);
            customerUser.setRoles(roles);
            
            userRepository.save(customerUser);
        }
        
        // Create staff user
        if (userRepository.findByUsername("staff").isEmpty()) {
            User staffUser = new User();
            staffUser.setUsername("staff");
            staffUser.setEmail("staff@example.com");
            staffUser.setPasswordHash(passwordEncoder.encode("testpassword123"));
            
            Role staffRole = roleRepository.findByName("staff")
                    .orElseThrow(() -> new RuntimeException("Staff role not found"));
            
            Set<Role> roles = new HashSet<>();
            roles.add(staffRole);
            staffUser.setRoles(roles);
            
            userRepository.save(staffUser);
        }
    }
}
