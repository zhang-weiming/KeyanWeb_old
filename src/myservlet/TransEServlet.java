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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		String positionsPara = request.getParameter("positions");
		String sents = request.getParameter("sents");
		String[] positions = positionsPara.trim().split(" ");
		if(positions[0].equals("")) {
			out.print(new String("ERROR|no data!"));
		}
		else {
			TransE transETool = new TransE();
			int[] positionsInt = new int[positions.length];
			for(int i = 0; i < positions.length; i++) positionsInt[i] = Integer.parseInt(positions[i]);
			ArrayList<String> transEResult = transETool.process(positionsInt, sents);
			if(transEResult == null) {
				out.print(new String("ERROR|no data!"));
			}
			else {
				String returnData = transEResult.get(0);
				for(int i = 1; i < transEResult.size(); i++) {
					returnData += "|" + transEResult.get(i);
				}
				out.print(returnData);
			}
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
