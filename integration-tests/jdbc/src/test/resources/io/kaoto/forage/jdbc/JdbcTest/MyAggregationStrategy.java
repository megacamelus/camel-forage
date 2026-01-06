package io.kaoto.forage.jdbc.JdbcTest;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Aggregation Strategy that collects exchange bodies into a List.
 * Each incoming exchange's body is added to the list and returned as the aggregated result.
 */
public class MyAggregationStrategy implements AggregationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(MyAggregationStrategy.class);
    
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        LOG.info("New exchange to aggregate with id %s: %s".formatted(newExchange.getIn().getHeader("eventId"), newExchange.getIn().getBody()));
        // Get the body from the new exchange
        Object newBody = newExchange.getIn().getBody();
        
        // If this is the first exchange (oldExchange is null), initialize the list
        if (oldExchange == null) {
            LOG.info("It is a first exchange in a batch.");
            List<Object> list = new ArrayList<>();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        }
        LOG.info("Aggregating the exchange.");
        // Get the existing list from the old exchange
        @SuppressWarnings("unchecked")
        List<Object> list = oldExchange.getIn().getBody(List.class);
        
        // Add the new body to the list
        list.add(newBody);
        
        // Set the updated list back to the exchange
        oldExchange.getIn().setBody(list);

        LOG.info("Current number of aggregated messages: %d".formatted(list.size()));
        
        return oldExchange;
    }
}