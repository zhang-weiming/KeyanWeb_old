/**
 * Word2PUVec.java
 */
package myjavabean.pu;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import myjavabean.path.MyPath;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
public class Word2PUVec {
//	public static final String WEB_INF_DIR_PATH = MyPath.WEB_INF_DIR_PATH;
	private static final String WEB_INF_DIR_PATH = MyPath.WEB_INF_DIR_PATH;
	private static final String FEATURE_SET_FILE_PATH = MyPath.FEATURE_SET_FILE_PATH;
	private static final String SOURCE_FILE_PATH = WEB_INF_DIR_PATH + "/data/pu/model/temp.txt"; // 文本向量保存路径。
	public static double calLen(double[] vector) { // 计算向量的模
		double sum = 0.0;
		for(double para : vector) {
			sum += para * para;
		}
		return Math.sqrt(sum);
	}
	public static double[] norm(double[] vector) { // 向量单位化
		double len = calLen(vector);
		for(int i = 0; i < vector.length; i++) {
			vector[i] /= len;
		}
		return vector;
	}
	/**
	 * 加载词库，放入ArrayList对象中，由于其排列有序，与链表中的索引值自动形成映射关系。
	 * @param featureSetFilePath
	 * @return
	 */
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
			}
			return featureList;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 主程序。提前设置好一个词库，将每个词语映射为唯一的id。通过该词库将文本向量化。
	 * @param sents
	 * @return
	 */
	public static String[] process(String[] sents) {
		System.out.println("Word2PUVec start...");
		try {
			ArrayList<String> featureList = loadFeatureList(FEATURE_SET_FILE_PATH); // 加载词语和id的映射。
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(SOURCE_FILE_PATH), "utf-8");
			BufferedWriter bufw = new BufferedWriter(osw);
			for(int i = 0; i < sents.length; i++) { // 遍历所有句子，分别做处理。
				String[] words = sents[i].split(" ");
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				tempList.clear();
				for(int j = 0; j < words.length; j++) {
					if(featureList.contains(words[j])) {
						tempList.add( featureList.indexOf(words[j]) );
					}
				}
				
				// 排序
				HashSet<Integer> tempSet = new HashSet<Integer>(tempList); // 应svm_classify.exe输入文件（词袋模型）的规则约束，需要将所有id值排序。
				ArrayList<Integer> tempListFromSet = new ArrayList<Integer>(tempSet);
				Collections.sort(tempListFromSet);
				double[] vector = new double[featureList.size()];
				for(int j = 0; j < tempListFromSet.size(); j++) {
					int word = tempListFromSet.get(j);
					vector[word] = Collections.frequency(tempList, word);
				}
				// 排序
				vector = norm(vector); // 向量单位化（归一化）
				// 组合回一个句子，给 sent[i]
				String temp = "";
				for(int j = 0; j < tempListFromSet.size(); j++) {
					temp = temp + (tempListFromSet.get(j) + 1) + ":" + vector[tempListFromSet.get(j)] + " ";
				}
				temp = temp.trim();
				bufw.write("1 " + temp); // 保存对当前句子的文本表示结果到临时文件中。
				bufw.flush();
				bufw.newLine();
				// 组合回一个句子，给 sent[i]
			}
			bufw.close();
			System.out.println("文本向量化完成 create a source file (temp.txt)..."); // 服务器端输出提示文本向量化完成。
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sents;
	}
}
