package askisi;


//File: Students.java
//Last Updated: 9-Jan-2013

//Import Servlet Libraries
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
//Import Java Libraries
import java.io.*;
import java.sql.*;


@WebServlet("/nextquestion2")
public class NextQuestion2 extends HttpServlet {
	String driver = "com.mysql.jdbc.Driver";
	String dbURL = "jdbc:mysql://localhost:3306/askisidb?user=ted&password=ted!"; // ?user=blah&password=blah"

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String qry = "select q.qid as qid, question, s.selid as selid, selection_text, s.correct " +
				"from questions q, selections s where q.qid = s.qid and q.qid = ?";
		
		
		Connection dbCon;

		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		PrintWriter out = res.getWriter();
		
		try {

			Class.forName(driver);
			dbCon = DriverManager.getConnection(dbURL);
			ResultSet rs;
			PreparedStatement stmt;
			stmt = dbCon.prepareStatement(qry);
			stmt.setString(1, getRandomQid());
			rs = stmt.executeQuery();
			
			out.println("<!DOCTYPE html><html><body>");
			
			printAnyError(out, req);
			
			out.println("<hr/>");
			out.println("<h1>Ερώτηση</h1>");
			out.println("<form action=\"nextquestion\" method=\"POST\">");
            boolean firstTime = true;
            
			while (rs.next()) {
			  if (firstTime) {
				out.println("<input type=\"hidden\" name=\"qid\" value=\"" 
			    + rs.getString("qid") +"\">");
			    out.println(rs.getString("question"));
				out.println("<br><br>");
				firstTime = false;
			  }
			  out.println("<input type=\"radio\" name=\"answer\" value=\""+rs.getString("selid")+"\">");
			  out.println("  "+rs.getString("selection_text")+"<br>");    
			}
			
			out.println("<br><input type=\"submit\" name=\"submit\" value=\"Απάντηση\">");
			out.println("</form>");
			out.println("</body></html>");
			
			rs.close();
			stmt.close();
			dbCon.close();
			
		} catch (Exception e) {
			out.println(e.toString());
		} finally {
			out.close();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String qry = "select selid, selection_text from selections " +
				"where qid = ? and correct = 1"; 

		Connection dbCon;

		//req.setCharacterEncoding("utf-8");
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		PrintWriter out = res.getWriter();

		String[] sel = new String[3];
		
		String qid = req.getParameter("qid");
		String answer = req.getParameter("answer");
        ResultSet rs;
        
		try {

			Class.forName(driver);
			dbCon = DriverManager.getConnection(dbURL);

			PreparedStatement stmt;
			stmt = dbCon.prepareStatement(qry);
			stmt.setString(1, qid);
			
			rs = stmt.executeQuery();
  			
			if (rs.next()){
				
			  String selid = rs.getString("selid");
			  if (selid.equals(answer)) {
				  out.println("<strong> Σωστά </strong>");				  
			  } else {
				  out.println("<strong> Λάθος </strong>");
				  out.println("<br><br> Η Σωστή Απάντηση είναι: <br><br>");
				  out.println(rs.getString("selection_text"));
			  }
			  out.println("<br><br><a href=\"nextquestion\">Επόμενη Ερώτηση</a><br><br>");
			}
			
		} catch (Exception e) {
			res.sendRedirect("Questions?errormsg=" + e.getMessage());
		}
	}

	

	void printAnyError(PrintWriter out, HttpServletRequest req) {
		String errorMessage = req.getParameter("errormsg");
		if (errorMessage != null) {
			out.println("<br><strong style=\"color:red\"> Error: "+ errorMessage + "</strong>");
		}
	}
	
	private String getRandomQid() {
	  return "q1";
	  
	}
	
}