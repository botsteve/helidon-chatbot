package com.gpt.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Snapshot;
import org.eclipse.microprofile.metrics.Timer;
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
    return ChatEndpoint.sessions()
               .stream()
               .map(Session::getId)
               .toList();
  }

  @GET
  @Path("/meanResponseTime")
  public double getMeanResponseTime() {
    Timer messagesTimer = metricRegistry.getTimer(new MetricID("messagesTimer"));
    Snapshot snapshot = messagesTimer.getSnapshot();
    return snapshot.getMean() / 1_000_000_000L;
  }
}
