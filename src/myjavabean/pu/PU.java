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
	
	// public static int[] count(String resultFilePath) {
	public static String count(File resultFile) {
		try {
			if(resultFile.exists()) {
				BufferedReader bufr = new BufferedReader(new FileReader(resultFile));
				
				int posCounter = 0, negCounter = 0;
				String pos_positions = "";
				String str = null;
				int sentId = 0;
				while((str = bufr.readLine()) != null) {
					if(!str.equals("")) {
						if(str.charAt(0) == '-') {
							//neg sent
							negCounter++;
						}
						else {
							//pos sent
							posCounter++;
							pos_positions += sentId + " ";
						}
					}
					sentId++;
				}
				
				System.out.println("PU finished...");
				System.out.println("Pos: " + posCounter + ", Neg: " + negCounter);

				return posCounter + " " + negCounter + '|' + pos_positions.trim();
			}
			else {
				System.out.println("PU failed: No result file...");
				return null;
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static String svm_classify(String sInput) {
		System.out.println("PU start...");
		NlpirTest nlpir = new NlpirTest();

		String[] sents = sInput.trim().split("[。 ！ ？]"); //split the input paragraph into several sents
//		String tempStr = "[PU]\n";
//		for(String str : sents) {
//			tempStr += str + "\n";
//		}
//		System.out.println(tempStr);
		for(int i = 0; i < sents.length; i++) {
			if(!sents[i].equals("")) {
				sents[i] = nlpir.multProcess(sents[i]);
			}
		}
		nlpir.exit();
		System.out.println("NlpirTest finished...");

		sents = Word2PUVec.process(sents);
		
		try {
			String resultFilePath = RESULT_DIR_PATH + "/result.txt";
//			String resultFilePath = RESULT_DIR_PATH + "/" + System.currentTimeMillis() / 1000 + ".txt";
			// String command = exePath + " " + sourcePath + " " + modelPath + " " + resultFilePath;
			String svm_classify_command = (String) (MyPath.SYSTEM_NAME.contains("Windows")?"":"wine ") + 
					EXE_PATH + " " + SOURCE_FILE_PATH + " " + MODEL_FILE_PATH + " " + resultFilePath;
			
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
			mexecutor.exec(svm_classify_command);

			resultFile = new File(resultFilePath);
			int countTime = 0;
			while(!resultFile.exists() && countTime < 30) {
				System.out.println("result file does not exist..." + countTime++);
				Thread.sleep(1000);
			}
			if(resultFile.exists()) {
				String classifyResult = count(resultFile);
				return classifyResult;
			}
			else {
				System.out.println("PU failed...");
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
