package com.gpt.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

@ServerEndpoint(value = "/chat",
                decoders = {JSONTextDecoder.class},
                encoders = {JSONTextEncoder.class})
@ApplicationScoped
public class ChatEndpoint {

  private static final Logger LOGGER = Logger.getLogger(ChatEndpoint.class.getSimpleName());
  private static final Set<Session> CHATTERS = new CopyOnWriteArraySet<>();
  private final LLMService llmService;

  @Inject
  public ChatEndpoint(LLMService llmService) {
    this.llmService = llmService;
  }

  @OnOpen
  public void onOpen(Session session) {
    LOGGER.info("WebSocket opened: " + session.getId());
    CHATTERS.add(session);
  }

  @OnMessage
  @Counted(name = "messagesCounted", absolute = true)
  @Metered(name = "messagesMeter", absolute = true, unit = MetricUnits.MILLISECONDS)
  @Timed(name = "messagesTimer", absolute = true, unit = MetricUnits.MILLISECONDS)
  public void onMessage(String message, Session session) {
    LOGGER.info(String.format("Information received: %s from %s ", message, session.getId()));
    try {
      session.getBasicRemote().sendObject(llmService.getResponse(message));
    } catch (IOException | EncodeException e) {
      throw new IllegalStateException(e);
    }
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    LOGGER.info("WebSocket error for " + session.getId() + " " + throwable.getMessage());
    session.getAsyncRemote().sendObject(throwable.getMessage());
  }

  @OnClose
  public void onClose(Session session, CloseReason closeReason) {
    LOGGER.info("WebSocket closed for " + session.getId() + " with reason " + closeReason.getCloseCode());
    CHATTERS.remove(session);
  }

  public static Set<Session> sessions() {
    return CHATTERS;
  }
}