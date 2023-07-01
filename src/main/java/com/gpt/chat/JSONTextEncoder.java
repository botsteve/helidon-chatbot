package com.gpt.chat;

import jakarta.json.JsonObject;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

public class JSONTextEncoder implements Encoder.Text<JsonObject> {

  @Override
  public String encode(JsonObject object) throws EncodeException {
    return object.toString();
  }

  @Override
  public void init(EndpointConfig config) { }

  @Override
  public void destroy() { }
}
