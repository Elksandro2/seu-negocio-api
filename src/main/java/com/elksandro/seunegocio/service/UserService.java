package com.elksandro.seunegocio.service;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elksandro.seunegocio.dto.user.TokenResponse;
import com.elksandro.seunegocio.dto.user.UserLogin;
import com.elksandro.seunegocio.dto.user.UserRequest;
import com.elksandro.seunegocio.dto.user.UserResponse;
import com.elksandro.seunegocio.dto.user.UserSummaryResponse;
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
    private final MinioService minioService;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, TokenProvider tokenProvider, MinioService minioService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.minioService = minioService;
    }

    public UserResponse registerUser(UserRequest userRequest, MultipartFile image) throws Exception {
        validateUserRequest(userRequest);

        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("E-mail já cadastrado. Utilize outro e-mail.");
        }

        String imageKey = minioService.uploadFile(image);

        User user = new User();
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setWhatsapp(userRequest.whatsapp());
        user.setRole(Role.BUYER);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setProfilePictureKey(imageKey);

        User savedUser = userRepository.save(user);

        return convertToResponse(savedUser);
    }

    public UserResponse updateProfilePicture(Long userId, MultipartFile image) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        if (Objects.isNull(image) || image.isEmpty()) {
            throw new IllegalArgumentException("A imagem de perfil não pode ser vazia.");
        }

        if (user.getProfilePictureKey() != null) {
            minioService.deleteObject(user.getProfilePictureKey());
        }

        String newKey = minioService.uploadFile(image);

        user.setProfilePictureKey(newKey);
        User updatedUser = userRepository.save(user);

        return convertToResponse(updatedUser);
    }

    public TokenResponse loginUser(UserLogin userLogin) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLogin.email(), userLogin.password());

        Authentication auth = this.authenticationManager.authenticate(authenticationToken);

        User user = (User) auth.getPrincipal();
        String token = tokenProvider.generateToken(user);
        long expiresIn = tokenProvider.getExpirationTimeInSeconds();
        return new TokenResponse(token, expiresIn);
    }

    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));

        return convertToResponse(user);
    }

    public UserResponse updateUser(Long id, UserUpdate userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para atualização."));

        updateData(user, userUpdate);
        userRepository.save(user);

        return convertToResponse(user);
    }

    private void updateData(User user, UserUpdate userUpdate) {
        user.setName(userUpdate.name());
        user.setWhatsapp(userUpdate.whatsapp());
    }

    public void removeUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para remoção."));

        if (user.getProfilePictureKey() != null) {
            minioService.deleteObject(user.getProfilePictureKey());
        }

        userRepository.deleteById(id);
    }

    private UserResponse convertToResponse(User user) {
        String profileUrl = minioService.getObjectUrl(user.getProfilePictureKey());

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getWhatsapp(),
                profileUrl);
    }

    public UserSummaryResponse convertToSummaryResponse(User user) {
        String profileUrl = minioService.getObjectUrl(user.getProfilePictureKey());

        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getWhatsapp(),
                profileUrl);
    }

    private void validateUserRequest(UserRequest request) {
        if (Objects.isNull(request.name()) || request.name().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (request.name().length() < 3 || request.name().length() > 100) {
            throw new IllegalArgumentException("O nome deve ter entre 3 e 100 caracteres.");
        }

        if (Objects.isNull(request.email()) || request.email().isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        Matcher matcher = EMAIL_PATTERN.matcher(request.email());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("O e-mail deve ser válido.");
        }

        if (Objects.isNull(request.password()) || request.password().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }

        if (request.password().length() < 6) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres.");
        }

        if (request.whatsapp() != null && request.whatsapp().length() > 20) {
            throw new IllegalArgumentException("O WhatsApp deve ter no máximo 20 caracteres.");
        }
    }
}