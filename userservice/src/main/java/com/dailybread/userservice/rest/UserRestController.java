package com.dailybread.userservice.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailybread.userservice.dto.AddRequest;
import com.dailybread.userservice.dto.CashierActivationRequest;
import com.dailybread.userservice.dto.LoginRequest;
import com.dailybread.userservice.model.User;
import com.dailybread.userservice.service.UserService;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public User save(@RequestBody User user) {
        System.out.println("User signup request received");
        return userService.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login request received");
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/register")
    public ResponseEntity<?> addCashier(@RequestBody AddRequest request) {
        System.out.println(request.getEmail() + " email received");
        Map<String, Object> response = new HashMap<>();
        String res = userService.addCashier(request.getEmail(), request.getUserName());
        if (res.equals("success")) {
            response.put("message", res);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", res);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateCashier(@RequestBody CashierActivationRequest request) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("request received: " + request.getPassword() + request.getActivationToken());
        String res = userService.activateCashier(request);
        if (res.equals("success")) {
            response.put("message", res);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", res);
            return ResponseEntity.badRequest().body(response);
        }
        // return userService.activateCashier(request);
    }

    @GetMapping("/cashiers")
    public ResponseEntity<?> getAllCashiers() {
        System.out.println("Get all cashiers request received");
        List<User> cashiers = userService.getAllCashiers();
        Map<String, Object> response = new HashMap<>();
        cashiers.forEach(cashier -> {
            System.out.println("Cashier: " + cashier.getEmail());
        });
        if (cashiers.isEmpty()) {
            System.out.println("No cashiers found");
            response.put("message", "No cashiers found");
            return ResponseEntity.status(404).body(response);
        } else {
            System.out.println("Cashiers found: " + cashiers.size());
            response.put("cashiers", cashiers);
            return ResponseEntity.ok(response);
        }

    }

    @DeleteMapping("/cashiers/{email}")
    public ResponseEntity<?> deleteCashier(@PathVariable String email) {
        System.out.println("Delete cashier request received for email: " + email);
        Map<String, Object> response = new HashMap<>();
        String res = userService.deleteCashier(email);
        if (res.equals("success")) {
            response.put("message", "Cashier deleted successfully");
            response.put("email", email);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", res);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
