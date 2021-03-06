package donjons.feuillethym.controller;

import java.util.ArrayList;
import java.util.List;


import donjons.feuillethym.form.PersonForm;
import donjons.feuillethym.model.Person;
import donjons.feuillethym.model.PersonList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class MainController {

    @Autowired
    private RestTemplate restTemplate;

    // Injectez (inject) via application.properties.
    @Value("${welcome.message}")
    private String message;

    @Value("${error.message}")
    private String errorMessage;

    @Value("${urlApi.person}")
    private String urlApiPerson;

    @GetMapping(value = { "/", "/index" })
    public String index(Model model) {
        model.addAttribute("message", message);
        return "index";
    }

    public List<Person> getPersonsListApi() {
        PersonList personList= restTemplate.getForObject(urlApiPerson,PersonList.class);
        return personList.getPersonList();
    }

    @GetMapping(value = { "/personList" })
    public String personList(Model model) {
        model.addAttribute("persons",getPersonsListApi());
        return "personList";
    }
    @GetMapping(value = { "/personList/{id}" })
    public String displayPerson(Model model, @PathVariable int id) {
        Person person=restTemplate.getForObject(urlApiPerson+"/"+id,Person.class);
        model.addAttribute("person", person);
        return "displayPerson";
    }

    @GetMapping(value = { "/addPerson" })
    public String showAddPersonPage(Model model) {
        PersonForm personForm = new PersonForm();
        model.addAttribute("personForm", personForm);
        return "addPerson";
    }

    @PostMapping(value = { "/addPerson" })
    public String savePerson(Model model, @ModelAttribute("personForm") PersonForm personForm) {
        int id=personForm.getId();
        String name = personForm.getName();
        String type = personForm.getType();

        if (name != null && name.length() > 0 //
                && type != null && type.length() > 0 && id>-1) {
            Person newPerson = new Person(id,name, type);
            restTemplate.postForEntity(urlApiPerson,newPerson, ResponseEntity.class);
            model.addAttribute("persons",getPersonsListApi());
            return "redirect:/personList";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "addPerson";
    }

    @GetMapping( { "/updatePerson/{id}" })
    public String showUpdatePersonPage(Model model, @PathVariable int id) {
        PersonForm personForm = new PersonForm();
        personForm.setId(id);
        model.addAttribute("personForm", personForm);
        return "updatePerson";
    }

    @PostMapping(value = { "/updatePerson" })
    public String updatePerson(Model model, @ModelAttribute("personForm") PersonForm personForm) {
        int id=personForm.getId();
        String name = personForm.getName();
        String type = personForm.getType();
        if (name != null && name.length() > 0 //
                && type != null && type.length() > 0 && id>-1) {
            Person newPerson = new Person(id,name, type);
            restTemplate.put(urlApiPerson+"/"+id,newPerson);
        }
        List<Person> personList=getPersonsListApi();
        model.addAttribute("persons", personList);
        return "redirect:/personList";

    }

    @GetMapping(value = { "/deletePerson/{id}" })
    public String deletePerson(Model model, @PathVariable int id) {
        restTemplate.delete(urlApiPerson+"/"+id,ResponseEntity.class);
        model.addAttribute("persons",getPersonsListApi());
        return "redirect:/personList";
    }

}