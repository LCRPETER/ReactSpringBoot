package com.Fullstack.reactSpringBoot.services.validation;


import com.Fullstack.reactSpringBoot.repositories.userManagement.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private final UsersRepository usersRepository;

    @Autowired
    public ValidationService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void checkMatriculeAvailability(int matricule) {
        if (usersRepository.findByMatricule(matricule).isPresent()) {
            throw new RuntimeException("Matricule is already in use");
        }
    }

    public void validateEmail(String email) {
        if (!email.contains("@") || !email.contains(".")) {
            throw new RuntimeException("Invalid email");
        }
    }
}

