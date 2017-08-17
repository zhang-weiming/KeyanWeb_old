// 编码 utf-8
package myjavabean.transe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import java.lang.Math;
import java.io.*;

import myjavabean.nlp.NlpirTest;

public class TransE {
	public static final String WEB_INF_PATH = "/opt/apache-tomcat-9.0.0.M18/webapps/keyan/WEB-INF";
	public static final String WORD_LIB_PATH = WEB_INF_PATH + "/data/Util/word_library/wordlib_utf-8/Word_Library.wordlib";
	// public static final String wordVecPath = WEB_INF_PATH + "/Util/word_library/transe/yuliao_vector_utf_8.txt";
	public static final String RELATION_VECTOR_PATH = WEB_INF_PATH + "/data/transe/model/relation2vec.bern";
	public static final String ENTITY_VECTOR_PATH = WEB_INF_PATH + "/data/transe/model/entity2vec.bern";
	public static final int STRING_WIDTH = 8;

	// TransE 参数
	public static int VECTOR_N = 100;	// 嵌入维度
	public static int MARGIN = 1; // 阈值
	// TransE 参数


	private HashMap<String, String> wordLib_map;
	// private HashMap<String, double[]> wordVec_map;
	private HashMap<String, double[]> entity_id2vec_map;
	private double[] relation_vec;


	public TransE() {
		this.wordLib_map = loadWordLibMap(WORD_LIB_PATH);
		this.entity_id2vec_map = loadEntityId2VecMap(ENTITY_VECTOR_PATH);
		this.relation_vec = loadRelationVector(RELATION_VECTOR_PATH);

		System.out.println("TransE has been called...");
	}

	// 限制实体向量a的模在1以内
    public void norm(double[] a) {
        double x = vec_len(a);
        if (x > 1) {
			for (int ii = 0; ii < a.length; ii++)
				a[ii] /= x;
		}
        // return a;
    }
	// 限制实体向量a的模在1以内

	// 返回向量a的模
	public double vec_len(double[] a) {
		double res = 0;
		for (int i = 0; i < a.length; i++)
			res += a[i] * a[i];
		return Math.sqrt(res);
	}
	// 返回向量a的模

	// load entity_vec ************
	public HashMap<String, double[]> loadEntityId2VecMap(String entity_vector_path) {
		try {
			HashMap<String, double[]> entity_id2vec_map = new HashMap<String, double[]>();
			entity_id2vec_map.clear();

			BufferedReader bufr_entity = new BufferedReader(new FileReader(entity_vector_path));
			String str = null;
			while((str = bufr_entity.readLine()) != null) {
				if(!str.equals("")) {
					String[] strs = str.split("\t");
					String entity_temp = strs[0];
					double[] entity_vec_temp = new double[VECTOR_N];
					for(int i = 0; i < entity_vec_temp.length; i++) {
						entity_vec_temp[i] = Double.parseDouble(strs[i + 1]);
					}
					entity_id2vec_map.put(entity_temp, entity_vec_temp);
				}
			}
			
			return entity_id2vec_map;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	// load entity_vec ************
	
	// load relation_vec ************
	public double[] loadRelationVector(String relation_vector_path) {
		try {
			double[] relation_vec = new double[VECTOR_N];

			BufferedReader bufr_rel = new BufferedReader(new FileReader(relation_vector_path));
			String str = bufr_rel.readLine();
			String[] relation_vec_strs = str.split("\t");
			for(int i = 0; i < relation_vec.length; i++) {
				relation_vec[i] = Double.parseDouble(relation_vec_strs[i + 1]);
			}
			
			return relation_vec;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	// load relation_vec end ********
	
	// TransE 处理
    public String[] transE_process(int[] positions, String sInput) {
        int n = VECTOR_N;		// 嵌入维数
		int margin = MARGIN;	// 阈值
        // rate = rate_in;
        // margin = margin_in;
        // method = method_in;

		
		// 由 pos的下标 和 所有句子，得到 pos 句子，放在 pos_sents 中
		String[] sents = sInput.split("[。 ！ ？]");
		String[] pos_sents = new String[positions.length];
		for(int i = 0; i < pos_sents.length; i++) {
			pos_sents[i] = sents[ positions[i] ];
		}
		// 由 pos的下标 和 所有句子，得到 pos 句子，放在 pos_sents 中


		try {
			ArrayList<String> mList = new ArrayList<String>();
			mList.clear();

			boolean flag = false;
			for(String sent : sents) {
				// initialize entity_vec ********
				String[] entity = getNoun(sent);
				String[] entity_id = new String[entity.length];
				double[][] entity_vec = new double[entity.length][VECTOR_N];
				for(int i = 0; i < entity.length; i++) {
					if(!this.wordLib_map.containsKey(entity[i])) {
						// the word is not in wordLib, then add it into the word lib
						System.out.println("Error: no such word in wordLib ... \"" + entity[i] + "\"");
						// OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(wordLibPath, true)); // append
						// BufferedWriter bufw_add = new BufferedWriter(osw);
						// bufw_add.write(entity[i] + " " + (this.wordLib_map.keySet().size() + 1));
						// bufw_add.flush();
						// bufw_add.newLine();
						// bufw_add.close();
						// osw.close();

						// this.wordLib_map = loadWordLibMap(wordLibPath);

						// 有实体词不在词库中，按出错处理，并返回出错状态
						String[] temp_list = new String[2];
						temp_list[0] = "ERROR";
						temp_list[1] = "no such word: " + entity[i];
						return temp_list;
						// 有实体词不在词库中，按出错处理，并返回出错状态
					} // if
					
					// 装载 entity_id
					// entity_id[i] = this.wordLib_map.get(entity[i]);
					String temp = String.valueOf(this.wordLib_map.get(entity[i]));
					int restTimes = STRING_WIDTH - temp.length();
					for(int j = 0; j < restTimes; j++)
						temp = "0" + temp;
					entity_id[i] = temp;
					// 装载 entity_id
				} // for
				
				// 对实体向量进行归一化处理
				for (int i = 0; i < entity_vec.length; i++) {
					entity_vec[i] = this.entity_id2vec_map.get(entity_id[i]);
					// 限制每个实体向量的模在1以内
					norm(entity_vec[i]);
				}
				// 对实体向量进行归一化处理
				// initialize entity_vec end ****
				
				
				for(int i = 0; i < entity_vec.length; i++) {
					for(int j = 0; j < entity_vec.length; j++) {
						if(i != j) {
							double dist = calc_sum(entity_vec[i], entity_vec[j], this.relation_vec);
							if(dist < MARGIN) {
								flag = true;
								mList.add(entity[i] + " " + entity[j]);
							}
							
							System.out.println(entity[i] + ", " + entity[j] + " " + dist);
						}
					}
				}
			}

			if(flag) {
				String[] datas = new String[mList.size()];

				System.out.println("[TransE]There they are: ");
				for(int i = 0; i < mList.size(); i++) {
					System.out.println(mList.get(i));
					datas[i] = mList.get(i);
				}

				return datas;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		// 未知错误，返回出错状态
		System.out.println("[TransE]ERROR: No data!");

		String[] temp_list = new String[2];
		temp_list[0] = "ERROR";
		temp_list[1] = "no data!";
		
		return temp_list;
		// 未知错误，返回出错状态
	}
	// TransE 处理

	
	// 计算实体e2和e1+rel的距离
    public double calc_sum(double[] e1, double[] e2, double[] rel)
    {
        double sum = 0;
        for(int ii = 0; ii < e1.length; ii++)
            	// sum += Math.abs(e2[ii] - e1[ii] - rel[ii]);//L1距离
           	sum += Math.pow(e2[ii] - e1[ii] - rel[ii], 2);
		sum = Math.sqrt(sum);
        // if(L1_flag)
        	// for(int ii=0; ii<n; ii++)
            	// sum+=fabs(entity_vec[e2][ii]-entity_vec[e1][ii]-relation_vec[rel][ii]);//L1距离
        // else
        	// for(int ii=0; ii<n; ii++)
            	// sum+=sqr(entity_vec[e2][ii]-entity_vec[e1][ii]-relation_vec[rel][ii]);//L2距离
        return sum;
    }
	// 计算实体e2和e1+rel的距离
	
	// 通过分词工具，获取一个句子中的所有名词
	public String[] getNoun(String sent) {
		NlpirTest nlpir = new NlpirTest();
		ArrayList<String> list = new ArrayList<String>();
		list.clear();
		
		sent = nlpir.segment(sent);
		String[] words = sent.split(" ");
		for(String w :words) {
			if(!w.equals("") && w.contains("/n"))
				list.add(nlpir.modify(w));
				
		}
		
		String[] temp = new String[list.size()];
		for(int i = 0; i < temp.length; i++)
			temp[i] = list.get(i);
		
		nlpir.exit();
		return temp;
	}
	// 通过分词工具，获取一个句子中的所有名词
	
	// 加载 词库 Map
	public HashMap<String, String> loadWordLibMap(String wordLibPath) {
		HashMap<String, String> wordLib_map_temp = new HashMap<String, String>();
		wordLib_map_temp.clear();
		
		try {
			BufferedReader bufr_wordLib = new BufferedReader(new FileReader(wordLibPath));
		
			String word = null;
			int word_id = 1;
			while((word = bufr_wordLib.readLine()) != null && !word.equals("")) {
				String[] strs = word.split(" ");
				wordLib_map_temp.put(strs[0], strs[1]);

        		// System.out.println("load word #" + word_id++);
			}
			
		} catch(Exception e) {
			System.out.println("Error: cannot load word library ... details are as follows:");
			e.printStackTrace();
		}
		return wordLib_map_temp;
	}
	// 加载 词库 Map
	
}
