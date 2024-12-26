package com.tsspdcl.sas.repository;

import com.tsspdcl.sas.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
	User findBySasusername(String sasusername);
    
    List<User> findBySasusernameContainingIgnoreCase(String uname);
   
}
