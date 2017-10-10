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
 * Servlet implementation class SignOutServlet
 */
@WebServlet("/signout")
public class SignOutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignOutServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			System.out.println("已注销");
		}
		else {
			User user = (User) session.getAttribute("user");
			System.out.println("注销用户" + user.getUemailaddress());
			session.removeAttribute("user");
			out.println("success");
//			System.out.println("用户" + session.getAttribute("uemailaddress") + "请求注销");
//			if ((session = null) == null) {
//				System.out.println("注销成功！");
//				out.println("success");
//			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
