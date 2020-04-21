package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.database.Sqlite;

@RestController
public class MainRestController {
	
	@Autowired Sqlite database;
	
	@GetMapping("/map/getMapData")
	public String getMapData() {
		
		List<JSONObject> jsonObjects = database.getAllClients();
		List<JSONObject> clients = new ArrayList<>();
		for (Object object : jsonObjects) {
			JSONObject obj = (JSONObject) object;
			JSONObject newJobject = new JSONObject();
			newJobject.put("remoteAddr", obj.get("remoteAddr"));
			newJobject.put("utcTime", obj.get("utcTime"));
			newJobject.put("utcTime", obj.get("utcTime"));
			
			
			if(obj.has("query") && ((String)obj.get("query")).contains("name")) {
				String name = ((String) obj.get("query")).substring(5);
				newJobject.put("name", name);
			} else if(obj.has("upgrade_insecure_requests") && obj.get("upgrade_insecure_requests").equals("1")) {
				newJobject.put("name", "annonim");
			} else {
				newJobject.put("name", "bot");				
			}
			clients.add(newJobject);
		}
		
		List<JSONObject> locations = database.getAllLocation();
		
		List<String> jsons = new ArrayList<>();
		jsons.add(locations.toString());
		jsons.add(clients.toString());
		
	    return jsons.toString();
	}
	
}
