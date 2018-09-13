package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.product_list.Product;
import cms.sre.persistenceapi.model.PersistedProduct;
import cms.sre.persistenceapi.repository.PersistedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ProductPersistenceService {

    private PersistedProductRepository persistedProductRepository;
    private MongoTemplate mongoTemplate;

    private static Product strip(PersistedProduct persistedProduct){
        return new Product()
                .setClassification(persistedProduct.getClassification())
                .setDevelopers(persistedProduct.getDevelopers())
                .setDivision(persistedProduct.getDivision())
                .setLane(persistedProduct.getLane())
                .setLifecycleStatus(persistedProduct.getLifecycleStatus())
                .setNeedsSCM(persistedProduct.getNeedsSCM())
                .setOrg(persistedProduct.getOrg())
                .setProductStatus(persistedProduct.getProductStatus())
                .setProgram(persistedProduct.getProgram())
                .setScmLocation(persistedProduct.getScmLocation())
                .setSection(persistedProduct.getSection())
                .setSspName(persistedProduct.getSspName())
                .setTitle(persistedProduct.getTitle());
    }

    @Autowired
    public ProductPersistenceService(PersistedProductRepository persistedProductRepository, MongoTemplate mongoTemplate){
        this.persistedProductRepository = persistedProductRepository;
    }

    public List<Product> getProducts(){
        LinkedList<Product> ret = new LinkedList<>();
        this.persistedProductRepository.findAll().forEach(persistedProduct -> {
            ret.add(strip(persistedProduct));
        });

        return ret;
    }

    public List<Product> getProductsByTitle(String title){
        LinkedList<Product> ret = new LinkedList<>();
        this.persistedProductRepository.findByTitle(title).forEach(persistedProduct -> {
            ret.add(strip(persistedProduct));
        });
        return ret;
    }

    public List<Product> getProductsByScmLocation(String scmLocation){
        LinkedList<Product> ret = new LinkedList<>();
        this.persistedProductRepository.findByScmLocation(scmLocation).forEach(persistedProduct -> {
            ret.add(strip(persistedProduct));
        });
        return ret;
    }

    public boolean upsert(Product product){
        Query query = new Query()
                .addCriteria(Criteria.where("title").is(product.getTitle()));

        Update update = new Update()
                .set("classification", product.getClassification())
                .set("developers", product.getDevelopers())
                .set("division", product.getDivision())
                .set("lane", product.getLane())
                .set("lifecycleStatus", product.getLifecycleStatus())
                .set("needsSCM", product.getNeedsSCM())
                .set("org", product.getOrg())
                .set("productStatus", product.getProductStatus())
                .set("program", product.getProgram())
                .set("scmLocation", product.getScmLocation())
                .set("section", product.getSection())
                .set("sspName", product.getSspName())
                .set("title", product.getTitle());

        return this.mongoTemplate.upsert(query, update, Product.class)
                .wasAcknowledged();
    }

    public boolean upsert(List<Product> products){
        boolean ret = true;
        for(Product product : products){
            ret = this.upsert(product) && ret;
        }
        return ret;
    }

    public boolean remove(List<Product> products){
        Criteria criteria = new Criteria();

        for(Product product : products){
            criteria.orOperator(Criteria.where("title").is(product.getTitle()));
        }

        return this.mongoTemplate.remove(new Query().addCriteria(criteria), Product.class)
                .wasAcknowledged();
    }

}
