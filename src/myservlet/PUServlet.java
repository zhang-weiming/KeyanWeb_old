package myservlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myjavabean.model.MySessionContext;
import myjavabean.model.TextPosted;
import myjavabean.path.MyPath;
import myjavabean.path.PublicVariable;
import myjavabean.pu.PU;
import myjavabean.util.DBHelper;
@WebServlet("/puservlet")
public class PUServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private long time;
	private long currentTime;
	private long diff;
	private String sents;
	private String classifyResult;
	private String myId;
	private DBHelper dbHelper;
	private TextPosted textPosted;
	
    public PUServlet() {
        super();
        time = 0;
        currentTime = 0;
        diff = 0;
        myId = null;
        dbHelper = new DBHelper();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		try {
//			System.out.println(MyPath.WEB_INF_DIR_PATH);
			currentTime = new Date().getTime();
			diff = currentTime - time;
			if (diff > 5000) 
			{ // 两次请求间隔大于5s，允许请求。
				time = currentTime;
				sents = request.getParameter("sents"); // 获取输入文本
				myId = request.getParameter("myId"); // 获取sessionid
				
				dbHelper.init(); // 将用户发送的分析文本存入数据库
				textPosted = new TextPosted();
				textPosted.setText(sents);
				dbHelper.insert(textPosted);
				classifyResult = PU.svm_classify(sents); // 文本分句、去停用词并分类
//				PublicVariable.sents = sents;
//				PublicVariable.resultFromPU = classifyResult;
				HttpSession session = MySessionContext.getSession(myId);
				if (session == null) {
					session = request.getSession(true);
				}
				System.out.println("[PUServlet] SessionId: " + session.getId());
				
				session.setAttribute("sents", sents);
				session.setAttribute("resultFromPU", classifyResult);
				if(classifyResult != null) {
					out.print(classifyResult); // 返回处理结果。格式：正例个数 负例个数|[正例在句子数组中的索引值...，以一个空格间隔] （例如：5 3|0 2 3 5 7）
				}
				else 
				{
					out.print("null"); // 处理结果为空
				}
			}
			else 
			{ // 两次请求间隔小于等于5s，不允许请求。
				System.out.println("上次时间：" + time);
				System.out.println("当前时间：" + currentTime);
				out.println("null|请" + (diff / 1000) + "秒后再提交！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("null");
		}
		dbHelper.close();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
