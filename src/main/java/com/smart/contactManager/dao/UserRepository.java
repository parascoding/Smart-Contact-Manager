package com.smart.contactManager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.contactManager.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    
}
