package myservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myjavabean.model.MySessionContext;
import myjavabean.model.Report;
import myjavabean.path.PublicVariable;

@WebServlet("/androidecharts")
public class AndroidEchartsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private String sents;
	private String resultFromPU;
	private String resultFromTransE;
	private String pos_position;
	private String neg_position;
	private String postReason;
	private String posSents;
	private String negSents;
	private String myId;
	private ArrayList<String> transEResult;
	private String[] pos_sents;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AndroidEchartsServlet() {
        super();
        sents = null;
        resultFromPU = null;
        resultFromTransE = null;
        pos_position = null;
        neg_position = null;
        postReason = null;
        posSents = null;
        negSents = null;
        myId = null;
        transEResult = null;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			postReason = request.getParameter("postReason").trim();
			myId = request.getParameter("myId").trim();
			HttpSession session = MySessionContext.getSession(myId);
			if (session == null) {
				session = request.getSession(false);
			}
			
			if (session == null) { // 不响应
				System.out.println("[AndroidEchartsServlet-" + postReason + "]找不到session");
				out.println("null");
			}
			else {
				System.out.println("[AndroidEchartsServlet-" + postReason + "]找到session: " + session.getId());
				sents = (String) session.getAttribute("sents");
				resultFromPU = ((String) session.getAttribute("resultFromPU")).trim();
				resultFromTransE = ((String) session.getAttribute("resultFromTransE")).trim();
//				sents = PublicVariable.sents;
//				resultFromPU = PublicVariable.resultFromPU;
//				resultFromTransE = PublicVariable.resultFromTransE;

				String[] parts = resultFromPU.split("\\|");
//				System.out.println(parts.length);
				if (!resultFromPU.equals("null")) {
					pos_position = parts[1].trim();
					neg_position = parts[2].trim();
				}
				String[] sentsArr = sents.split("[。！？]");
				switch (postReason) {
					case "showPie":
						out.println(resultFromPU.trim());
						break;
					case "showForce":
						out.println(resultFromTransE.trim());
						break;
					case "getPosition":
						out.println(pos_position);
						break;
					case "getPos":
						posSents = "";
						if (pos_position.equals("null")) {
							pos_position = "无";
						}
						else {
							String[] pos_positions = pos_position.split(" ");
							for (String p : pos_positions) {
								posSents += sentsArr[ Integer.parseInt(p) ] + "。";
							}
						}
						out.println(posSents);
						break;
					case "getNeg":
						negSents = "";
						if (neg_position.equals("null")) {
							negSents = "无";
						}
						else {
							String[] neg_positions = neg_position.split(" ");
							for (String p : neg_positions) {
								negSents += sentsArr[ Integer.parseInt(p) ] + "。";
							}
						}
						out.println(negSents);
						break;
					case "getReport":
						transEResult = (ArrayList<String>) session.getAttribute("transEResult");
						pos_sents = (String[]) session.getAttribute("pos_sents");
						if (transEResult == null) {
							out.println("null");
						}
						else {
							out.println( new Report(transEResult, pos_sents).getReport() );
						}
						break;
					default:
						break;
					}
			}
		} catch (Exception e) {
			out.println("null");
			System.out.println("[AndroidEchartsServlet-" + postReason + " Error]: " + postReason);
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
