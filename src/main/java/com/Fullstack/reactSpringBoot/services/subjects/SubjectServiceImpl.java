package com.Fullstack.reactSpringBoot.services.subjects;



import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Subjects;
import com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class SubjectServiceImpl implements SubjectService{


    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional
    public String deleteSubjectById(long subjectId) {
        Optional<Subjects> subjectOptional = subjectRepository.findById(subjectId);
        if (subjectOptional.isPresent()) {
            subjectRepository.deleteSubjects(subjectId);
            return "Matière avec l'ID " + subjectId + " supprimé avec succès.";
        } else {
            return "Matière avec l'ID " + subjectId + " n'existe pas.";
        }
    }
}
