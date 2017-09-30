package myjavabean.nlp;

import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;

import com.sun.jna.Library;
import com.sun.jna.Native;

import myjavabean.path.MyPath;

public class NlpirTest {
	private static final String NLPIR_ROOT_PATH = MyPath.NLPIR_ROOT_PATH;
	private final String STOP_WORD_FILE_PATH = MyPath.STOP_WORD_FILE_PATH;
	// public static final String system_charset = "GBK";
	private final String system_charset = "UTF-8";
	
	private ArrayList<String> stopWordList;

	//com.sun.jna.Library
	public interface CLibrary extends Library {
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				NLPIR_ROOT_PATH + "/lib" + (String) (MyPath.SYSTEM_NAME.contains("Windows")?"/win64/NLPIR":"/linux64/libNLPIR.so"), 
				CLibrary.class);
		// CLibrary Instance = (CLibrary) Native.loadLibrary(
				// rootPath + "/lib/NLPIR", CLibrary.class);
		
		public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		public int NLPIR_AddUserWord(String sWord);
		public int NLPIR_DelUsrWord(String sWord);
		public String NLPIR_GetLastErrorMsg();
		public void NLPIR_Exit();
	}
	
	public NlpirTest() {
		String argu = NLPIR_ROOT_PATH;
		int charset_type = 1;
		
		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;

		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is " + nativeBytes);
			return;
		}

		try {
			// 读取停用词
			BufferedReader bufr = new BufferedReader(new FileReader(STOP_WORD_FILE_PATH));
			this.stopWordList = new ArrayList<String>();
			this.stopWordList.clear();
			String str = null;
			while((str = bufr.readLine()) != null) {
				this.stopWordList.add(str);
			}
			// 读取停用词
		} catch (Exception e) {
			System.out.println("[Error]加载停用词出错!");
			e.printStackTrace();
		}
//		System.out.println("NlpirTest对象创建");
	}
	
	public String transString(String aidString, String ori_encoding, String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addUserWord(String word, String ci_xing) {
		try {
			CLibrary.Instance.NLPIR_AddUserWord(word + " " + ci_xing);
		} catch (Exception e) {
			System.out.println("Error: cannot add user word");
			e.printStackTrace();
		}
	}
	
	public void delUserWord(String word) {
		try {
			CLibrary.Instance.NLPIR_DelUsrWord(word);
		} catch (Exception e) {
			System.out.println("Error: cannot delete user word ... " + word);
			e.printStackTrace();
		}
	}
	
	public String multProcess(String sInput) {
		try {
			// 分词
			String sResult = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
			// 分词
			// 调整分词
			String[] sInputArr = sResult.split(" ");
			for(int i = 0; i < sInputArr.length; i++) {
				if(sInputArr[i].contains("/")) {
					sInputArr[i] = sInputArr[i].substring(0, sInputArr[i].indexOf("/"));
					// 去停用词
					if(this.stopWordList.contains(sInputArr[i]))
						sInputArr[i] = null;
					// 去停用词
				}
			}
			// 调整分词
			sResult = "";
			for(int i = 0; i < sInputArr.length; i++) {
				if(sInputArr[i] != null) {
					sResult += sInputArr[i] + " ";
				}
			}
			return sResult.trim();
		} catch (Exception e) {
			// statue = 0;
			System.out.println("Error: failed to process this sentence ... " + sInput);
			e.printStackTrace();
			return null;
		}
	}
	
	public String segment(String sInput) {
		try {
			String nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
			
			return nativeBytes;
		} catch (Exception e) {
			System.out.println("Error: cannot segment ... " + sInput);
			e.printStackTrace();
			return null;
		}
	}
	
	public String modify(String sInput) {
		String[] sInputArr = sInput.split(" ");
		String sResult = "";
		for(int i = 0; i < sInputArr.length; i++) {
			if(sInputArr[i].contains("/")) {
				String str = sInputArr[i].substring(0, sInputArr[i].indexOf("/"));
				sResult = sResult + str + " ";
			}
		}
		sResult = sResult.trim();
		return sResult;
	}
	
	public String removeTYC(String sInput) {
		String[] sResultArr = sInput.split(" ");

		// 去停用词
		for(int i = 0; i < sResultArr.length; i++) {
			if(this.stopWordList.contains(sResultArr[i]))
				sResultArr[i] = null;
		}
		// 去停用词

		String sResult = "";
		for(int i = 0; i < sResultArr.length; i++) {
			if(sResultArr[i] != null) {
				sResult += sResultArr[i] + " ";
			}
		}
		return sResult.trim();
	}
	
	public void exit() {
		CLibrary.Instance.NLPIR_Exit();
	}

}
