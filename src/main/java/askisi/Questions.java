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


@WebServlet("/Questions")
public class Questions extends HttpServlet {
	String driver = "com.mysql.jdbc.Driver";
	String dbURL = "jdbc:mysql://127.5.86.130:3306/askisidb?useUnicode=true&characterEncoding=UTF-8&user=askisi&password=@sk1s1!"; // ?user=blah&password=blah"

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String qry = "select q.qid as qid, question, s.selid as selid, selection_text, s.correct " +
				"from questions q, selections s where q.qid = s.qid order by qid, selid";
		String[] columns = new String[] { 
				"qid", 
				"question",
				"selid",
				"selection_text",
				"correct"
				};
		String[] columnsVisible = new String [] {
				"ΚΩΔΙΚΟΣ",
				"ΕΡΩΤΗΣΗ",
				"ΚΩΔΙΚΟΣ ΕΠΙΛΟΓΗΣ",
				"ΕΠΙΛΟΓΗ",
				"ΣΩΣΤΗ ΕΠΙΛΟΓΗ"
		};
		
		Connection dbCon;

		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		PrintWriter out = res.getWriter();
		
		try {

			Class.forName(driver);
			dbCon = DriverManager.getConnection(dbURL);
			ResultSet rs;
			Statement stmt;
			stmt = dbCon.createStatement();
			rs = stmt.executeQuery(qry);
			
			out.println("<!DOCTYPE html><html><body>");
			
			printAnyError(out, req);
			
			//Printing the table
			out.println("<hr/>");
			out.println("<table border=1><tr>");
			for (int i = 0; i < columns.length; i++) {
				out.print("<td><b>");
				out.print(columnsVisible[i].toUpperCase());
				out.print("</b></td>");
			}

			while (rs.next()) {
				out.println("<tr>");
				for (int i = 0; i < columns.length; i++) {
					out.println("<td>");
					out.println(rs.getString(columns[i]));					
				}
				
			}
			out.println("</table></body></html>");
			
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

		String qry1 = "insert into questions (qid, question) values (? ,?)";
		String qry2 = "insert into selections (qid,selid,selection_text,correct) values (?,?,?,?)";

		Connection dbCon;

		req.setCharacterEncoding("utf-8");
		
		String[] sel = new String[3];
		
		String qid = req.getParameter("qid");
		String question = req.getParameter("question");
		sel[0] = req.getParameter("sel1");
		sel[1] = req.getParameter("sel2");
		sel[2] = req.getParameter("sel3");
		String correct = req.getParameter("correct");
		

		try {

			Class.forName(driver);
			dbCon = DriverManager.getConnection(dbURL);

			PreparedStatement stmt;
			stmt = dbCon.prepareStatement(qry1);
			stmt.setString(1, qid);
			stmt.setString(2, question);

			int i = stmt.executeUpdate();
  			
			stmt = dbCon.prepareStatement(qry2);
			for (int n=i;n<=3;n++) {			
			
               stmt.setString (1, qid);
               stmt.setInt(2, n);
               stmt.setString(3,sel[n-1]);
               stmt.setString(4,n==Integer.parseInt(correct)?"1":"0");
			   i = stmt.executeUpdate();
			}
			
			

			res.sendRedirect("Questions");

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
}