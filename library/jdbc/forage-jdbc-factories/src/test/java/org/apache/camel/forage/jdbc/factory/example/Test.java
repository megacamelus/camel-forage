package org.apache.camel.forage.jdbc.factory.example;

import org.apache.camel.builder.RouteBuilder;

/**
 * Test route demonstrating multiple DataSource usage with MultiDataSourceFactory.
 *
 * Configuration required in forage-multi-datasource-factory.properties:
 * - multi.datasource.names=ds1,ds2
 * - datasource.id.source.type=header
 * - datasource.id.source.name=dataSourceId
 * - ds1.provider.datasource.class=<your-datasource-provider-class>
 * - ds2.provider.datasource.class=<your-datasource-provider-class>
 */
public class Test extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:java?period=1000")
                // Set header to select ds1 DataSource
                .setHeader("dataSourceId", constant("ds1"))
                .to(
                        "sql:select * from acme?dataSourceFactory=#class:org.apache.camel.forage.jdbc.factory.MultiDataSourceFactory")
                .log("${body}")

                // Set header to select ds2 DataSource
                .setHeader("dataSourceId", constant("ds2"))
                .to(
                        "sql:select * from foo?dataSourceFactory=#class:org.apache.camel.forage.jdbc.factory.MultiDataSourceFactory")
                .log("${body}");
    }
}
