package com.haulmatic.api.services;

import com.haulmatic.api.dto.RoleDto;
import com.haulmatic.api.dto.SearchResultRoleDto;
import com.haulmatic.api.entities.Role;
import com.haulmatic.api.entities.Role.RoleType;
import com.haulmatic.api.repositories.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private RoleRepository roleRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    private RoleDto convertToRoleDto(Role post) {
        return modelMapper.map(post, RoleDto.class);
    }

    private SearchResultRoleDto convertToSearchResultRoleDto(Role post) {
        return modelMapper.map(post, SearchResultRoleDto.class);
    }

    private Role convertToEntity(RoleDto roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }

    public ResponseEntity<RoleDto> getRoleByNic(String nic) {
        if (nic == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Role> roleData = roleRepository.findById(nic);
        return roleData.map(detailedRole -> new ResponseEntity<>(convertToRoleDto(detailedRole), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<RoleDto> createRole(RoleDto roleDto) {
        Role role = convertToEntity(roleDto);
        if (role.getNic() == null ||
                role.getOrganization() == null ||
                role.getFirstName() == null ||
                role.getLastName() == null ||
                role.getRoleType() == null ||
                role.getCreatedDate() != null ||
                role.getLastModifiedDate() != null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Role> roleData = roleRepository.findById(role.getNic());
        if (!roleData.isPresent()) {
            try {
                Date currentDate = new Date();
                role.setCreatedDate(currentDate);
                role.setLastModifiedDate(currentDate);
                Role roleSaved = roleRepository.save(role);
                return new ResponseEntity<>(convertToRoleDto(roleSaved), HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }


    public ResponseEntity<RoleDto> updateRole(String nic, RoleDto roleDto) {
        Role role = convertToEntity(roleDto);
        if (nic == null ||
                role.getOrganization() == null ||
                role.getFirstName() == null ||
                role.getLastName() == null ||
                role.getRoleType() == null ||
                role.getNic() != null ||
                role.getCreatedDate() != null ||
                role.getLastModifiedDate() != null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Role> roleData = roleRepository.findById(nic);
        if (roleData.isPresent()) {
            Role roleFromDb = roleData.get();
            roleFromDb.setOrganization(role.getOrganization());
            roleFromDb.setFirstName(role.getFirstName());
            roleFromDb.setLastName(role.getLastName());
            roleFromDb.setRoleType(role.getRoleType());
            roleFromDb.setLastModifiedDate(new Date());
            return new ResponseEntity<>(convertToRoleDto(roleRepository.save(roleFromDb)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteRole(String nic) {
        if (nic == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            roleRepository.deleteById(nic);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<SearchResultRoleDto>> findByOrganization(String organization, String roleTypeStr) {
        if (organization == null || roleTypeStr == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        RoleType roleType;
        switch (roleTypeStr) {
            case "Assistant":
                roleType = RoleType.ASSISTANT;
                break;
            case "Driver":
                roleType = RoleType.DRIVER;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            List<Role> roles = roleRepository.findByOrganizationAndRoleType(organization, roleType);

            if (roles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<SearchResultRoleDto> roleDtos = roles
                    .stream()
                    .map(this::convertToSearchResultRoleDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(roleDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
