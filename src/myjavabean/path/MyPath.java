package myjavabean.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import myjavabean.nlp.NlpirTest;

public class MyPath {
	public static final String CLASS_DIR_PATH = MyPath.class.getResource("/").getPath().toString();
	public static final String WEB_INF_DIR_PATH = CLASS_DIR_PATH.substring(0, CLASS_DIR_PATH.indexOf("/classes"));
	public static final String NLPIR_ROOT_PATH = WEB_INF_DIR_PATH + "/data/Util/NLPIR";
	public static final String STOP_WORD_FILE_PATH = WEB_INF_DIR_PATH + "/data/Util/word_dict/stop_word_UTF_8.txt";
	public static final String FEATURE_SET_FILE_PATH = WEB_INF_DIR_PATH + "/data/pu/model/feature_set.txt";
	
	public static void main(String[] args) {
//		try {
//			BufferedReader bufr = new BufferedReader(new FileReader(FEATURE_SET_FILE_PATH));
//			String str = null;
//			int a = 1;
//			while((str = bufr.readLine()) != null) {
//				System.out.println(a++ + "\t" + str);
//			}
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
		NlpirTest nlpir = new NlpirTest();
		nlpir.addUserWord("我爱你", "myflag");
		String sInput = "我爱你中国";
		System.out.println(nlpir.multProcess(sInput));
		nlpir.exit();
	}
}
