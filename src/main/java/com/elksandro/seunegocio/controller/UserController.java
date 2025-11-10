package com.elksandro.seunegocio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.elksandro.seunegocio.dto.user.TokenResponse;
import com.elksandro.seunegocio.dto.user.UserLogin;
import com.elksandro.seunegocio.dto.user.UserRequest;
import com.elksandro.seunegocio.dto.user.UserResponse;
import com.elksandro.seunegocio.dto.user.UserUpdate;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.service.UserService;
import com.elksandro.seunegocio.service.exception.UserAlreadyExistsException;
import com.elksandro.seunegocio.service.exception.UserNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", 
                 produces = MediaType.APPLICATION_JSON_VALUE, 
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest userRequest)
            throws UserAlreadyExistsException {
        
        UserResponse userResponse = userService.registerUser(userRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping(value = "/login", 
                 produces = MediaType.APPLICATION_JSON_VALUE, 
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> loginUser(@RequestBody @Valid UserLogin userLogin) {
        
        TokenResponse tokenResponse = userService.loginUser(userLogin);
        
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> findUserAuthenticated(@AuthenticationPrincipal User user) {
        
        UserResponse userResponse = userService.findUserById(user.getId());
        
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping(value = "/{id}", 
                  produces = MediaType.APPLICATION_JSON_VALUE, 
                  consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id, 
            @RequestBody @Valid UserUpdate userUpdate,
            @AuthenticationPrincipal User loggedUser) throws UserNotFoundException {
        
        if (!loggedUser.getId().equals(id)) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        UserResponse userResponse = userService.updateUser(id, userUpdate);
        
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> removeUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) throws UserNotFoundException {
        
        if (!loggedUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.removeUser(id);
        
        return ResponseEntity.noContent().build();
    }
}
