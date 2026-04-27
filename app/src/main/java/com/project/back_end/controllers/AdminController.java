package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.entity.Admin;
import com.project.back_end.services.AuthService;

@RestController
@RequestMapping("${api.path}/admin")
public class AdminController {

    private final AuthService authService;

    // Constructor Injection
    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    // Admin Login
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Admin admin) {
        return authService.validateAdmin(admin.getUsername(), admin.getPassword());
    }
}
