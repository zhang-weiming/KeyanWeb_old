package myservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myjavabean.util.DBHelper;

/**
 * Servlet implementation class CheckUpdateServlet
 */
@WebServlet("/checkupdateservlet")
public class CheckUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private String version;
	private String latestVersion;
	
	private DBHelper dbHelper;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckUpdateServlet() {
        super();
        
        dbHelper = new DBHelper();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		version = request.getParameter("version");
		
		if (version == null || version.equals("")) {
			System.out.println("获取软件版本号失败");
			out.println("failed_version_null");
		}
		else {
			latestVersion = dbHelper.getAppLatestVersion();
			System.out.println("最新版本号：" + latestVersion);
			if (latestVersion.equals("")) {
				System.out.println("获取最新版本号失败");
				out.println("failed_mysql_error");
				return;
			}
			else {
				if (latestVersion.equals(version)) {
					System.out.println("已是最新版");
					out.println("true");
					return;
				}
				else {
					System.out.println("不是最新版");
					out.println(latestVersion);
					return;
				}
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
