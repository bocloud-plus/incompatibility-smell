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
	
	static String NEW_VERSION = null;
	
	static String PRE_VERSION = null;
	
	public static void main(String[] args) throws Exception {
		
		input(args);
		analyze();
	}

	protected static void input(String[] args) {
		
		help(args);
		
		for (int i = 0; i < args.length; i = i + 2) {
			if (args[i].trim().equals("--dir")) {
				DIR = args[i + 1];
			} else if (args[i].trim().equals("--thisVersion")) {
				NEW_VERSION = args[i + 1];
			} else if (args[i].trim().equals("--preVersion")) {
				PRE_VERSION = args[i + 1];
			}
		}
		
		if (NEW_VERSION == null || PRE_VERSION == null) {
			help(args);
		}
	}

	protected static void help(String[] args) {
		if (args == null || args.length < 4 || args.length%2 != 0 || args[0].trim().equals("help")) {
			System.out.println("Usage: java -jar incompatibility-smell-0.1-jar-with-dependencies.jar [params]");
			System.out.println("--dir\t,可选参数，目录为confs");
			System.out.println("--thisVersion\t,必填，如1.18");
			System.out.println("--preVersion\t,必填，如1.17");
			System.exit(1);
		}
	}

	/********************************************************************************************
	 * 
	 * 
	 *                    Core
	 * 
	 * 
	 ********************************************************************************************/
	protected static void analyze() throws Exception, IOException, JsonProcessingException {
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
		
		ObjectNode nfs = new ObjectMapper().createObjectNode();
		diffKinds(nfs, DIR + NEW_VERSION, DIR + PRE_VERSION);
		node.set("newFields", nfs);
		
		System.out.println(node.toPrettyString());
	}
	
	protected static ArrayNode diffFeatures(String src, String dst) throws Exception {
		ArrayNode array = new ObjectMapper().createArrayNode();
		
		for (File file : new File(src).listFiles()) {
			
			File f = new File(dst, file.getName());
			// 文件在不同版本Kubernetes目录下都存在，意味着两个版本都支持
			// 如果文件名有下划线，一般是一种Kind（如Pod）的额外解释
			if (f.exists() || file.getName().contains("_")) {
				continue;
			}
			
			// 记录dst目录下不支持的类型
			ObjectNode item = new ObjectMapper().createObjectNode();
			
			JsonNode obj = new ObjectMapper().readTree(file);
			item.put("apiVersion", obj.get("apiVersion").asText());
			item.put("kind", obj.get("kind").asText());
			
			array.add(item);
		}
		
		return array;
	}
	

	protected static void diffKinds(ObjectNode on, String f1, String f2) throws IOException, JsonProcessingException, Exception {
		
		for (File file : new File(f1).listFiles()) {
			
			// 如果文件名有下划线，一般是一种Kind（如Pod）的额外解释
			// 如果文件不存在，则说明在diffFeatures中已经处理过
			if (file.getName().contains("_") || 
					!new File(f2, file.getName()).exists()) {
				continue;
			}
			
			JsonNode oldOne = new ObjectMapper()
					.readTree(new File(f1, file.getName()));
			JsonNode newOne = new ObjectMapper()
					.readTree(new File(f2, file.getName()));
			
			JsonNode kind = diffKind(oldOne, newOne);
			if (kind != null) {
				on.set(newOne.get("kind").asText(), kind);
			}
		}
	}

	
	
	protected static ObjectNode diffKind(JsonNode json1, JsonNode json2) throws Exception {
		
		ObjectNode node = new ObjectMapper().createObjectNode();
		Iterator<String> iter = json1.fieldNames();
		
		while (iter.hasNext()) {
			String next = iter.next();
			
			// 为了区别JSON中Map，一般key不是英文
			if (next.matches("[a-zA-Z]+") == false) {
				continue;
			} else if (!json2.has(next)) {
				node.set(next, json1.get(next));
			} else if (json1.get(next).isContainerNode()) {
				ObjectNode subNode =  diffKind(json1.get(next), json2.get(next));
				if (subNode != null) {
					node.set(next, subNode);
				}
			}
		}
		return (node.size() == 0) ? null : node;
	}
}
