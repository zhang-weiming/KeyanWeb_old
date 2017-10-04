/**
 * TransEServlet.java
 */
package myservlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myjavabean.transe.TransE;
/**
 * Servlet implementation class TransEServlet
 */
@WebServlet("/transeservlet")
public class TransEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TransEServlet() {
        super();
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8"); // 设置接收信息的字符串编码方式
		response.setCharacterEncoding("utf-8"); // 设置返回信息的字符串编码方式
		PrintWriter out = response.getWriter(); // 获取返回信息的输出对象（句柄）
		String positionsPara = request.getParameter("positions"); // 获取参数。所有正例句子的索引值。
		String sents = request.getParameter("sents"); // 获取参数。用户输入的文本。
		String[] positions = positionsPara.trim().split(" "); // 获取参数
		if(positions[0].equals("")) {
			out.print(new String("ERROR|no data!")); // 获取的正例句子的索引值为空字符串，返回没有信息。
		}
		else {
			TransE transETool = new TransE(); // 初始化TransE算法模型工具类对象
			int[] positionsInt = new int[positions.length];
			for(int i = 0; i < positions.length; i++) {
				positionsInt[i] = Integer.parseInt(positions[i]); // 将接收的索引值从String类型（字符串）转化为int类型（整数）
			}
			ArrayList<String> transEResult = transETool.process(positionsInt, sents); // 调用TransE算法模型对输入文本做细粒度分析。
			if(transEResult == null) {
				out.print(new String("ERROR|no data!")); // TransE处理结果为空字符串，返回没有信息。
			}
			else {
				String returnData = transEResult.get(0); // 整理返回信息文本。
				for(int i = 1; i < transEResult.size(); i++) {
					returnData += "|" + transEResult.get(i);
				}
				out.print(returnData); // 返回处理结果。
			}
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
