package com.gpt.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

@Path("/dashboard")
@ApplicationScoped
public class DashboardController {

  @Inject
  @RegistryType(type = MetricRegistry.Type.APPLICATION)
  MetricRegistry metricRegistry;

  @GET
  @Path("/sessions")
  public List<String> getSession() {
    return ChatEndpoint.chatters.stream().map(Session::getId).toList();
  }
}