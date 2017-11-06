/**
 * PU.java
 */
package myjavabean.pu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import myjavabean.nlp.NlpirTest;
import myjavabean.path.MyPath; 

public class PU {
	private static final String WEB_INF_DIR_PATH = MyPath.WEB_INF_DIR_PATH;
	private static final String EXE_PATH = WEB_INF_DIR_PATH + "/data/pu/model/svm_classify.exe";
	private static final String MODEL_FILE_PATH = WEB_INF_DIR_PATH + "/data/pu/model/model";
	private static final String RESULT_DIR_PATH = WEB_INF_DIR_PATH + "/data/pu/result";
	private static final String SOURCE_FILE_PATH = WEB_INF_DIR_PATH + "/data/pu/model/temp.txt";
	public static String count(File resultFile, String[] sents) {
		try {
			if(resultFile.exists()) {
				BufferedReader bufr = new BufferedReader(new FileReader(resultFile));
				int posCounter = 0, negCounter = 0;
				String pos_positions = "";
				String neg_positions = "";
				String str = null;
				int sentId = 0;
				while((str = bufr.readLine()) != null) 
				{
					str = str.trim();
					if(!str.equals("")) 
					{
						if (!sents[sentId].equals("")) { // 跳过空句子
							if(str.charAt(0) == '-') 
							{
								//neg sent
								negCounter++;
								neg_positions += sentId + " ";
							}
							else 
							{
								//pos sent
								posCounter++;
								pos_positions += sentId + " ";
							}
						}
					}
					sentId++;
				}
				pos_positions = pos_positions.trim();
				neg_positions = neg_positions.trim();
				if (pos_positions.equals("")) {
					pos_positions = "null";
				}
				if (neg_positions.equals("")) {
					neg_positions = "null";
				}
//				System.out.println("\tPos: " + posCounter + ", Neg: " + negCounter);
//				System.out.println("\tPos: " + pos_positions.trim() + ", Neg: " + neg_positions.trim());
				String returnData = posCounter + " " + negCounter + "|" + pos_positions + "|" + neg_positions;
				System.out.println("PU finished. return: " + returnData);
				return returnData;
			}
			else 
			{
				System.out.println("PU failed: No result file...");
				return null;
			}
		} catch(Exception e) {
			System.out.println("PU Error");
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 对输入文本做预处理，然后调用svm程序分类，并返回分类结果
	 * @param sInput
	 * @return
	 */
	public static String svm_classify(String sInput) {
		System.out.println("PU start...");
		NlpirTest nlpir = new NlpirTest(); // 初始化张华平分词工具类对象

		String[] sents = sInput.trim().split("[。 ！ ？]"); // 对输入文本按中文标点符号句号、感汉号、问号分句。
		for(int i = 0; i < sents.length; i++) {
			sents[i] = sents[i].trim();
			if(!sents[i].equals("")) {
				sents[i] = nlpir.multProcess(sents[i]); // 对每个句子用张华平分词工具进行分词、去掉词性标注、去停用词。
			}
		}
		nlpir.exit(); // 销毁张华平分词工具类对象
		System.out.println("NlpirTest finished..."); // 服务器端输出语句，提示完成分词、去掉词性标注、去停用词过程。
		sents = Word2PUVec.process(sents); // 将文本表示成词袋模型向量。
		try {
			String resultFilePath = RESULT_DIR_PATH + "/result.txt";
			String svm_classify_command = (String) (MyPath.SYSTEM_NAME.contains("Windows")?"":"wine ") + 
					EXE_PATH + " " + SOURCE_FILE_PATH + " " + MODEL_FILE_PATH + " " + resultFilePath; // 针对运行操作系统的不同，自适应设置调用svm命令语句。
			File source_file = new File(SOURCE_FILE_PATH);
			while(!source_file.exists()) {
				System.out.println("source file does not exist...");
				Thread.sleep(1000);
			}
			File resultFile = new File(resultFilePath);
			if(resultFile.exists()) {
				resultFile.delete();
			}
			Runtime mexecutor = Runtime.getRuntime();
			mexecutor.exec(svm_classify_command); // 调用svm_classify.exe程序对输入文本进行分类。
			resultFile = new File(resultFilePath);
			int countTime = 0;
			while(!resultFile.exists() && countTime < 30) {
				System.out.println("result file does not exist..." + countTime++);
				Thread.sleep(1000);
			}
			if(resultFile.exists()) {
				String classifyResult = count(resultFile, sents); // 根据svm_classify.exe处理结果，整理返回信息。
				return classifyResult;
			}
			else {
				System.out.println("PU svm failed..."); // svm_classify.exe运行结果不存在，服务器端输入提示分类失败。
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
