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
	private final int STRING_WIDTH = 8; // 实体词和关系词的字符串表示长度。
	private final int VECTOR_N = 20; // 嵌入维度
	private final int MARGIN = 5; // 阈值
	private HashMap<String, String> wordLibMap;
	private HashMap<String, double[]> entityId2VecMap;
	private double[] relationVector;
	/**
	 * 构造方法。初始化词库映射关系、实体词向量、关系词向量
	 */
	public TransE() {
		this.wordLibMap = loadWordLibMap(WORD_LIB_PATH); // 词库映射关系
		this.entityId2VecMap = loadEntityId2VecMap(ENTITY_VECTOR_PATH); // 加载实体词向量
		this.relationVector = loadRelationVector(RELATION_VECTOR_PATH); // 加载关系词向量
		System.out.println("创建TransE算法模型工具类对象 TransE Object has been built..."); // 服务器端输出提示创建TransE算法模型工具类对象完成。
	}
    public void norm(double[] a) { // 限制实体向量a的模在1以内
        double len = vecLen(a);
        if (len > 1) {
			for (int ii = 0; ii < a.length; ii++)
				a[ii] /= len;
		}
    }
	public double vecLen(double[] a) { // 返回向量a的模
		double res = 0;
		for (int i = 0; i < a.length; i++)
			res += a[i] * a[i];
		return Math.sqrt(res);
	}
	public HashMap<String, double[]> loadEntityId2VecMap(String entityVectorPath) { // 加载实体词向量
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
	public double[] loadRelationVector(String relationVectorPath) { // 加载关系词向量
		try {
			double[] relationVector = new double[VECTOR_N];
			InputStreamReader isr = new InputStreamReader(new FileInputStream(relationVectorPath),"utf-8");
			BufferedReader bufrRelation = new BufferedReader(isr);
			String str = bufrRelation.readLine(); // 因为只有一个关系量，所以直接读取一行就完成了。
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
	/**
	 * 主方法。获取输入文本中的所有名词，略过词库中没有的词，两两之间计算相似度。
	 * @param positions
	 * @param sInput
	 * @return
	 */
    public ArrayList<String> process(int[] positions, String sInput) {
        int n = VECTOR_N;		// 嵌入维数
		int margin = MARGIN;	// 阈值
		
		String[] sents = sInput.split("[。 ！ ？]"); // 将输入文本按中文标点符号句号、感叹号、问号分句。
		String[] pos_sents = new String[positions.length];
		for(int i = 0; i < pos_sents.length; i++) {
			pos_sents[i] = sents[ positions[i] ]; // 按正例句子的索引值获取所有正例句子的文本。
		}
		try {
			ArrayList<String> mList = new ArrayList<String>();
			mList.clear();
			boolean flag = false;
			for(String sent : sents) { // 遍历所有正例句子。
				ArrayList<String> entity = getNoun(sent); // 获取当前句子中的所有名词。
				ArrayList<String> entityId = new ArrayList<String>();
				entityId.clear();
				int i = 0;
				while(i < entity.size()) { // 遍历当前句子中的所有名词。
					if(!this.wordLibMap.containsKey(entity.get(i))) { // 词库中没有当前处理的词语。
//						System.out.println("Error: no such word in wordLib ... \"" + entity.get(i) + "\""); // 服务器端输出提示 没有该词语：[词语文本]。
						entity.remove(i); // 将改词从当前处理句子的词集中去掉。
					} // if
					else {
						String temp = String.valueOf(this.wordLibMap.get(entity.get(i))); // 获取词语对应id。
						int restTimes = STRING_WIDTH - temp.length();
						for(int j = 0; j < restTimes; j++)
							temp = "0" + temp; // 按规定长度，整理id的字符串长度。
						entityId.add(temp); // 装载 entity_id
						i++;
					}
				} // for
				double[][] entityVector = new double[entityId.size()][VECTOR_N];
				for (i = 0; i < entityVector.length; i++) {
					entityVector[i] = this.entityId2VecMap.get(entityId.get(i)); // 装载该句子所包含词的向量。
				}
				for(i = 0; i < entityVector.length; i++) {
					for(int j = 0; j < entityVector.length; j++) {
						if(i != j) {
							double dist = calcSum(entityVector[i], entityVector[j], this.relationVector); // 两两之间计算曼哈顿距离
							if(dist < MARGIN) {
								flag = true;
								mList.add(entity.get(i) + " " + entity.get(j)); // 收集符合阈值规则的头尾实体元组。
//								System.out.println(entity.get(i) + ", " + entity.get(j) + " " + dist + " / " + MARGIN); // 服务器端输出提示 该头尾实体文本及之间的曼哈顿距离。
							}
						}
					} // for
				} // for
			} // for
			if(flag) {
				System.out.println("[细粒度]共抽取出 " + mList.size() + " 个元组");
				return mList;
			}
			else {
				System.out.println("[细粒度]共抽取出 0 个元组");
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    public ArrayList<String> process(String sInput) {
        int n = VECTOR_N;		// 嵌入维数
		int margin = MARGIN;	// 阈值
		
		String[] pos_sents = sInput.split("。"); // 将输入文本按中文标点符号句号、感叹号、问号分句。
//		System.out.println("[TransE]");
//		for (String sent : pos_sents) {
//			System.out.println(sent);
//		}
		try {
			ArrayList<String> mList = new ArrayList<String>();
			mList.clear();
			boolean flag = false;
			for(String sent : pos_sents) { // 遍历所有正例句子。
				ArrayList<String> entity = getNoun(sent); // 获取当前句子中的所有名词。
				ArrayList<String> entityId = new ArrayList<String>();
//				entityId.clear();
				int i = 0;
				while(i < entity.size()) { // 遍历当前句子中的所有名词。
					if(!this.wordLibMap.containsKey(entity.get(i))) { // 词库中没有当前处理的词语。
//						System.out.println("Error: no such word in wordLib ... \"" + entity.get(i) + "\""); // 服务器端输出提示 没有该词语：[词语文本]。
						entity.remove(i); // 将改词从当前处理句子的词集中去掉。
					} // if
					else {
						String temp = String.valueOf(this.wordLibMap.get(entity.get(i))); // 获取词语对应id。
						int restTimes = STRING_WIDTH - temp.length();
						for(int j = 0; j < restTimes; j++)
							temp = "0" + temp; // 按规定长度，整理id的字符串长度。
						entityId.add(temp); // 装载 entity_id
						i++;
					}
				} // for
				double[][] entityVector = new double[entityId.size()][VECTOR_N];
				for (i = 0; i < entityVector.length; i++) {
					entityVector[i] = this.entityId2VecMap.get(entityId.get(i)); // 装载该句子所包含词的向量。
				}
				for(i = 0; i < entityVector.length; i++) {
					for(int j = 0; j < entityVector.length; j++) {
						if(i != j) {
							double dist = calcSum(entityVector[i], entityVector[j], this.relationVector); // 两两之间计算曼哈顿距离
							if(dist < MARGIN) {
								flag = true;
								mList.add(entity.get(i) + " " + entity.get(j)); // 收集符合阈值规则的头尾实体元组。
//								System.out.println(entity.get(i) + ", " + entity.get(j) + " " + dist + " / " + MARGIN); // 服务器端输出提示 该头尾实体文本及之间的曼哈顿距离。
							}
						}
					} // for
				} // for
			} // for
			if(flag) {
				System.out.println("[细粒度]共抽取出 " + mList.size() + " 个元组");
				return mList;
			}
			else {
				System.out.println("[细粒度]共抽取出 0 个元组");
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    public double calcSum(double[] e1, double[] e2, double[] rel) {// 计算实体e2和e1+rel的曼哈顿距离
        double sum = 0;
        for(int ii = 0; ii < e1.length; ii++)
        	sum += Math.abs(e2[ii] - e1[ii] - rel[ii]); // L1距离
        return sum;
    }
	public ArrayList<String> getNoun(String sent) { // 通过张华平分词工具，获取一个句子中的所有名词
		NlpirTest nlpir = new NlpirTest(); // 初始化张华平分词工具类对象。
		ArrayList<String> list = new ArrayList<String>();
		list.clear();
		sent = nlpir.segment(sent);
		String[] words = sent.split(" ");
		for(String w :words) {
			if(!w.equals("") && w.contains("/n"))
				list.add(nlpir.modify(w));
		}
		nlpir.exit(); // 销毁张华平分词工具类对象。
		return list;
	}
	public HashMap<String, String> loadWordLibMap(String wordLibPath) { // 加载 词库映射关系。
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
}
