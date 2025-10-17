package org.apache.camel.forage.core.vertx;

import io.vertx.core.Vertx;
import org.apache.camel.forage.core.common.BeanProvider;

/**
 * Provider interface for creating Vertx instances
 */
public interface VertxProvider extends BeanProvider<Vertx> {}
