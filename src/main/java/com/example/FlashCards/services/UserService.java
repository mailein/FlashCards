package com.example.FlashCards.services;

import com.example.FlashCards.DTOs.UserDTO;
import com.example.FlashCards.configurations.Entity2DTOMapper;
import com.example.FlashCards.entities.User;
import com.example.FlashCards.exceptions.InvalidRequestException;
import com.example.FlashCards.exceptions.NotFoundException;
import com.example.FlashCards.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    Entity2DTOMapper modelMapper;

    public UserDTO add(UserDTO userDTO) {
        // user id IS null now! The savedUser id IS NOT null!
        if (userRepository.findByName(userDTO.getName()).isPresent()) {
            throw new InvalidRequestException("User with username " + userDTO.getName() + " already exists.");
        }

        User user = modelMapper.map(userDTO, User.class);

        User savedUser = userRepository.save(user);
        log.info("[User] added " + savedUser.toString());
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public boolean login(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByName(userDTO.getName());
        boolean success = optionalUser.map(user -> user.getPassword().equals(userDTO.getPassword())).orElse(false);
        log.info(userDTO.getName() + (success ? " logs in successfully." : " can't log in."));
        return success;
    }

    public void deleteById(Long id) {
        // check user
        if (id == null) {
            throw new InvalidRequestException("User id is null.");
        }

        // check repo
        if (userRepository.findById(id).isEmpty()) {
            throw new InvalidRequestException("User with id " + id + " doesn't exist.");
        }

        userRepository.deleteById(id);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " is not found."));
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUserByName(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("User with name " + name + " is not found."));
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(UserDTO userDTO, Long userId) {
        // check user
        if (userId == null) {
            throw new InvalidRequestException("User id is null.");
        }

        // check repo
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new InvalidRequestException("User with id " + userId + " doesn't exist.");
        }

        User existingUser = optionalUser.get();
        existingUser.setName(userDTO.getName());
        existingUser.setPassword(userDTO.getPassword());
//        existingUser.setGlossaries(user.getGlossaries());//user doesn't update glossaries => glossary updates user
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }
}
