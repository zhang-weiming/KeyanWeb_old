package myservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myjavabean.model.User;

/**
 * Servlet implementation class ConfirmSessionServlet
 */
@WebServlet("/confirmsession")
public class ConfirmSessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmSessionServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		try {
			HttpSession session = request.getSession(false);
			
			if (session == null || session.getAttribute("user") == null) { // 该会话没有用户登录
				out.println("failed_null");
			}
			else {
				out.println("success|" + ((User) session.getAttribute("user")).getUemailaddress());
			}
		} catch (Exception e) {
			System.out.println("捕获到异常");
			e.printStackTrace();
			System.out.println("捕获到异常");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
