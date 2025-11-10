package com.elksandro.seunegocio.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elksandro.seunegocio.dto.user.TokenResponse;
import com.elksandro.seunegocio.dto.user.UserLogin;
import com.elksandro.seunegocio.dto.user.UserRequest;
import com.elksandro.seunegocio.dto.user.UserResponse;
import com.elksandro.seunegocio.dto.user.UserUpdate;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.model.enums.Role;
import com.elksandro.seunegocio.repository.UserRepository;
import com.elksandro.seunegocio.security.TokenProvider;
import com.elksandro.seunegocio.service.exception.UserAlreadyExistsException;
import com.elksandro.seunegocio.service.exception.UserNotFoundException;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; 
    private final AuthenticationManager authenticationManager; 
    private final TokenProvider tokenProvider;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                       AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public UserResponse registerUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("E-mail já cadastrado. Utilize outro e-mail.");
        }

        User user = new User();
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setWhatsapp(userRequest.whatsapp());
        user.setRole(Role.BUYER);
        user.setPassword(passwordEncoder.encode(userRequest.password()));

        User savedUser = userRepository.save(user);

        return savedUser.toResponse();
    }

    public TokenResponse loginUser(UserLogin userLogin) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLogin.email(), userLogin.password());

        Authentication auth = this.authenticationManager.authenticate(authenticationToken);

        User user = (User) auth.getPrincipal();
        String token = tokenProvider.generateToken(user);
        long expiresIn = tokenProvider.getExpirationTimeInSeconds();
        return new TokenResponse(token, expiresIn);
    }

    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));
        
        return user.toResponse();
    }

    public UserResponse updateUser(Long id, UserUpdate userUpdate) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para atualização."));
        
        updateData(user, userUpdate);
        userRepository.save(user);

        return user.toResponse();
    }

    private void updateData(User user, UserUpdate userUpdate) {
        user.setName(userUpdate.name());
        user.setWhatsapp(userUpdate.whatsapp());
    }

    public void removeUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuário não encontrado para remoção.");
        }

        userRepository.deleteById(id);
    }
}