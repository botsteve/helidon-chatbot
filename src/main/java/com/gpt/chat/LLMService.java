package com.gpt.chat;

import com.hexadevlabs.gpt4all.LLModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.file.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class LLMService {

  private final String modelFilePath;
  private static final LLModel.GenerationConfig CONFIG = LLModel.config().withNPredict(4096).build();

  @Inject
  public LLMService(@ConfigProperty(name = "llm.path") String modelFilePath) { this.modelFilePath = modelFilePath; }

  public String getResponse(String prompt) {
    try (LLModel model = new LLModel(Path.of(modelFilePath))) {
      return model.generate(prompt, CONFIG, false);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}