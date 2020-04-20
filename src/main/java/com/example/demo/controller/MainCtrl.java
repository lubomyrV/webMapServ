package com.example.demo.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.database.Sqlite;

@Controller
public class MainCtrl {
	
	@Autowired Sqlite database;
	@Autowired Environment env;
	
	@GetMapping("/")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name, Model model, HttpServletRequest request) {
		model.addAttribute("message","Hello, "+ name+"!");
		
		JSONObject obj = new JSONObject();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		obj.put("query", request.getQueryString());
		obj.put("utcTime", dateFormat.format(new Date()));
		
		Map<String, String> result = getRequestHeadersInMap(request);
		for (String key : result.keySet()) {
			obj.put(key, result.get(key));
		}
		String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        obj.put("remoteAddr", remoteAddr);

        // run async task
        new Thread(() -> saveRecord(database, obj)).start();			
        
		return "index";
	}

	@GetMapping("/map")
	public String showMap() {
		
		return "map";
	}
	
	private void saveRecord(Sqlite database, JSONObject obj) {
		database.insertClient(obj.toString());
		String locationId = database.getLocationIdByAddr(obj.getString("remoteAddr"));
	    if(locationId == null) {
	    	String locationInfo = doHttpLocationRequest(obj.getString("remoteAddr"));
	    	JSONObject jsonObj = new JSONObject(locationInfo);
	    	if(jsonObj.has("ip")) {
	    		database.insertLocation(locationInfo);
	    	} else {
	    		System.out.println("locationInfo="+jsonObj);
	    	}
	    }
	}
	
	private String doHttpLocationRequest(String remoteAddr) {
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(
						"https://api.ipgeolocation.io/ipgeo?apiKey=" + env.getProperty("ipgeoApiKey")
						+ "&ip=" + remoteAddr
						+ "&fields=country_name,state_prov,district,city,isp,latitude,longitude&output=application/json"))
				.build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request,HttpResponse.BodyHandlers.ofString());
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		return response.body();
	}
	
	private Map<String, String> getRequestHeadersInMap(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            result.put(key, value);
        }

        return result;
    }
	
}
