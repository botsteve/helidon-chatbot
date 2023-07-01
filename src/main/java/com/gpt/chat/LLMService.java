package com.gpt.chat;

import com.hexadevlabs.gpt4all.LLModel;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.ConfigProvider;

public class LLMService {

  private static final Logger LOGGER = Logger.getLogger(LLMService.class.getSimpleName());
  private static final Path MODEL_FILE_PATH = Path.of(ConfigProvider.getConfig().getValue("llm.path", String.class));
  private static final LLModel.GenerationConfig CONFIG = LLModel.config().withNPredict(4096).build();
  private static final Map<String, LLModel> sessionToModels = new ConcurrentHashMap<>();

  public static String getResponse(String sessionId, String prompt) {
    LLModel model = getLlModel(sessionId);
    String response = model.generate(prompt, CONFIG, false);
    LOGGER.info(String.format("Session: %s and response: %s", sessionId, response));
    return response;
  }

  private static LLModel getLlModel(String sessionId) {
    if (!sessionToModels.containsKey(sessionId)) {
      sessionToModels.putIfAbsent(sessionId, new LLModel(MODEL_FILE_PATH));
    }
    return sessionToModels.get(sessionId);
  }

  public static void removeSession(String sessionId) {
    try {
      sessionToModels.get(sessionId).close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    sessionToModels.remove(sessionId);
  }
}
