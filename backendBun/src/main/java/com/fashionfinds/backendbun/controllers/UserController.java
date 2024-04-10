package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

}
