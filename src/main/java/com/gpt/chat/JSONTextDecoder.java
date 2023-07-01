package com.gpt.chat;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;
import java.io.StringReader;

public class JSONTextDecoder implements Decoder.Text<JsonObject> {

  @Override
  public JsonObject decode(String s) {
    try (JsonReader jsonReader = Json.createReader(new StringReader(s))) {
      return jsonReader.readObject();
    }
  }

  @Override
  public boolean willDecode(String s) {
    try (JsonReader jsonReader = Json.createReader(new StringReader(s))) {
      jsonReader.readObject();
      return true;
    } catch (JsonException e) {
      return false;
    }
  }

  @Override
  public void init(EndpointConfig config) {
  }

  @Override
  public void destroy() {
  }
}