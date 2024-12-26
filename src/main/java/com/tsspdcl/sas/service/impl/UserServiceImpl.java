package com.tsspdcl.sas.service.impl;

import com.tsspdcl.sas.dto.UserDto;
import com.tsspdcl.sas.entity.Role;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.repository.RoleRepository;
import com.tsspdcl.sas.repository.UserRepository;
import com.tsspdcl.sas.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        //user.setSasname(userDto.getFirstName() + " " + userDto.getLastName());
        user.setSasofficeadd(userDto.getSasoffaddr());
        user.setSasdesg(userDto.getSasoffaddr());
        user.setSasusername(userDto.getUsername());

        //encrypt the password once we integrate spring security
        //user.setPassword(userDto.getPassword());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByName("ROLE_ADMIN");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public User findByUser(String uname) {
        return userRepository.findBySasusername(uname);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }
        
    @Override
    public List<User> findBySasusernameContainingIgnoreCase(String uname) {
        return userRepository.findBySasusernameContainingIgnoreCase(uname);
    }

    private UserDto convertEntityToDto(User user){
        UserDto userDto = new UserDto();
        //String[] name = user.getSasname().split(" ");
        //userDto.setFirstName(name[0]);
        //userDto.setLastName(name[1]);
        userDto.setSasoffaddr(user.getSasofficeadd());
        userDto.setSasdesg(user.getSasdesg());
        userDto.setUsername(user.getSasusername());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }
}
