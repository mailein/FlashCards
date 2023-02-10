package com.example.ClickClip.services;

import com.example.ClickClip.DTOs.UserDTO;
import com.example.ClickClip.entities.User;
import com.example.ClickClip.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService service;

    @Autowired
    UserRepository repository;

    ModelMapper mapper = new ModelMapper();

    private UserDTO u1;
    private UserDTO u2;

    @BeforeEach
    void setUp() {
        u1 = new UserDTO();
        u1.setName("name1");
        u1.setPassword("pass1");

        u2 = new UserDTO();
        u2.setName("name2");
        u2.setPassword("pass2");
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    public void getUserById_success() {
        User user = repository.save(mapper.map(u1, User.class));
        UserDTO userDTO = service.getUserById(user.getId());
        assertEquals(u1.getName(), userDTO.getName());
    }

    @Test
    public void getUserByName_success() {
        User user = repository.save(mapper.map(u1, User.class));
        UserDTO userDTO = service.getUserByName(u1.getName());
        assertEquals(user.getId(), userDTO.getId());
    }

    @Test
    public void getAllUsers_success() {
        User user1 = repository.save(mapper.map(u1, User.class));
        User user2 = repository.save(mapper.map(u2, User.class));
        UserDTO userDTO1 = mapper.map(user1, UserDTO.class);
        UserDTO userDTO2 = mapper.map(user2, UserDTO.class);

        List<UserDTO> testUsers = service.getAllUsers();
        assertEquals(2, testUsers.size());
        assertEquals(userDTO1, testUsers.get(0));
        assertEquals(userDTO2, testUsers.get(1));
        assertEquals(userDTO1.getName(), testUsers.get(0).getName());
        assertEquals(userDTO2.getName(), testUsers.get(1).getName());
        assertEquals(userDTO1.getCreatedAt(), testUsers.get(0).getCreatedAt());
        assertEquals(userDTO2.getCreatedAt(), testUsers.get(1).getCreatedAt());
        assertEquals(userDTO1.getUpdatedAt(), testUsers.get(0).getUpdatedAt());
        assertEquals(userDTO2.getUpdatedAt(), testUsers.get(1).getUpdatedAt());
        assertEquals(u1.getPassword(), user1.getPassword());
        assertEquals(u2.getPassword(), user2.getPassword());
    }

    @Test
    public void addUser_success() {
        UserDTO testUserDTO = service.add(u1);

        UserDTO expectedUserDTO = service.getUserById(testUserDTO.getId());
        assertEquals(expectedUserDTO.getId(), testUserDTO.getId());
        assertEquals(expectedUserDTO.getName(), testUserDTO.getName());
    }

    @Test
    public void updateUser_success() {
        UserDTO userDTO1 = service.add(u1);
        UserDTO userDTO2 = service.updateUser(u2, userDTO1.getId());
        UserDTO updatedUserDTO1 = service.getUserById(userDTO1.getId());
        assertEquals(userDTO2.getName(), updatedUserDTO1.getName());
        assertEquals(userDTO2.getPassword(), updatedUserDTO1.getPassword());
    }

    @Test
    public void deleteUser_success() {
        User user = repository.save(mapper.map(u1, User.class));
        service.deleteById(user.getId());
        assertEquals(0, service.getAllUsers().size());
    }
}