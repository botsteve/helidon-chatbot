package com.gpt.chat;

import com.hexadevlabs.gpt4all.LLModel;
import jakarta.websocket.Session;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.ConfigProvider;

public class LLMService {

  private LLMService(){}

  private static final Logger LOGGER = Logger.getLogger(LLMService.class.getSimpleName());
  private static final Path MODEL_FILE_PATH = Path.of(ConfigProvider.getConfig().getValue("llm.path", String.class));
  private static final LLModel.GenerationConfig CONFIG = LLModel.config()
                                                             .withNPredict(ConfigProvider.getConfig()
                                                                               .getOptionalValue("llm.npredict", Integer.class)
                                                                               .orElse(4096))
                                                             .withTemp(ConfigProvider.getConfig()
                                                                           .getOptionalValue("llm.temp", Float.class)
                                                                           .orElse(0.28F))
                                                             .withTopK(ConfigProvider.getConfig()
                                                                           .getOptionalValue("llm.topk", Integer.class)
                                                                           .orElse(40))
                                                             .withTopP(ConfigProvider.getConfig()
                                                                           .getOptionalValue("llm.topp", Float.class)
                                                                           .orElse(0.95F))
                                                             .build();
  private static LLModel model;

  private static final AtomicBoolean inUse = new AtomicBoolean(false);

  private static ConcurrentLinkedQueue<SessionPrompt> queue = new ConcurrentLinkedQueue<>();


  public static void getResponse(Session session, String prompt) {
    queue.add(new SessionPrompt(session, prompt));
    LOGGER.info("Added to the queue");
    if (!inUse.get()) {
      inUse.set(true);
      processQueue();
      inUse.set(false);
    }
  }

  public static void init() {
    model = new LLModel(MODEL_FILE_PATH);
    LLModel.OUTPUT_DEBUG = ConfigProvider.getConfig()
                               .getOptionalValue("llm.debug", Boolean.class)
                               .orElse(false);
  }

  private static void processQueue() {
    SessionPrompt sessionPrompt = queue.poll();
    if (Objects.nonNull(sessionPrompt)) {
      Session session = sessionPrompt.session();
      String prompt = sessionPrompt.prompt();
      String response = model.generate(prompt, CONFIG, false);
      LOGGER.info(String.format("Session: %s and response: %s", session.getId(), response));
      session.getAsyncRemote().sendObject(response);
      processQueue();
    }
  }
}
record SessionPrompt(Session session, String prompt) { }