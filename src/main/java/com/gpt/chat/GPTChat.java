package com.gpt.chat;

import io.helidon.microprofile.server.RoutingPath;
import io.helidon.microprofile.server.Server;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.Collections;
import java.util.Set;

@ApplicationScoped
@RoutingPath("/")
public class GPTChat implements ServerApplicationConfig {

  @Override
  public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpoints) {
    assert endpoints.isEmpty();
    return Collections.emptySet();
  }

  @Override
  public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> endpoints) {
    return endpoints;
  }

  public static void main(String[] args) {
    Server.create().start();
  }
}