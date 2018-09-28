package cms.sre.persistenceapi.model;

import cms.sre.dna_common_data_model.emailnotifier.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "emails")
public class PersistedEmail extends Email {

    @Id
    private String id;

    public PersistedEmail(){
        super();
    }

    public PersistedEmail(Email email){
        super(email.getUuid(), email.getEmailAddress(),email.getSubject(), email.getBody());
        super.setCreatedDate(email.getCreatedDate());
    }

    public String getId() {
        return id;
    }

    public PersistedEmail setId(String id) {
        this.id = id;
        return this;
    }
}