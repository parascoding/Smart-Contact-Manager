package com.smart.contactManager.dao;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contactManager.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
    // Current-page and number of contact per page.
    @Query("from Contact as c where c.user.id = :userId")
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
}
