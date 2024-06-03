package com.Fullstack.reactSpringBoot.services.parents;

import com.Fullstack.reactSpringBoot.models.userManagement.Parents;
import com.Fullstack.reactSpringBoot.repositories.userManagement.ParentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParentServiceImpl implements ParentService {

    private final ParentsRepository parentsRepository;

    @Autowired
    public ParentServiceImpl(ParentsRepository parentsRepository) {
        this.parentsRepository = parentsRepository;
    }

    @Override
    public List<Parents> findParentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName) {
        return parentsRepository.findParentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);

    }

    @Override
    public Optional<Parents> findParentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName) {
        return parentsRepository.findParentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);

    }

    @Override
    @Transactional
    public String deleteParentsByUserId(long userId) {
        Optional<Parents> parentOptional = parentsRepository.findById(userId);
        if (parentOptional.isPresent()) {
            parentsRepository.deleteParents(userId);
            return "Parent with ID " + userId + " deleted successfully.";
        } else {
            return "Parent with ID " + userId + " not found.";
        }
    }

    @Override
    @Transactional
    public String deleteParentsByFirstNameAndLastName(String firstName, String lastName) {
        Optional<Parents> parentOptional = parentsRepository.findParentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
        if (parentOptional.isPresent()) {
            Parents parent = parentOptional.get();
            parentsRepository.deleteParents(parent.getUser_id());
            return "Parent " + firstName + " " + lastName + " deleted successfully.";
        } else {
            return "Parent " + firstName + " " + lastName + " not found.";
        }
    }
}
