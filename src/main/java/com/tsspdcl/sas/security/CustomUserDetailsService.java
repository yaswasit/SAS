package com.tsspdcl.sas.security;

import com.tsspdcl.sas.entity.Role;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired 
	HttpSession session; //autowiring session
	
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
    	
        User user = userRepository.findBySasusername(uname);
        //System.out.println("user details.."+user);
        session.setAttribute("userData", user);
        if (user != null) {
        	//System.out.println("Roles..."+user.getRoles());
            return new org.springframework.security.core.userdetails.User(user.getSasusername(),
            	user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
        } else {
        	throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection < ? extends GrantedAuthority> mapRolesToAuthorities(Collection <Role> roles) {
        Collection < ? extends GrantedAuthority> mapRoles = roles.stream()
        	.map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
        mapRoles.forEach(role -> System.out.println("Role..."+role.getAuthority()));
        return mapRoles;
    }
}

