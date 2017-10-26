package myservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myjavabean.model.FeedBack;
import myjavabean.util.DBHelper;

/**
 * Servlet implementation class FeedBackServlet
 */
@WebServlet("/feedbackservlet")
public class FeedBackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private FeedBack feedback;
	private String uemailaddress;
	private String feedinfo;
	private String inputtext;
//	private String fbdatetime;
	
	private DBHelper dbHelper;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeedBackServlet() {
        super();
        
        feedback = new FeedBack();
        uemailaddress = "";
        feedinfo = "";
        inputtext = "";
//        fbdatetime = "";
        
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
		
		uemailaddress = request.getParameter("uemailaddress");
		feedinfo = request.getParameter("feedinfo");
		inputtext = request.getParameter("inputtext");
		if (inputtext == null) inputtext = "";
//		fbdatetime = new SimpleDateFormat().format(new Date());
		
		if (uemailaddress.equals("") || uemailaddress == null) { // 邮箱为空，则不允许提交反馈
			out.println("failed_emailaddress_is_null");
		}
		else {
			System.out.println("[反馈]:邮箱:" + uemailaddress + "\n\t正在提交反馈:" + feedinfo + "\n\t携带输入文本:" + inputtext);
			dbHelper.init();
			if ( dbHelper.containsUemailaddress(uemailaddress) ) { // 该邮箱已注册，可以提交反馈
				if (feedinfo.equals("") || feedinfo == null) { // 反馈信息为空，不予以提交
					System.out.println("\t提交失败：反馈信息为空，不予以提交");
					out.println("failed_feedinfo_is_null");
				}
				else {
					feedback.setUemailaddress(uemailaddress);
					feedback.setFeedInfo(feedinfo);
					feedback.setInputtext(inputtext);
//					feedback.setFbdatetime(fbdatetime);
					if ( 0 == dbHelper.insert(feedback) ) { // 插入出错
						System.out.println("\t提交失败：mysql插入出错");
						out.println("failed_mysql_error");
					}
					else { // 插入成功
						System.out.println("提交反馈成功");
						out.println("success");
					}
					
				} //else
			} // if
			else { // 该邮箱未被注册，不予以提交
				System.out.println("\t提交失败：该邮箱未被注册");
				out.println("failed_emailaddress_is_not_signed_up");
			}
		} // else
		dbHelper.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
