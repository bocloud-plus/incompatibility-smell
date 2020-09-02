/**
 * Copyright (2020, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.bocloud;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author henry
 *
 */
public class Main {

	static String DIR = "confs/";
	
	static String NEW_VERSION = "1.18";
	
	static String PRE_VERSION = "1.16";
	
	public static void main(String[] args) throws Exception {
		
		ObjectNode node = new ObjectMapper().createObjectNode();
		
		node.put("ThisVersion", NEW_VERSION);
		node.put("PreviousVersion", PRE_VERSION);
		
		ObjectNode fts = new ObjectMapper().createObjectNode();
		fts.set("added", diffFeatures(DIR + NEW_VERSION, DIR + PRE_VERSION));
		fts.set("removed", diffFeatures(DIR + PRE_VERSION, DIR + NEW_VERSION));
		node.set("features", fts);
		
		ObjectNode ics = new ObjectMapper().createObjectNode();
		diffKinds(ics, DIR + PRE_VERSION, DIR + NEW_VERSION);
		node.set("incompatibility", ics);
		
//		ObjectNode nfs = new ObjectMapper().createObjectNode();
//		diffKinds(nfs, DIR + NEW_VERSION, DIR + PRE_VERSION);
//		node.set("newAtrributes", ics);
		
		System.out.println(node);
	}

	private static void diffKinds(ObjectNode on, String f1, String f2) throws IOException, JsonProcessingException, Exception {
		for (File file : new File(f1).listFiles()) {
			
			if (file.getName().contains("_") || 
					!new File(f2, file.getName()).exists()) {
				continue;
			}
			
			JsonNode oldOne = new ObjectMapper().readTree(
					new File(f1, file.getName()));
			JsonNode newOne = new ObjectMapper().readTree(
					new File(f2, file.getName()));
			
			JsonNode kind = diffKind(oldOne, newOne);
			if (kind != null) {
				on.set(newOne.get("kind").asText(), kind);
			}
		}
	}

	private static ArrayNode diffFeatures(String src, String dst) throws Exception {
		ArrayNode array = new ObjectMapper().createArrayNode();
		
		for (File file : new File(src).listFiles()) {
			
			if (file.getName().contains("_")) {
				continue;
			}
			
			File f = new File(dst, file.getName());
			
			if (!f.exists()) {
				
				ObjectNode item = new ObjectMapper().createObjectNode();
				
				JsonNode obj = new ObjectMapper().readTree(file);
				item.put("apiVersion", obj.get("apiVersion").asText());
				item.put("kind", obj.get("kind").asText());
				
				array.add(item);
			}
		}
		
		return array;
	}
	
	private static ObjectNode diffKind(JsonNode json1, JsonNode json2) throws Exception {
		ObjectNode node = new ObjectMapper().createObjectNode();
		Iterator<String> iter = json1.fieldNames();
		while (iter.hasNext()) {
			String next = iter.next();
			
			boolean isEng = next.matches("[a-zA-Z]+");
			
			if (!isEng) {
				continue;
			}
			
			if (!json2.has(next)) {
				node.set(next, json1.get(next));
			}
			
			if (json1.get(next).isContainerNode()) {
				ObjectNode subNode =  diffKind(json1.get(next), json2.get(next));
				if (subNode != null) {
					node.set(next, subNode);
				}
			}
		}
		return (node.size() == 0) ? null : node;
	}
}
