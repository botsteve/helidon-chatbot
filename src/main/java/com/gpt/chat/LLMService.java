package com.gpt.chat;

import com.hexadevlabs.gpt4all.LLModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class LLMService {

  public LLMService() {
    LLModel.OUTPUT_DEBUG = ConfigProvider.getConfig()
                               .getOptionalValue("llm.debug", Boolean.class)
                               .orElse(false);
  }

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

  public String getResponse(Session session, String prompt) {
    try (LLModel model = new LLModel(MODEL_FILE_PATH)) {
      String response = model.generate(prompt, CONFIG, false);
      LOGGER.info(String.format("Session: %s and response: %s", session.getId(), response));
      return response;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}