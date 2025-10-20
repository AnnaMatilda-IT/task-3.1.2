package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Set<Role> getRolesByIds(Long[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            throw new RuntimeException("Role IDs cannot be null or empty");
        }
        return roleRepository.findByIdIn(Arrays.asList(roleIds));
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
}
