package myjavabean.pu;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;

import myjavabean.path.MyPath;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

public class Word2PUVec {
//	public static final String WEB_INF_DIR_PATH = MyPath.WEB_INF_DIR_PATH;
	private static final String FEATURE_SET_FILE_PATH = MyPath.FEATURE_SET_FILE_PATH;
	
	public static double calLen(double[] vector) {
		double sum = 0.0;
		for(double para : vector) {
			sum += para * para;
		}
		return Math.sqrt(sum);
	}
	
	public static double[] norm(double[] vector) {
		double len = calLen(vector);
		for(int i = 0; i < vector.length; i++) {
			vector[i] /= len;
		}
		return vector;
	}
	
	public static ArrayList<String> loadFeatureList(String featureSetFilePath) {
		try {
			ArrayList<String> featureList = new ArrayList<String>();
			featureList.clear();
			
			InputStreamReader isr = new InputStreamReader(new FileInputStream(featureSetFilePath), "UTF-8");
			BufferedReader bufr = new BufferedReader(isr);
		
			String str = null;
			while((str = bufr.readLine()) != null) {
				String[] parts = str.split(" ");
				featureList.add(parts[0]);
//				if(!word.equals("")) {
//					String[] strs = word.split(" ");
//					wordLib_map.put(strs[0], strs[1]);
//				}
			}
			
			return featureList;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] process(String[] sents) {
		System.out.println("Word2PUVec start...");
		try {
			ArrayList<String> featureList = loadFeatureList(FEATURE_SET_FILE_PATH);
			
			for(int i = 0; i < sents.length; i++) {
				String[] words = sents[i].split(" ");
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				tempList.clear();
				for(int j = 0; j < words.length; j++) {
					if(featureList.contains(words[j])) {
						tempList.add( featureList.indexOf(words[j]) );
					}
				}
				
				// 排序
				HashSet<Integer> tempSet = new HashSet<Integer>(tempList);
				ArrayList<Integer> tempListFromSet = new ArrayList<Integer>(tempSet);
				Collections.sort(tempListFromSet);
				double[] vector = new double[featureList.size()];
				for(int j = 0; j < tempListFromSet.size(); j++) {
					int word = tempListFromSet.get(j);
					vector[word] = Collections.frequency(tempList, word);
				}
				// 排序
				vector = norm(vector);
				// 组合回一个句子，给 sent[i]
				String temp = "";
				for(int j = 0; j < tempListFromSet.size(); j++) {
					temp = temp + tempListFromSet.get(j) + ":" + vector[tempListFromSet.get(j)] + " ";
				}
				temp = temp.trim(); //remove the space characters ' '
				
				sents[i] = "1 " + temp;
				// 组合回一个句子，给 sent[i]
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		// System.out.println("end of Word2PUVec");
		
		return sents;
	}
}