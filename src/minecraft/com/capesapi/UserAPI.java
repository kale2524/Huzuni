package com.capesapi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * A class to help communicate with the CapesAPI API.
 * 
 * @author Matthew Hatcher
 * @version 2.1.0, February 2017
 */

public class UserAPI {
	static String baseURL = "http://capesapi.com/api/v1/";
	
	public static boolean hasCape(UUID uuid, String capeId) throws IOException {
		URL url = new URL(baseURL + uuid.toString() + "/hasCape" + capeId);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer response = new StringBuffer();
		
		while((line = in.readLine()) != null) {
			response.append(line);
		}
		
		in.close();
		
		boolean hasCape = false;
		int responseSays = Integer.parseInt(response.toString());
		
		if(responseSays == 1) {
			hasCape = true;
		}
		
		return hasCape;
	}
	
	public static void addCape(UUID uuid, String capeId) throws IOException {
		String data = "{\"capeId\": \"" + capeId + "\"}";
		URL url = new URL(baseURL + uuid.toString() + "/addCape");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.write(data.getBytes());
		out.flush();
		out.close();
		
		InputStream stream = con.getResponseCode() == 200 ? con.getInputStream() : con.getErrorStream();

        if (stream == null) {
            throw new IOException();
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuffer response = new StringBuffer();
        
        while((line = in.readLine()) != null) {
        	response.append(line);
        }
        
        in.close();
        
        if(response.toString() != "1") {
        	System.out.println(response.toString());
        }
	}
}
