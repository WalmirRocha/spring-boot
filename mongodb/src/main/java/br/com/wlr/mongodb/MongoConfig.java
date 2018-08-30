package br.com.wlr.mongodb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

	// ---------------------------------------------------- MongoTemplate

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoClient(), getDatabaseName());
	}

	@Value("${spring.data.mongodb.host}")
	private String mongoHost;

	@Value("${spring.data.mongodb.port}")
	private int mongoPort;

	@Value("${spring.data.mongodb.database}")
	private String mongoDB;

	@Value("${spring.data.mongodb.username}")
	private String username;

	@Value("${spring.data.mongodb.password}")
	private String password;

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}

	@Override
	@Bean
	public MongoClient mongoClient() {
		MongoClientOptions mongoClientOptions = new MongoClientOptions.Builder().connectTimeout(30000).build();
		MongoCredential credential = MongoCredential.createCredential(username, mongoDB, password.toCharArray());
		MongoClientURI clientURI = new MongoClientURI("mongodb://admin:admin@localhost:27017/?authSource=admin");
		return new MongoClient(clientURI);
	}
}