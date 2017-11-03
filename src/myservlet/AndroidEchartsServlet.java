package myservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myjavabean.path.PublicVariable;

@WebServlet("/androidecharts")
public class AndroidEchartsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private String sents;
	private String resultFromPU;
	private String resultFromTransE;
	private String postReason;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AndroidEchartsServlet() {
        super();
        sents = null;
        resultFromPU = null;
        resultFromTransE = null;
        postReason = null;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			System.out.println("pie.html发送请求");
			postReason = request.getParameter("postReason").trim();
			sents = PublicVariable.sents;
			resultFromPU = PublicVariable.resultFromPU;
			resultFromTransE = PublicVariable.resultFromTransE;
			switch (postReason) {
			case "showPie":
				out.println(resultFromPU.trim());
				break;
			case "showForce":
				out.println(resultFromTransE.trim());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
