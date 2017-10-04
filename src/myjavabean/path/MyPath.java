/**
 * MyPath.java
 * 全局共享的文件路径信息。
 */
package myjavabean.path;
import java.io.BufferedReader;
public class MyPath {
	public static final String SYSTEM_NAME = System.getProperty("os.name");
	public static final String CLASS_DIR_PATH = MyPath.class.getResource("/").getPath().toString();
	public static final String WEB_INF_DIR_PATH = CLASS_DIR_PATH.substring(
			(int) (SYSTEM_NAME.contains("Windows")?1:0), CLASS_DIR_PATH.indexOf("/classes")); // 根据操作系统，自适应获得WEB-INF文件夹的绝对路径。
	public static final String NLPIR_ROOT_PATH = WEB_INF_DIR_PATH + "/data/Util/NLPIR"; // 张华平分词工具的根目录。
	public static final String STOP_WORD_FILE_PATH = WEB_INF_DIR_PATH + "/data/Util/word_dict/stop_word_UTF_8.txt"; // 停用词表文件路径。
	public static final String FEATURE_SET_FILE_PATH = WEB_INF_DIR_PATH + "/data/pu/model/feature_set.txt"; // PU_Learning的词库文件路径。
}
