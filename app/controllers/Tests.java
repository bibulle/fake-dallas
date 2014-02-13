package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import models.Property;
//import models.Value;

import models.Test;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.POJONode;

import play.libs.F.*;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;

public class Tests extends Controller {

	private static ObjectWriter objectWriter = (new ObjectMapper()).writerWithDefaultPrettyPrinter();

	/**
	 * List all the properties
	 * 
	 * @return
	 */
	public static Result list() {
		// Get the properties
		List<Test> list = Test.findAllOrderByDate();

		try {
			return ok(objectWriter.writeValueAsString(list));

		} catch (JsonGenerationException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
	}

//	private static Map<WebSocket.In<JsonNode>, WebSocket.Out<JsonNode>> webSocketOuts = new HashMap<WebSocket.In<JsonNode>, WebSocket.Out<JsonNode>>();
//
//	public static WebSocket<JsonNode> webSocket() {
//		return new WebSocket<JsonNode>() {
//
//			@Override
//			public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {
//
//				in.onClose(new Callback0() {
//					@Override
//					public void invoke() throws Throwable {
//						webSocketOuts.remove(in);
//					}
//				});
//
//				in.onMessage(new Callback<JsonNode>() {
//					@Override
//					public void invoke(JsonNode event) throws Throwable {
//						// System.out.println("############" + event);
//						String action = event.get("action").asText();
//						// System.out.println(action);
//
//						if (action.equalsIgnoreCase("refresh")) {
//							try {
//								// System.out.println("let's go");
//								long lastUpdateDate = (event.get("lastUpdateDate") == null ? 0 : event.get("lastUpdateDate").asLong(0));
//								long lastUpdateId = (event.get("lastUpdateId") == null ? 0 : event.get("lastUpdateId").asLong(0));
//								int count = (event.get("count") == null ? 10 : event.get("count").asInt(10));
//								// System.out.println(lastUpdateDate);
//								// System.out.println(count);
//								// Send all
//								// System.out.println("============Debut");
//								List<Property> properties = Property.findOrderByUpdateDate(lastUpdateDate, lastUpdateId, count);
//								for (Property property : properties) {
//									sendNews("save", property);
//								}
//								if (properties.isEmpty()) {
//									ObjectNode noRefreshEvent = Json.newObject();
//									noRefreshEvent.put("action", "noRefreshNeeded");
//									out.write(noRefreshEvent);
//								}
//								// System.out.println("============Fin");
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//
//					}
//				});
//
//				webSocketOuts.put(in, out);
//
//			}
//
//		};
//
//	}

//	public static void sendNews(String action, Property property) {
//		final ObjectNode event = Json.newObject();
//		event.put("action", action);
//		// event.put("property", objectWriter.writeValueAsString(property));
//		event.put("property", Json.toJson(property));
//
//		Akka.system().scheduler().scheduleOnce(Duration.Zero(), new Runnable() {
//			@Override
//			public void run() {
//				for (WebSocket.Out<JsonNode> out : webSocketOuts.values()) {
//					out.write(event);
//				}
//			}
//		}, Akka.system().dispatcher());
//	}

}