package com.Fullstack.reactSpringBoot.services.users;

import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import com.Fullstack.reactSpringBoot.repositories.userManagement.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // Enregistrer un nouvel enseignant
    public int generateUniqueMatricule() {
        Random random = new Random();
        int matricule;
        do {
            matricule = 1000 + random.nextInt(9000); // Générer un nombre à 4 chiffres
        } while (usersRepository.existsByMatricule(matricule));
        return matricule;
    }

    @Override
    public Optional<Users> findUsersByMatricule(int matricule) {
        return usersRepository.findByMatricule(matricule);
    }

    @Override
    public List<Users> findUsersByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName) {
        return usersRepository.findUsersByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);
    }

    @Override
    public Optional<Users> findUsersByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName) {
        return usersRepository.findUsersByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
    }

//    public List<Object> getUsersByRole(Role role) {
//        List<Users> users = usersRepository.findByRole(role);
//
//        return users.stream().map(user -> {
//            if ("PARENT".equalsIgnoreCase(role.toString())) {
//                return new ParentInfoDTO(user.getMatricule(), user.getFirstName(), user.getLastName());
//            } else if ("STUDENT".equalsIgnoreCase(role.toString())) {
//                Students student = (Students) user; // Assuming User is extended by Student
//                return new StudentInfoDTO(student.getMatricule(), student.getFirstName(), student.getLastName(),
//                        student.getGroups().getNameGroup(), student.getGroups().getLevel());}
////            } else if ("teacher".equalsIgnoreCase(role)) {
////                Teachers teacher = (Teachers) user; // Assuming User is extended by Teacher
////                return new TeacherInfoDTO(teacher.getUser_id(), teacher.getFirstName(), teacher.getLastName(),
////                        teacher.getTaughtSubjects());
////            }
//            return null;
//        }).collect(Collectors.toList());
//    }

    @Override
    @Transactional
    public String deleteUsersByUserId(long userId) {
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (userOptional.isPresent()) {
            usersRepository.deleteUsers(userId);
            return "Utilisateur avec l'ID " + userId + " supprimé avec succès.";
        } else {
            return "Utilisateur avec l'ID " + userId + " n'existe pas.";
        }
    }

    @Override
    @Transactional
    public String deleteUsersByFirstNameAndLastName(String firstName, String lastName) {
        Optional<Users> parentOptional = usersRepository.findUsersByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
        if (parentOptional.isPresent()) {
            Users parent = parentOptional.get();
            usersRepository.deleteUsers(parent.getUser_id());
            return "Utilisateur " + firstName + " " + lastName + " supprimé avec succès.";
        } else {
            return "Utilisateur " + firstName + " " + lastName + " n'existe pas.";
        }
    }
}
