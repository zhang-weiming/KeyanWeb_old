package myservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myjavabean.util.DBHelper;

/**
 * Servlet implementation class SignInServlet
 */
@WebServlet("/signinservlet")
public class SignInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String uemailaddress;
	private String upassword;
	
	private String postreason;
	private DBHelper dbHelper;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignInServlet() {
        super();

        uemailaddress = "";
        upassword = "";
        dbHelper = new DBHelper();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		System.out.println("登录请求接收");
		
		postreason = request.getParameter("postreason");
		uemailaddress = request.getParameter("uemailaddress");
		upassword = request.getParameter("upassword");
		
		System.out.println("[SignIn]: uemailaddress: " + uemailaddress);
		
		if (uemailaddress != null) {
			if (dbHelper.containsUemailaddress(uemailaddress)) { // 邮箱存在
				try {
					String sql = "select uemailaddress,upassword from user "
							+ "where uemailaddress=\'" + uemailaddress + "\';";
					ResultSet rs = dbHelper.selectSql(sql);
					if (rs.next()) {
						// 密码匹配，允许登录
						if ( upassword.equals(rs.getString("upassword")) ) {
							System.out.println("[SignIn]: 登录成功");
//							if (postreason != null) { // 网站系统--为用户建立session标识
							HttpSession session = request.getSession(true);
							session.setAttribute("uemailaddress", uemailaddress);
							System.out.println("为用户" + uemailaddress + "建立session，id：" + session.getId());
//								if (session != null) {
//									System.out.println("为用户" + uemailaddress + "建立session，id：" + session.getId());
//									out.println("success|" + session.getId());
//								}
//								else {
//									System.out.println("创建session失败！");
////									out.println("failed_session_cannot_built");
//								}
//							}
							
							out.println("success");
							return;
						}
						// 密码不匹配，不允许登录
						else {
							System.out.println("[SignIn]: 登录失败 -- 密码不匹配");
							out.println("failed_password_false");
							return;
						}
					}
					else {
						// 该情况应该是数据库中没有该邮箱，但是从实际来看，该情况不会发生
						System.out.println("[SignIn]: 登录失败 -- 未知错误");
						out.println("failed_null");
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					// 未知错误，无法判断，不允许登录
					System.out.println("[SignIn]: 登录失败 -- 未知错误");
					out.println("failed_unknown_error");
					return;
				}
				
			}
			// 该邮箱不存在，不允许登录
			else {
				System.out.println("[SignIn]: 登录失败 -- 该邮箱地址不存在");
				out.println("failed_emailaddress_not_found");
			}
		} // if
		else {
			out.println("failed_post_error");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
