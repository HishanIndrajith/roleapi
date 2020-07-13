package com.haulmatic.api.endpointtest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmatic.api.Application;
import com.haulmatic.api.entities.Role;
import com.haulmatic.api.repositories.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RoleServiceControllerTest {

    private final String sample_nic = "953088670v";
    private final String sample_firstName = "George";
    private final String sample_lastName = "Lucas";
    private final String sample_organization = "organization123";
    private final Role.RoleType sample_roleType = Role.RoleType.ASSISTANT;
    @Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    RoleRepository roleRepository;
    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void createRole() throws Exception {
        Role role = new Role();
        // Setting the sample values
        role.setNic(sample_nic);
        role.setFirstName(sample_firstName);
        role.setLastName(sample_lastName);
        role.setOrganization(sample_organization);
        role.setRoleType(sample_roleType);
        // Obtain json string excluding null
        String inputJson = mapToJson(role);
        // Send request
        String uri = "http://localhost:8080/api/roles";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        // Delete entry from database manually
        roleRepository.deleteById(sample_nic);
        // Asserting the status code
        assertEquals(201, status);
        // Obtain json string excluding null
        String content = mvcResult.getResponse().getContentAsString();
        Role response = mapFromJson(content, Role.class);
        // Asserting the response
        assertEquals(response.getNic(), sample_nic);
        assertEquals(response.getFirstName(), sample_firstName);
        assertEquals(response.getLastName(), sample_lastName);
        assertEquals(response.getOrganization(), sample_organization);
        assertEquals(response.getRoleType(), sample_roleType);
    }

    @Test
    public void updateRole() throws Exception {
        // values that are edited in the test
        String organizationEdited = "Organization_edited";
        String lastNameEdited = "Kevin";
        Role role = new Role();
        // Setting the sample values
        role.setNic(sample_nic);
        role.setFirstName(sample_firstName);
        role.setLastName(sample_lastName);
        role.setOrganization(sample_organization);
        role.setRoleType(sample_roleType);
        // Save role manually
        roleRepository.save(role);
        // removing nic from json
        role.setNic(null);
        // editing last name and organization
        role.setOrganization(organizationEdited);
        role.setLastName(lastNameEdited);
        // Obtain json string excluding null
        String inputJson = mapToJson(role);
        // Send request
        String uri = "http://localhost:8080/api/roles/" + sample_nic;
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        // Delete entry from database manually
        roleRepository.deleteById(sample_nic);
        // Asserting the response code
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Role response = mapFromJson(content, Role.class);
        // Asserting the response
        assertEquals(response.getNic(), sample_nic);
        assertEquals(response.getFirstName(), sample_firstName);
        assertEquals(response.getLastName(), lastNameEdited);
        assertEquals(response.getOrganization(), organizationEdited);
        assertEquals(response.getRoleType(), sample_roleType);
    }

    @Test
    public void deleteRole() throws Exception {
        Role role = new Role();
        // Setting the sample values
        role.setNic(sample_nic);
        role.setFirstName(sample_firstName);
        role.setLastName(sample_lastName);
        role.setOrganization(sample_organization);
        role.setRoleType(sample_roleType);
        // Save role manually
        roleRepository.save(role);
        //send request
        String uri = "http://localhost:8080/api/roles/" + sample_nic;
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        // assert the response code
        assertEquals(200, status);
        Optional<Role> objectIfNotDeleted = roleRepository.findById(sample_nic);
        // assert that it is deleted or not
        assertEquals(Optional.empty(), objectIfNotDeleted);
    }

    @Test
    public void retrieveRole() throws Exception {
        Role role = new Role();
        // Setting the sample values
        role.setNic(sample_nic);
        role.setFirstName(sample_firstName);
        role.setLastName(sample_lastName);
        role.setOrganization(sample_organization);
        role.setRoleType(sample_roleType);
        // Save role manually
        roleRepository.save(role);
        String uri = "http://localhost:8080/api/roles/" + sample_nic;
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        // Delete entry from database manually
        roleRepository.deleteById(sample_nic);
        // assert the response code
        assertEquals(200, status);
        // get the response
        String content = mvcResult.getResponse().getContentAsString();
        Role response = mapFromJson(content, Role.class);
        // Asserting the response
        assertEquals(response.getNic(), sample_nic);
        assertEquals(response.getFirstName(), sample_firstName);
        assertEquals(response.getLastName(), sample_lastName);
        assertEquals(response.getOrganization(), sample_organization);
        assertEquals(response.getRoleType(), sample_roleType);
    }

    @Test
    public void getRoleList() throws Exception {
        //Create 3 new sample role using the RoleRepository API, first two are similar in organization and role type.
        //Third is different in role type
        //When searched a list using filtering organization and roletype in first two size of list should be 2
        Role role1 = new Role();
        role1.setNic(sample_nic);
        role1.setFirstName(sample_firstName);
        role1.setLastName(sample_lastName);
        role1.setOrganization(sample_organization);
        role1.setRoleType(sample_roleType);
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setNic("945187552v");
        role2.setFirstName("Nicholas");
        role2.setLastName("Lyman");
        role2.setOrganization(sample_organization);
        role2.setRoleType(sample_roleType);
        roleRepository.save(role2);

        Role role3 = new Role();
        role3.setNic("945178962v");
        role3.setFirstName("Piers");
        role3.setLastName("Hughes");
        role3.setOrganization("organizationXYZ");
        role3.setRoleType(Role.RoleType.ASSISTANT);
        roleRepository.save(role3);
        String url = "http://localhost:8080/api/roles?organization=" +
                sample_organization +
                "&role_type=" +
                "Assistant";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        //Delete the test data created
        roleRepository.delete(role1);
        roleRepository.delete(role2);
        roleRepository.delete(role3);
        // Asserting the status code
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        List roleList = mapFromJson(content, List.class);
        // Asserting the length of list received
        assertEquals(2, roleList.size());
    }

    private String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(obj);
    }

    private <T> T mapFromJson(String json, Class<T> clazz) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}