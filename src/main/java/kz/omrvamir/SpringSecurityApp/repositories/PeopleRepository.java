package kz.omrvamir.SpringSecurityApp.repositories;

import kz.omrvamir.SpringSecurityApp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);

    @Modifying
    @Query("UPDATE Person m SET m.role = 'ROLE_ADMIN' WHERE m.id = ?1")
    void appointAdmin(int i);

    @Modifying
    @Query("UPDATE Person m SET m.role = 'ROLE_USER' WHERE m.id = ?1")
    void demoteAdmin(int i);

}
