package kz.omrvamir.SpringSecurityApp.controllers;

import kz.omrvamir.SpringSecurityApp.models.Person;
import kz.omrvamir.SpringSecurityApp.repositories.PeopleRepository;
import kz.omrvamir.SpringSecurityApp.security.PersonDetails;
import kz.omrvamir.SpringSecurityApp.services.AdminService;
import kz.omrvamir.SpringSecurityApp.services.PeopleService;
import kz.omrvamir.SpringSecurityApp.services.RegistrationService;
import kz.omrvamir.SpringSecurityApp.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/people")
public class AdminController {

    private final AdminService adminService;
    private final PeopleService peopleService;
    private final PersonValidator personValidator;
    private final PasswordEncoder passwordEncoder;
    private final PeopleRepository peopleRepository;
    private final RegistrationService registrationService;



    @Autowired
    public AdminController(AdminService adminService, PeopleService peopleService, PersonValidator personValidator,
                           PasswordEncoder passwordEncoder, PeopleRepository peopleRepository, RegistrationService registrationService) {
        this.adminService = adminService;
        this.peopleService = peopleService;
        this.personValidator = personValidator;
        this.passwordEncoder = passwordEncoder;
        this.peopleRepository = peopleRepository;
        this.registrationService = registrationService;
    }

    @GetMapping
    public String people(Model model) {
        adminService.adminTemp();

        model.addAttribute("people", peopleService.findAll());

        return "admin/people";
    }

    @GetMapping("/{id}")
    public String personInfo(@PathVariable("id") int id, Model model) {
        if (peopleService.findOne(id).isPresent()) {
            model.addAttribute("person", peopleService.findOne(id).get());

            return "admin/person";
        }

        return "errors/error-404";
    }

    @GetMapping("/{id}/edit")
    public String personEdit(@PathVariable("id") int id, Model model) {
        if (peopleService.findOne(id).isPresent()) {
            model.addAttribute("person", peopleService.findOne(id).get());

            return "admin/edit";
        }

        return "errors/error-404";
    }

    @PatchMapping("/{id}")
    public String personUpdate(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
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

            return "redirect:/admin/people";
        }

        return "errors/error-404";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "admin/new";
    }

    @PostMapping()
    public String createPerson(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "admin/new";

        registrationService.register(person);

        return "redirect:/admin/people";
    }

    @PatchMapping("/{id}/appointAdmin")
    public String appointAdmin(@PathVariable("id") int id) {
        peopleService.appointAdmin(id);

        return "redirect:/admin/people";
    }

    @PatchMapping("/{id}/demoteAdmin")
    public String demoteAdmin(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        if (personDetails.getPerson().getId() == id) {
            peopleService.demoteAdmin(id);

            return "redirect:/auth/login";
        }
        peopleService.demoteAdmin(id);

        return "redirect:/admin/people";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        if (personDetails.getPerson().getId() == id) {
            peopleService.delete(id);

            return "redirect:/auth/login";
        }
        peopleService.delete(id);

        return "redirect:/admin/people";
    }
}
