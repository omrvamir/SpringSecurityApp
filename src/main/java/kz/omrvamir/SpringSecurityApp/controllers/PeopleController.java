package kz.omrvamir.SpringSecurityApp.controllers;

import kz.omrvamir.SpringSecurityApp.models.Person;
import kz.omrvamir.SpringSecurityApp.security.PersonDetails;
import kz.omrvamir.SpringSecurityApp.services.PeopleService;
import kz.omrvamir.SpringSecurityApp.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator, PasswordEncoder passwordEncoder) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String people(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        model.addAttribute("person", personDetails.getPerson());

        return "homepage";
    }

    @GetMapping("/{id}")
    public String person(@PathVariable("id") int id, Model model) {
        if (peopleService.findOne(id).isPresent()) {
            model.addAttribute("person", peopleService.findOne(id).get());
            return "people/person";
        }

        return "errors/error-404";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        if (peopleService.findOne(id).isPresent()) {
            model.addAttribute("person", peopleService.findOne(id).get());

            return "people/edit";
        }

        return "errors/error-404";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (peopleService.findOne(id).isPresent()) {
            if (!peopleService.findOne(id).get().getUsername().equals(person.getUsername())) {
                personValidator.validate(person, bindingResult);

                if (bindingResult.hasErrors())
                    return "people/edit";
            }

            String role = peopleService.findOne(id).get().getRole();
            person.setPassword(passwordEncoder.encode(person.getPassword()));

            peopleService.update(id, role, person);

            return "redirect:/people/" + id;
        }

        return "errors/error-404";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        if (peopleService.findOne(id).isPresent()) {
            peopleService.delete(id);

            return "redirect:/auth/login";
        }

        return "errors/error-404";
    }
}
