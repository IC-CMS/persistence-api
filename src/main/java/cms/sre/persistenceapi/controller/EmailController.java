package cms.sre.persistenceapi.controller;

import cms.sre.dna_common_data_model.emailnotifier.Email;
import cms.sre.persistenceapi.service.EmailPersistenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class EmailController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EmailPersistenceService emailPersistenceService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public EmailController(EmailPersistenceService emailPersistenceService){
        this.emailPersistenceService = emailPersistenceService;
    }

    @GetMapping("/email")
    public List<Email> getEmails(){
        return this.emailPersistenceService.getEmails(); }

    @GetMapping("/email/{uuid}")
    public Email getProductsByTitle(@PathVariable("uuid") String uuid){

        return this.emailPersistenceService.getEmailByUuid(uuid);

    }

    @RequestMapping(value = "/email", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String saveEmail(@RequestBody String requestBody) throws IOException {

        String prettyString = null;

        try {

            JSONObject json = new JSONObject(requestBody);
            prettyString = json.toString(4);

            Email email = mapper.readValue(requestBody, Email.class);

            this.emailPersistenceService.saveEmail(email);

        } catch (Exception e) {

            logger.debug("Email: " + prettyString);
            return "Email store request failed";
        }

        return "Processed Store Requested";
    }

    @DeleteMapping("/email/{uuid}")
    public void deleteEmail(@PathVariable String uuid){
        logger.info("Deleting Email:" + uuid);
        emailPersistenceService.deleteEmail(uuid);
    }

    @DeleteMapping("/email")
    public void deleteEmail(@PathVariable Email email){

        emailPersistenceService.deleteEmail(email);
    }
}
