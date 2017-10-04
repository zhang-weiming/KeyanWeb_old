package myservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myjavabean.model.User;
import myjavabean.util.DBHelper;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/signupservlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private DBHelper dbHelper;
	private User user;
	
	private String uname;
	private String uemailaddress;
	private String upassword;
	private String uorganization;
	private String ucontactway;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUpServlet() {
        super();
        dbHelper = new DBHelper();
        user = new User();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		System.out.println("接收到请求");
		
//		String uemailaddress = request.getParameter("uemailaddress");
		
		uname = request.getParameter("uname");
		uemailaddress = request.getParameter("uemailaddress");
		upassword = request.getParameter("upassword");
		uorganization = request.getParameter("uorganization");
		ucontactway = request.getParameter("ucontactway");
		
		if (!uname.equals("")) user.setUname(uname);
		user.setUemailaddress( uemailaddress );
		user.setUpassword( upassword );
		user.setUorganization( uorganization );
		user.setUcontactway( ucontactway );

		
		if (uemailaddress != null) {
			if (dbHelper.containsUemailaddress(uemailaddress)) { // 该邮箱已存在，注册失败
				System.out.println("该邮箱已存在，注册失败");
				out.println("failed_emailaddress_have_signed_up");
				return;
			}
			// 该邮箱可注册
			else {
				dbHelper.insert(user);
				out.print("success");
				return;
			}
		}
		else {
			out.println("failed_post_error");
		}
		
		
//		int i = dbHelper.insert(user);
//		System.out.println("[SignUp]: " + i);
//		if (0 == i) {
//			out.println("failed");
//		}
//		else {
//			out.println("success");
//		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
