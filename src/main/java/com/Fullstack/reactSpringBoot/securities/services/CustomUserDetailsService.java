package com.Fullstack.reactSpringBoot.securities.services;

import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import com.Fullstack.reactSpringBoot.repositories.userManagement.AdminRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.ParentsRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.StudentsRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.TeacherRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentsRepository studentRepository;
    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private  final ParentsRepository parentRepository;

    @Override
    public UserDetails loadUserByUsername(String matriculeString) throws UsernameNotFoundException {
        int matricule;
        try {
            matricule = Integer.parseInt(matriculeString);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Matricule format invalid: " + matriculeString);
        }

        Optional<Users> userOptional = adminRepository.findByMatricule(matricule).map(Users.class::cast)
                .or(() -> studentRepository.findByMatricule(matricule).map(Users.class::cast))
                .or(() -> parentRepository.findByMatricule(matricule).map(Users.class::cast))
                .or(() -> teacherRepository.findByMatricule(matricule).map(Users.class::cast));

        Users user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne correspond à ce matricule: " + matricule));
        return new CustomUserDetails(user);
    }



}

