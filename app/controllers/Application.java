package controllers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import models.Request;
import models.Test;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	private static final String BASE = "data/";

	private static ObjectWriter objectWriter = (new ObjectMapper()).writerWithDefaultPrettyPrinter();

	/**
	 * Home page
	 * @return the home page
	 */
	public static Result index() {
    return ok(index.render());
	}

	/**
	 * Init a test
	 * @return a json return
	 */
	public static Result init(String name, String ip) {
        Logger.debug("Request : init("+name+", "+ip+")");

		//String ip = request().remoteAddress();
        if ((ip != null) && (ip.trim().length() == 0)) {
            ip = null;
        }

		// Save the test
		Test theTest = new Test();
		theTest.ip = ip;
		theTest.name = name;

		theTest.save();

		// Send response
		return returnJson("OK", "Your new test is ready", theTest);
	}

	/**
	 * Get a json file (within a test)
	 * @return the file content
	 */
	public static Result get(String url) {
        Logger.debug("Request : get "+url);

		String ip = request().remoteAddress();
		// Get current test
		Test theTest = Test.getCurrentTest(ip);

		if (theTest == null) {
			return returnJson("KO", "No init has been called from this IP", ip);
		}

		// for (Request r : theTest.requests) {
		// Logger.info(r.id+" "+r.status+" "+r.updateDate);
		// }

		// Prepare the request object
		Request r = new Request();
		r.url = url;
		theTest.requests.add(r);

		// search for the bigger matching path
		String theUrl = "/" + url;
		String[] files = null;
		File dir;

		while (((files == null) || (files.length == 0)) && (theUrl.length() != 0)) {
			dir = new File(getFilePath(theTest, theUrl, null));
			files = dir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return (name.toLowerCase().matches(".*[.]json$")) || (name.toLowerCase().matches(".*[.]html$"));
				}
			});
			if ((files == null) || (files.length == 0)) {
				theUrl = theUrl.substring(0, theUrl.lastIndexOf("/"));
			}

		}

		if ((files == null) || (files.length == 0)) {
			r.status = "KO : Service not found";
			theTest.save();
			//theTest = Test.find.byId(theTest.id);
			return returnJson("KO", "Service not found", url);
		}

		// get the last OK file for this URL
		Request lastOK = null;
		for (Request aRequest : theTest.requests) {
			if (aRequest.status.equals("OK") && aRequest.file.matches("^" + getFilePath(theTest, theUrl, null) + "/[^/]*$")) {
				lastOK = aRequest;
				//Logger.info("LASTOK: "+aRequest.status + " " + aRequest.file + " " + aRequest.file.matches("^" + getFilePath(theTest, theUrl, null) + "/[^/]*$") + " " + getFilePath(theTest, theUrl, null));
				break;
			}
		}

		// get the next file to load
		Arrays.sort(files);
		String nextPath = null;
		String currPath = null;
		if (lastOK != null) {
			for (int i = files.length - 1; i >= 0; i--) {
				nextPath = currPath;
				currPath = getFilePath(theTest, theUrl, files[i]);
				if (currPath.equals(lastOK.file)) {
					break;
				}
			}
		}

		if (nextPath == null) {
			nextPath = getFilePath(theTest, theUrl, files[0]);
		}

		// Found... save and send
		r.status = "OK";
		r.file = nextPath;
		theTest.save();
		//theTest = Test.find.byId(theTest.id);
		//Logger.info("NOW:    "+r.status + " " + r.file + " " + r.file.matches("^" + getFilePath(theTest, theUrl, null) + "/[^/]*$") + " " + getFilePath(theTest, theUrl, null));

        if (nextPath.toLowerCase().matches(".*[.]json$")) {
            response().setContentType("application/json");
        } else if (nextPath.toLowerCase().matches(".*[.]htm[l]*$")) {
            response().setContentType("text/html");
        }

            return ok(new File(r.file));
	}

	/**
	 * Calculate the file path (from BASE, test, service url and filename) 
	 * @param theTest the test
	 * @param theUrl the url
	 * @param file and the file name
	 * @return a path
	 */
	private static String getFilePath(Test theTest, String theUrl, String file) {
		if (file != null) {
			return BASE + theTest.name + theUrl + "/" + file;
		} else {
			return BASE + theTest.name + theUrl;
		}
	}

	/**
	 * Send Json answer
	 * 
	 * @param status ("OK" or "KO")
	 * @param mess the message in the json returned
	 * @param obj the data in the json
	 * @return the json
	 */
	private static Result returnJson(String status, String mess, Object obj) {

		ObjectNode result = Json.newObject();
		result.put("status", status);
		result.put("message", mess);
		if (obj != null) {
			result.put("data", Json.toJson(obj));
		}

		try {
			if ("OK".equals(status)) {
				return ok(objectWriter.writeValueAsString(result));
			} else {
				return badRequest(objectWriter.writeValueAsString(result));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
	}

}
