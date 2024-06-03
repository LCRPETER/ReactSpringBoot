package com.Fullstack.reactSpringBoot.services.parents;


import com.Fullstack.reactSpringBoot.models.userManagement.Parents;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface ParentService extends Serializable {
    List<Parents> findParentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
    Optional<Parents> findParentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    String deleteParentsByUserId(long userId);
    String deleteParentsByFirstNameAndLastName(String firstName, String lastName);
}
