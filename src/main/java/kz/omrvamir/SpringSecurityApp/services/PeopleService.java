package kz.omrvamir.SpringSecurityApp.services;

import kz.omrvamir.SpringSecurityApp.models.Person;
import kz.omrvamir.SpringSecurityApp.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Optional<Person> findOne(int id) {
        return peopleRepository.findById(id);
    }

    @Transactional
    public void update(int id, String role, Person updatedPerson) {
        updatedPerson.setId(id);
        updatedPerson.setRole(role);

        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void appointAdmin(int id) {
        peopleRepository.appointAdmin(id);
    }

    @Transactional
    public void demoteAdmin(int id) {
        peopleRepository.demoteAdmin(id);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    public Optional<Person> findPersonByUsername(String username) {
        return peopleRepository.findByUsername(username);
    }
}
