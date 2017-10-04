package myservlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myjavabean.path.MyPath;
import myjavabean.pu.PU;

/**
 * Servlet implementation class PUServlet
 */
@WebServlet("/puservlet")
public class PUServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PUServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		String sents = request.getParameter("sents"); // 获取输入文本
		String classifyResult = PU.svm_classify(sents); // 文本分句、去停用词并分类
		if(classifyResult != null) {
			out.print(classifyResult); // 返回处理结果。格式：正例个数 负例个数|[正例在句子数组中的索引值...，以一个空格间隔] （例如：5 3|0 2 3 5 7）
		}
		else {
			out.print("null"); // 处理结果为空
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
