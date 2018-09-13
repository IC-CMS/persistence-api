package cms.sre.persistenceapi.model;

import cms.sre.dna_common_data_model.product_list.Product;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PersistedProduct extends Product {

    public PersistedProduct(){
        super();
    }

    public PersistedProduct(Product product){
        super();

        super.setClassification(product.getClassification())
                .setDevelopers(product.getDevelopers())
                .setDivision(product.getDivision())
                .setLane(product.getLane())
                .setLifecycleStatus(product.getLifecycleStatus())
                .setNeedsSCM(product.getNeedsSCM())
                .setOrg(product.getOrg())
                .setProductStatus(product.getProductStatus())
                .setProgram(product.getProgram())
                .setScmLocation(product.getScmLocation())
                .setSection(product.getSection())
                .setSspName(product.getSspName())
                .setTitle(product.getTitle());
    }
}
