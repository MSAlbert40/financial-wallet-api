package com.evertix.financialwallet.config;

import com.evertix.financialwallet.model.Role;
import com.evertix.financialwallet.model.enums.ERole;
import com.evertix.financialwallet.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader {
    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.loadData();
    }

    private void loadData() {
        this.addRoles();
    }

    private void addRoles() {
        this.roleRepository.saveAll(Arrays.asList(
                new Role(ERole.ROLE_ADMIN),
                new Role(ERole.ROLE_USER)
        ));
    }
}
