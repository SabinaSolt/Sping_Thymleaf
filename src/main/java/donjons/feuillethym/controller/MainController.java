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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("message", message);

        return "index";
    }

    public List<Person> getPersonsListApi() {
        PersonList personList= restTemplate.getForObject("http://localhost:8081/Personnages",PersonList.class);
        return personList.getPersonList();
    }

    @RequestMapping(value = { "/personList" }, method = RequestMethod.GET)
    public String personList(Model model) {
        model.addAttribute("persons",getPersonsListApi());
        return "personList";
    }
    @RequestMapping(value = { "/personList/{id}" }, method = RequestMethod.GET)
    public String displayPerson(Model model, @PathVariable int id) {
        Person person=restTemplate.getForObject("http://localhost:8081/Personnages/"+id,Person.class);
        model.addAttribute("person", person);
        return "displayPerson";
    }

    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.GET)
    public String showAddPersonPage(Model model) {
        PersonForm personForm = new PersonForm();
        model.addAttribute("personForm", personForm);
        return "addPerson";
    }

    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.POST)
    public String savePerson(Model model, //
                             @ModelAttribute("personForm") PersonForm personForm) {
        int id=personForm.getId();
        String name = personForm.getName();
        String type = personForm.getType();

        if (name != null && name.length() > 0 //
                && type != null && type.length() > 0 && id>-1) {
            Person newPerson = new Person(id,name, type);
            restTemplate.postForEntity("http://localhost:8081/Personnages",newPerson, ResponseEntity.class);
            model.addAttribute("persons",getPersonsListApi());
            return "redirect:/personList";
        }

        model.addAttribute("errorMessage", errorMessage);
        return "addPerson";
    }

}