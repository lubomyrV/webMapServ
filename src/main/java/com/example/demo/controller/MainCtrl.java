package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainCtrl {
	
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
        
	    BufferedWriter writer = null;
	    String path = System.getProperty("user.dir") + File.separator +"logs.json";
	    
	    String fileContent = "";		
	    File f = new File(path);
	    if(!f.exists() && !f.isDirectory()) { 
	    	fileContent += "[";
	    } else {
	    	fileContent += ",";
	    }
	    
	    fileContent += obj.toString();
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
		} catch (IOException e) {
			System.out.println("ex. open "+e.getMessage());
		}  
	    try {
			writer.write(fileContent);
		} catch (IOException e) {
			System.out.println("ex. write "+e.getMessage());
		}
	    try {
			writer.close();
		} catch (IOException e) {
			System.out.println("ex. close "+ e.getMessage());
		}

	    boolean saveRes = addrService(remoteAddr);
	    System.out.println("saveRes="+saveRes);
	    
		return "index";
	}

	@GetMapping("/map")
	public String showMap() {

		return "map";
	}
	
	private boolean addrService(String remoteAddr) {
		boolean result = false;
		
		boolean needCall = saveNewAddr(remoteAddr);
	    //System.out.println("needCall="+needCall);
	    if(needCall) {
	    	String respJson = getReqAddrInfo(remoteAddr);
	    	result = writeAddrInfo(respJson);
	    }
		
		return result;
	}
	
	private boolean writeAddrInfo(String respJson) {
		
		BufferedWriter writer = null;
	    String path = System.getProperty("user.dir") + File.separator +"addrsInfo.json";
	    
	    String fileContent = "";		
	    File f = new File(path);
	    if(!f.exists() && !f.isDirectory()) { 
	    	fileContent += "[";
	    }
	    
	    fileContent += respJson+",";
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
		} catch (IOException e) {
			System.out.println("ex. open "+e.getMessage());
		}  
	    try {
			writer.write(fileContent);
		} catch (IOException e) {
			System.out.println("ex. write "+e.getMessage());
		}
	    try {
			writer.close();
		} catch (IOException e) {
			System.out.println("ex. close "+ e.getMessage());
		}
	    
	    return true;
	}
	
	private String getReqAddrInfo(String remoteAddr) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(
						"https://api.ipgeolocation.io/ipgeo?apiKey=7d4f0da043a041faafbf2fbd542c9f17&ip="
						+remoteAddr+
						"&fields=country_name,state_prov,district,city,isp,latitude,longitude&output=application/json"))
				.build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request,
					HttpResponse.BodyHandlers.ofString());
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		return response.body();
	}
	
	private boolean saveNewAddr(String newRemoteAddr) {
		
		BufferedWriter writer = null;
	    String path = System.getProperty("user.dir") + File.separator +"remoteAddrs.txt";
	    
	    String fileContent = "";		
	    File f = new File(path);
	    if(f.exists() && !f.isDirectory()) { 
			String everything = "";		
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(path));
			} catch (FileNotFoundException e) {
				System.err.println("FileNotFoundException "+e.getMessage());
			}
			try {
			    StringBuilder sb = new StringBuilder();
			    String line = null;
				try {
					line = br.readLine();
				} catch (IOException e) {
					System.err.println("IOException readLine "+e.getMessage());
				}

			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        try {
						line = br.readLine();
					} catch (IOException e) {
						System.err.println("IOException append "+e.getMessage());
					}
			    }
			    everything = sb.toString();
			} finally {
			    try {
					br.close();
				} catch (IOException e) {
					System.err.println("IOException close "+e.getMessage());
				}
			}
			
			String[] oldRemoteAddrs = everything.split(",");
			Set<String> oldAddrSet = new HashSet<>();
			for (String oldAddr : oldRemoteAddrs) {
				String addr = oldAddr.trim();
				if(!addr.isBlank()) {
					oldAddrSet.add(addr);
				}
			}
			
			if(!oldAddrSet.contains(newRemoteAddr)) {
				//add new addr
				fileContent += (","+newRemoteAddr);
			}
	    } else {
	    	//new file
	    	fileContent += newRemoteAddr;
	    }
	    	    
		try {
			writer = new BufferedWriter(new FileWriter(path, true));//Set true for append mode
		} catch (IOException e) {
			System.out.println("ex. open "+e.getMessage());
		}  
	    try {
			writer.write(fileContent);
		} catch (IOException e) {
			System.out.println("ex. write "+e.getMessage());
		}
	    try {
			writer.close();
		} catch (IOException e) {
			System.out.println("ex. close "+ e.getMessage());
		}
	    
	    if(fileContent.isEmpty()) {
	    	return false;
	    }
	    return true;	    	
	}
	
	public List<JSONObject> jsonLogReader() {
		String path = System.getProperty("user.dir") + File.separator +"logs.json";
		String everything = "";		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException "+e.getMessage());
		}
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.err.println("IOException readLine "+e.getMessage());
			}

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        try {
					line = br.readLine();
				} catch (IOException e) {
					System.err.println("IOException append "+e.getMessage());
				}
		    }
		    everything = sb.toString();
		} finally {
		    try {
				br.close();
			} catch (IOException e) {
				System.err.println("IOException close "+e.getMessage());
			}
		}
		
		everything += "]";
		
		JSONArray jsonObjects = null;
		try {
			jsonObjects = new JSONArray(everything);
		}catch (JSONException err){
			System.out.println("JSONException "+err.toString());
		}
		
		List<JSONObject> jsonObjectList = new ArrayList<>();
		for (Object object : jsonObjects) {
			JSONObject obj = (JSONObject) object;
			JSONObject newJobject = new JSONObject();
			newJobject.put("remoteAddr", obj.get("remoteAddr"));
			newJobject.put("utcTime", obj.get("utcTime"));
			newJobject.put("utcTime", obj.get("utcTime"));
			if(obj.has("query")) {
				String query = (String) obj.get("query");
				if(query.contains("name")) {
					String name = query.substring(5);
					newJobject.put("name", name);
				}
			} else if(obj.has("upgrade-insecure-requests")){
				String uir = (String)obj.get("upgrade-insecure-requests");
				if(uir.equalsIgnoreCase("1")) {
					newJobject.put("name", "annonim");						
				}
			}
			if(!newJobject.has("name")) {
				newJobject.put("name", "bot");
			}
			jsonObjectList.add(newJobject);
		}
		return jsonObjectList;
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
