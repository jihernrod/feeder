package matxu.framework.agregation.feeder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDbConfiguration {


    public @Bean
    MongoClient mongoClient() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://jihernandez:mongodb@cluster0-srxes.mongodb.net/test?retryWrites=true&w=majority");

        MongoClient mongoClient = new MongoClient(uri);
        return mongoClient;
    }

    public @Bean
    MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "matxu");
    }

}
