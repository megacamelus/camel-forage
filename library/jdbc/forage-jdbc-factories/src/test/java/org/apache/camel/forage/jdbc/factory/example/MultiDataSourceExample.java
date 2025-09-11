package org.apache.camel.forage.jdbc.factory.example;

import org.apache.camel.builder.RouteBuilder;

/**
 * Example demonstrating how to use MultiDataSourceFactory with multiple DataSource configurations.
 *
 * This example shows how to:
 * 1. Configure multiple DataSources (ds1, ds2) via properties file
 * 2. Use different DataSources in the same route by setting the dataSourceId header
 * 3. Access different databases/tables with each DataSource
 */
public class MultiDataSourceExample extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Example route using MultiDataSourceFactory with header-based DataSource selection
        from("timer:multiDataSourceTest?period=5000")
                // Set header to select first DataSource (ds1) for 'acme' table
                .setHeader("dataSourceId", constant("ds1"))
                .to(
                        "sql:select * from acme?dataSourceFactory=#class:org.apache.camel.forage.jdbc.factory.MultiDataSourceFactory")
                .log("Results from ds1 (acme table): ${body}")

                // Set header to select second DataSource (ds2) for 'foo' table
                .setHeader("dataSourceId", constant("ds2"))
                .to(
                        "sql:select * from foo?dataSourceFactory=#class:org.apache.camel.forage.jdbc.factory.MultiDataSourceFactory")
                .log("Results from ds2 (foo table): ${body}");

        // Alternative approach: Using different routes for different DataSources
        from("direct:acmeQuery")
                .setHeader("dataSourceId", constant("ds1"))
                .to(
                        "sql:select * from acme where id = :#id?dataSourceFactory=#class:org.apache.camel.forage.jdbc.factory.MultiDataSourceFactory")
                .log("Acme record: ${body}");

        from("direct:fooQuery")
                .setHeader("dataSourceId", constant("ds2"))
                .to(
                        "sql:select * from foo where name = :#name?dataSourceFactory=#class:org.apache.camel.forage.jdbc.factory.MultiDataSourceFactory")
                .log("Foo record: ${body}");
    }
}
