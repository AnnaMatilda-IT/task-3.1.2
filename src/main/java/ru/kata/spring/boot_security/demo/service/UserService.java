package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserRepository;
import ru.kata.spring.boot_security.demo.dto.UserCreateDto;
import ru.kata.spring.boot_security.demo.exception.UserNotFoundException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public void initializeRoles() {
        // Создаем роли только если они не существуют
        if (roleService.findByName("ROLE_ADMIN") == null) {
            roleService.saveRole(new Role("ROLE_ADMIN"));
            System.out.println("ROLE_ADMIN created");
        }
        if (roleService.findByName("ROLE_USER") == null) {
            roleService.saveRole(new Role("ROLE_USER"));
            System.out.println("ROLE_USER created");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // загружает пользователя по имени для аутентификации
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println("Loaded user: " + username + " with password: " + user.getPassword());
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public UserCreateDto getUserDtoById(Long id) {
        User user = getUserById(id);
        return mapUserToDto(user);
    }

    public void saveUser(UserCreateDto userDto) {
        validateUserUniqueness(userDto, null);// Проверка уникальности
        User user = mapDtoToUser(userDto, null);// Создаем нового пользователя
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Set<Role> roles = roleService.getRolesByIds(userDto.getRoleIds());// Получаем роли по ID через RoleService
        user.setRoles(roles);

        userRepository.save(user);
    }

    public void updateUser(UserCreateDto userDto, Long userId) {
        User existingUser = getUserById(userId);
        validateUserUniqueness(userDto, userId); // Проверка уникальности (кроме текущего пользователя)
        User updatedUser = mapDtoToUser(userDto, existingUser);// Обновляем поля
        Set<Role> roles = roleService.getRolesByIds(userDto.getRoleIds());// Обновляем роли через RoleService
        updatedUser.setRoles(roles);

        userRepository.save(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.delete(getUserById(id));
    }

    private UserCreateDto mapUserToDto(User user) {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setAge(user.getAge());

        // Получаем ID ролей пользователя
        Long[] roleIds = user.getRoles().stream()
                .map(Role::getId)
                .toArray(Long[]::new);
        userDto.setRoleIds(roleIds);

        return userDto;
    }

    private User mapDtoToUser(UserCreateDto userDto, User existingUser) {
        if (existingUser == null) {
            existingUser = new User();
        }

        existingUser.setUsername(userDto.getUsername());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setAge(userDto.getAge());

        // Обновляем пароль, если указан новый (и он не пустой)
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return existingUser;
    }

    private void validateUserUniqueness(UserCreateDto userDto, Long userId) {
        // Проверка уникальности username
        if (userRepository.existsByUsername(userDto.getUsername())) {
            // Для обновления проверяем, что это не тот же пользователь
            if (userId != null) {
                User currentUser = getUserById(userId);
                if (!currentUser.getUsername().equals(userDto.getUsername())) {
                    throw new RuntimeException("Username already exists: " + userDto.getUsername());
                }
            } else {
                throw new RuntimeException("Username already exists: " + userDto.getUsername());
            }
        }
    }
}