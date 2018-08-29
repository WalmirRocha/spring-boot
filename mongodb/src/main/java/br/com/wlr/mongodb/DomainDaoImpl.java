package br.com.wlr.mongodb;

//imports as static
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class DomainDaoImpl {

	@Autowired
	MongoTemplate mongoTemplate;
	
	public List<HostingCount> getHostingCount() {
	
		Aggregation agg = newAggregation(
			match(Criteria.where("_id").lt(10)),
			group("hosting").count().as("total"),
			project("total").and("hosting").previousOperation(),
			sort(Sort.Direction.DESC, "total")
				
		);

		//Convert the aggregation result into a List
		AggregationResults<HostingCount> groupResults = mongoTemplate.aggregate(agg, Domain.class, HostingCount.class);
		List<HostingCount> result = groupResults.getMappedResults();
		
		return result;
		
	}
}