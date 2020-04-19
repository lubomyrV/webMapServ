package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainRestController {
	
	@GetMapping("/map/getMapData")
	public String getMapData() {
		//System.out.println("getMapData");
		MainCtrl mcrtl = new MainCtrl();
		List<JSONObject> sjsonList = mcrtl.jsonLogReader();
		
		String path = System.getProperty("user.dir") + File.separator +"addrsInfo.json";
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
		
		
		everything += "{}]";
		//System.out.println("everything="+everything.length());
		List<String> jsons = new ArrayList<>();
		jsons.add(everything);
		jsons.add(sjsonList.toString());
	    return jsons.toString();
	}
	
}
