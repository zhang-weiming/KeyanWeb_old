// 编码 utf-8
package myjavabean.transe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import java.lang.Math;
import java.io.*;

import myjavabean.nlp.NlpirTest;
import myjavabean.path.MyPath;

public class TransE {
	private final String WEB_INF_DIR_PATH = MyPath.WEB_INF_DIR_PATH;
	private final String WORD_LIB_PATH = WEB_INF_DIR_PATH + "/data/transe/word_library/Word_Library.wordlib";
	private final String RELATION_VECTOR_PATH = WEB_INF_DIR_PATH + "/data/transe/model/relation2vec.bern";
	private final String ENTITY_VECTOR_PATH = WEB_INF_DIR_PATH + "/data/transe/model/entity2vec.bern";
	private final int STRING_WIDTH = 8;
	private final int VECTOR_N = 20;	// 嵌入维度
	private final int MARGIN = 5; // 阈值

	private HashMap<String, String> wordLibMap;
	private HashMap<String, double[]> entityId2VecMap;
	private double[] relationVector;


	public TransE() {
		this.wordLibMap = loadWordLibMap(WORD_LIB_PATH);
		this.entityId2VecMap = loadEntityId2VecMap(ENTITY_VECTOR_PATH);
		this.relationVector = loadRelationVector(RELATION_VECTOR_PATH);

		System.out.println("TransE Object has been built...");
	}

	// 限制实体向量a的模在1以内
    public void norm(double[] a) {
        double len = vecLen(a);
        if (len > 1) {
			for (int ii = 0; ii < a.length; ii++)
				a[ii] /= len;
		}
        // return a;
    }
	// 限制实体向量a的模在1以内

	// 返回向量a的模
	public double vecLen(double[] a) {
		double res = 0;
		for (int i = 0; i < a.length; i++)
			res += a[i] * a[i];
		return Math.sqrt(res);
	}
	// 返回向量a的模

	// load entity_vec ************
	public HashMap<String, double[]> loadEntityId2VecMap(String entityVectorPath) {
		try {
			HashMap<String, double[]> entityId2VecMap = new HashMap<String, double[]>();
			entityId2VecMap.clear();

			InputStreamReader isr = new InputStreamReader(new FileInputStream(entityVectorPath),"utf-8");
			BufferedReader bufrEntity = new BufferedReader(isr);
			String str = null;
			while((str = bufrEntity.readLine()) != null) {
				if(!str.equals("")) {
					String[] strs = str.split("\t");
					String tempEntity = strs[0];
					double[] tempEntityVector = new double[VECTOR_N];
					for(int i = 0; i < tempEntityVector.length; i++) {
						tempEntityVector[i] = Double.parseDouble(strs[i + 1]);
					}
//					norm(tempEntityVector);
					entityId2VecMap.put(tempEntity, tempEntityVector);
				}
			}
			bufrEntity.close();
			return entityId2VecMap;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	// load entity_vec ************
	
	// load relation_vec ************
	public double[] loadRelationVector(String relationVectorPath) {
		try {
			double[] relationVector = new double[VECTOR_N];

			InputStreamReader isr = new InputStreamReader(new FileInputStream(relationVectorPath),"utf-8");
			BufferedReader bufrRelation = new BufferedReader(isr);
			String str = bufrRelation.readLine(); //只有一个关系量
			String[] parts = str.split("\t");
			for(int i = 0; i < relationVector.length; i++) {
				relationVector[i] = Double.parseDouble(parts[i + 1]);
			}
			bufrRelation.close();
			return relationVector;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	// load relation_vec end ********
	
	// TransE 处理
    public ArrayList<String> process(int[] positions, String sInput) {
        int n = VECTOR_N;		// 嵌入维数
		int margin = MARGIN;	// 阈值
        // rate = rate_in;
        // margin = margin_in;
        // method = method_in;

		// 由 pos的下标 和 所有句子，得到 pos 句子，放在 pos_sents 中
		String[] sents = sInput.split("[。 ！ ？]");
//		String tempStr = "[TransE]\n";
//		for(String str : sents) {
//			tempStr += str + "\n";
//		}
//		System.out.println(tempStr);
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
				ArrayList<String> entity = getNoun(sent);
				ArrayList<String> entityId = new ArrayList<String>();
				entityId.clear();
				int i = 0;
				while(i < entity.size()) {
					if(!this.wordLibMap.containsKey(entity.get(i))) {
						// the word is not in wordLib, then add it into the word lib
						System.out.println("Error: no such word in wordLib ... \"" + entity.get(i) + "\"");
						// OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(wordLibPath, true)); // append
						// BufferedWriter bufw_add = new BufferedWriter(osw);
						// bufw_add.write(entity[i] + " " + (this.wordLib_map.keySet().size() + 1));
						// bufw_add.flush();
						// bufw_add.newLine();
						// bufw_add.close();
						// osw.close();

						// this.wordLib_map = loadWordLibMap(wordLibPath);

						// 实体词不在词库中
						entity.remove(i);
					} // if
					else {
						// 装载 entity_id
						String temp = String.valueOf(this.wordLibMap.get(entity.get(i)));
						int restTimes = STRING_WIDTH - temp.length();
						for(int j = 0; j < restTimes; j++)
							temp = "0" + temp;
						entityId.add(temp);
						// 装载 entity_id
						i++;
					}
				} // for

				// 实体向量
				double[][] entityVector = new double[entityId.size()][VECTOR_N];
				for (i = 0; i < entityVector.length; i++) {
					entityVector[i] = this.entityId2VecMap.get(entityId.get(i));
//					// 限制每个实体向量的模在1以内
//					norm(entityVector[j]);
				}
				// 实体向量
				// initialize entity_vec end ****
				
				for(i = 0; i < entityVector.length; i++) {
					for(int j = 0; j < entityVector.length; j++) {
						if(i != j) {
							double dist = calcSum(entityVector[i], entityVector[j], this.relationVector);
							if(dist < MARGIN) {
								flag = true;
								mList.add(entity.get(i) + " " + entity.get(j));
								System.out.println(entity.get(i) + ", " + entity.get(j) + " " + dist + " / " + MARGIN);
							}
						}
					} // for
				} // for
			} // for

			if(flag) {
//				String[] datas = new String[mList.size()];
//				System.out.println("[TransE]There they are: ");
//				for(int i = 0; i < mList.size(); i++) {
//					System.out.println(mList.get(i));
//					datas[i] = mList.get(i);
//				}
//				return datas;
//				System.out.println("[TransE]There they are: ");
//				for(int i = 0; i < mList.size(); i++) {
//					System.out.println(mList.get(i));
//				}
				return mList;
			}
			else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

//		// 未知错误，返回出错状态
//		System.out.println("[TransE]ERROR: No data!");
//
//		String[] temp_list = new String[2];
//		temp_list[0] = "ERROR";
//		temp_list[1] = "no data!";
//		
//		return temp_list;
//		// 未知错误，返回出错状态
		return null;
	}
	// TransE 处理

	
	// 计算实体e2和e1+rel的距离
    public double calcSum(double[] e1, double[] e2, double[] rel)
    {
        double sum = 0;
        for(int ii = 0; ii < e1.length; ii++)
        	sum += Math.abs(e2[ii] - e1[ii] - rel[ii]); // L1距离
//           	sum += Math.pow(e2[ii] - e1[ii] - rel[ii], 2);
//		sum = Math.sqrt(sum);
//         if(L1_flag)
//        	 for(int ii=0; ii<n; ii++)
//            	 sum+=fabs(entity_vec[e2][ii]-entity_vec[e1][ii]-relation_vec[rel][ii]);//L1距离
//         else
//        	 for(int ii=0; ii<n; ii++)
//            	 sum+=sqr(entity_vec[e2][ii]-entity_vec[e1][ii]-relation_vec[rel][ii]);//L2距离
        return sum;
    }
	// 计算实体e2和e1+rel的距离
	
	// 通过分词工具，获取一个句子中的所有名词
	public ArrayList<String> getNoun(String sent) {
		NlpirTest nlpir = new NlpirTest();
		ArrayList<String> list = new ArrayList<String>();
		list.clear();
		
		sent = nlpir.segment(sent);
		String[] words = sent.split(" ");
		for(String w :words) {
			if(!w.equals("") && w.contains("/n"))
				list.add(nlpir.modify(w));
		}
		
//		String[] temp = new String[list.size()];
//		for(int i = 0; i < temp.length; i++)
//			temp[i] = list.get(i);
		
		nlpir.exit();
		return list;
	}
	// 通过分词工具，获取一个句子中的所有名词
	
	// 加载 词库 Map
	public HashMap<String, String> loadWordLibMap(String wordLibPath) {
		try {
			HashMap<String, String> wordLibMap = new HashMap<String, String>();
			wordLibMap.clear();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(wordLibPath),"utf-8");
			BufferedReader bufrWordLib = new BufferedReader(isr);
			String word = null;
			int word_id = 1;
			while((word = bufrWordLib.readLine()) != null && !word.equals("")) {
				String[] strs = word.split(" ");
				wordLibMap.put(strs[0], strs[1]);
			}
			return wordLibMap;
		} catch(Exception e) {
			System.out.println("Error: cannot load word library ... details are as follows:");
			e.printStackTrace();
		}
		return null;
	}
	// 加载 词库 Map
	
}
