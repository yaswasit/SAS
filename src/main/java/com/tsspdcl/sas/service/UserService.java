package com.tsspdcl.sas.service;

import com.tsspdcl.sas.dto.UserDto;
import com.tsspdcl.sas.entity.User;

import java.util.List;

public interface UserService {

	void saveUser(UserDto userDto);
    
	User findByUser(String uname);
    
	List<UserDto> findAllUsers();
    
	List<User> findBySasusernameContainingIgnoreCase(String uname);
}
