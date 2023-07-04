package com.gpt.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

@ServerEndpoint(value = "/chat",
                decoders = {JSONTextDecoder.class},
                encoders = {JSONTextEncoder.class})
@ApplicationScoped
public class ChatEndpoint {

  private static final Logger LOGGER = Logger.getLogger(ChatEndpoint.class.getSimpleName());

  protected static final Set<Session> chatters = new CopyOnWriteArraySet<>();

  private final LLMService llmService;

  @Inject
  public ChatEndpoint(LLMService llmService) { this.llmService = llmService; }

  @OnOpen
  public void onOpen(Session session) {
    LOGGER.info("WebSocket opened: " + session.getId());
    chatters.add(session);
  }

  @OnMessage
  public void onMessage(String message, Session session) {
    LOGGER.info(String.format("Information received: %s from %s ", message, session.getId()));
    session.getAsyncRemote().sendObject(llmService.getResponse(session, message));
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    LOGGER.info("WebSocket error for " + session.getId() + " " + throwable.getMessage());
  }

  @OnClose
  public void onClose(Session session, CloseReason closeReason) {
    LOGGER.info("WebSocket closed for " + session.getId() + " with reason " + closeReason.getCloseCode());
    chatters.remove(session);
  }
}