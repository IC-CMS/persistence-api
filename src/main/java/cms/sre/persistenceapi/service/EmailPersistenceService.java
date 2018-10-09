package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.emailnotifier.Email;
import cms.sre.persistenceapi.model.PersistedEmail;
import cms.sre.persistenceapi.repository.PersistedEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class EmailPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PersistedEmailRepository persistedEmailRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static Email strip(PersistedEmail persistedEmail){
        // Need to fix the constructor to set CreatedDate
        Email email = new Email(
                persistedEmail.getUuid(),
                persistedEmail.getEmailAddress(),
                persistedEmail.getSubject(),
                persistedEmail.getBody());

        email.setCreatedDate(persistedEmail.getCreatedDate());

        return email;
    }

    @Autowired
    public EmailPersistenceService(PersistedEmailRepository persistedEmailRepository, MongoTemplate mongoTemplate){

        this.persistedEmailRepository = persistedEmailRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Email> getEmails(){
        LinkedList<Email> ret = new LinkedList<>();

        List<PersistedEmail> emails = persistedEmailRepository.findAll();

        for (PersistedEmail email : emails) {

            // Check if an invalid email was stored in the database, if it was, log it and remove
            if (email.getEmailAddress() == null || email.getSubject() == null || email.getBody() == null) {
                logger.error("An invalid email was stored in the persistent store");
                logger.error(email.toString());

                persistedEmailRepository.delete(email);

            } else {

                ret.add(strip(email));
            }
        }

        return ret;
    }

    public Email getEmailByUuid(String uuid){


        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        PersistedEmail persistedEmail = mongoTemplate.findOne(query, PersistedEmail.class, "emails");

        Email email = null;

        if (persistedEmail != null) {

            email = new Email(persistedEmail.getUuid(), persistedEmail.getEmailAddress(),persistedEmail.getSubject(),persistedEmail.getBody());
            // Need to fix the constructor to add createdDate
            email.setCreatedDate(persistedEmail.getCreatedDate());
        }

        return email;

    }

    public boolean deleteEmail(String uuid){

        persistedEmailRepository.deleteByUuid(uuid);

        return true;
    }

    public boolean deleteEmail(Email email) {

        PersistedEmail persistedEmail = new PersistedEmail(email);

        persistedEmailRepository.delete(persistedEmail);

        return true;
    }

    public boolean saveEmail(Email email) {

        PersistedEmail persistedEmail = new PersistedEmail(email);

        persistedEmailRepository.save(persistedEmail);

        return true;
    }

}