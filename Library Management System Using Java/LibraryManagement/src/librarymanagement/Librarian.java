package librarymanagement;

import java.util.*;
import java.util.Date;
import java.sql.*;


/**
 * 
 * This class possesses all the methods required to handle 
 * the different actions that can be taken by a librarian
 *
 */
public class Librarian {

	String librarianName = "Krishan Gandhi";
	String username = "krish";
	String password = "123";
	
	
//////////////////  			registerStudent() method starts       /////////////////////////////////
	
	/**
	 * This method adds a student record in the database.
	 */
	public void registerStudent() {
		
		Integer studentId;
		String studentName;
		String studentMobile;

		System.out.println("Enter ID : ");
		Scanner intScanner = new Scanner(System.in);
		studentId = intScanner.nextInt();

		System.out.println("Enter Name : ");
		Scanner stringScanner = new Scanner(System.in);
		studentName = stringScanner.nextLine();

		System.out.println("Enter Contact Number : ");
		studentMobile = stringScanner.next();

		intScanner.close();
		stringScanner.close();
		
		try {
			Connection con = MainClass.getConnection();
			String sql = "insert into studentsrecord (studentid,studentName,studentmobile) VALUES ('" + studentId
					+ "','" + studentName + "','" + studentMobile + "')";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Student Record Added Successfully...");
			// ResultSet rs = stmt.executeQuery("select * from studentsrecord"); // execute
																				// queries
			// while (rs.next())
			//	System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getInt(3));
			stmt.close();
			con.close();
		} catch (Exception ex) {
			System.out.println("Error :  " + ex.getMessage());
		} // End of try-catch
	} 
	
	////////////////////         End of registerStudent() method       ////////////////////////////////////

	
	////////////////////         issueBooks() method starts       ////////////////////////////////////

	/**
	 * 
	 * This method validates the student Id and Name who received the 
	 * book and accordingly proceed for the process of book issue.
	 * 
	 */
	
	public void issueBook()
	{
		Integer studentId, bookId;
		String studentName;

		Scanner intScanner = new Scanner(System.in);
		Scanner stringScanner = new Scanner(System.in);
		
		System.out.print("Enter Student ID :  ");
		studentId = intScanner.nextInt();
		System.out.print("Enter Student Name :  ");
		studentName = stringScanner.nextLine();
		System.out.print("Enter Book ID :  ");
		bookId = intScanner.nextInt();

		if (studentValidation(studentId,studentName) == 1) 
		{
			proceedBookIssue(studentId,bookId,studentName);
		} 
		else
		{
			System.out.println("Invalid Student Id/Name !!!");
		}
					
	}

	////////////////////      End of issueBook() method starts       ////////////////////////////////////
	

	////////////////////      studentValidation() method starts       ////////////////////////////////////
	
	/**
	 * 
	 * This method is invoked if the student Id and Name matches with the 
	 * Id and Name of the student who actually received the issued book.
	 * 
	 */
	public int studentValidation(Integer studentId,String studentName) {
		
		Integer valid = 0;
		String temp = "NULL";
		try {
			Connection con = MainClass.getConnection(); 
			String sql = "Select  studentid, studentname FROM studentsrecord where studentid=?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setInt(1,studentId);
			ResultSet rs = stmt.executeQuery(); // execute
			rs.next();
			temp = rs.getString("studentname");
			if (studentName.equalsIgnoreCase(temp))
				valid = 1;
			con.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return valid;
	}

	////////////////////      End of studentValidation() method       ////////////////////////////////////
	

	///////////////////       proceedBookIssue() method starts        ///////////////////////////////////

	/**
	 * 
	 * This method is invoked if the student Id and Name matches with the 
	 * Id and Name of the student who actually received the issued book
	 * and the process of book issuing proceeds.
	 * 
	 */	
	public void proceedBookIssue(Integer studentId,Integer bookId,String studentName) {
		
		String date,bookName;
		int status = 0;
		try {
			Connection conn = MainClass.getConnection();
			String sql = "SELECT bookname, availablecopy FROM booksrecord where bookid=?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, bookId);
			ResultSet result = statement.executeQuery();
			int quantity = 0;
			result.next();
			quantity = result.getInt("availablecopy");
			bookName = result.getString("bookname");
			if (quantity > 0) {
				PreparedStatement statement2 = conn
						.prepareStatement("update booksrecord set availablecopy=? where bookid=?");
				statement2.setInt(1, quantity - 1);
				statement2.setInt(2, bookId);
				status = statement2.executeUpdate();
				if (status > 0) {
					System.out.println("Book Issued Successfully...");
					try (Connection conn2 = MainClass.getConnection()) {
						String sql2 = "insert into issuedbookrecord(bookid, studentid, bookname, studentname,dateofissue ) values(?,?,?,?,?)";
						PreparedStatement statement3 = conn2.prepareStatement(sql2);
						statement3.setInt(1, bookId);
						statement3.setInt(2, studentId);
						statement3.setString(3, bookName);
						statement3.setString(4, studentName);
						Date d = new Date();
						int y = d.getYear() + 1900;
						int m = d.getMonth() + 1;
						int da = d.getDay() + 16;
						date = " " + y + "-" + m + "-" + da + "";
						statement3.setString(5, date);
						statement3.executeUpdate();
						conn2.close();
						statement3.close();
					} catch (Exception e) {
						System.out.println(e);
					}
					statement2.close();
				}
			} else
				System.out.println("Sorry!!! Not enough copies remaining...");
			conn.close();
			statement.close();
		} catch (Exception ex) {
			System.out.println("Error :  " + ex.getMessage());
		}
	}

	///////////////////       End of proceedBookIssue() method         ///////////////////////////////////

	/////////////////       returnBook() method starts         //////////////////////////////////////////
	
	/**
	 * 
	 * This method is used to return a book to the library.
	 * 
	 */
	
	public void returnBook()
	{
		Integer studentId, bookId;
		String studentName;
		String bookName;
		Scanner intScanner = new Scanner(System.in);
		Scanner stringScanner = new Scanner(System.in);
		System.out.print("Enter Student Id :  ");
		studentId = intScanner.nextInt();
		System.out.print("Enter Student Name :  ");
		studentName = stringScanner.nextLine();
		System.out.print("Enter Book Id :  ");
		bookId = intScanner.nextInt();	
		if(studentValidation(studentId, studentName) == 1)
		{
			if(confirmReturn(studentId).equalsIgnoreCase("y"))
			{
				proceedBookReturn(bookId, studentId);
			}
			else
			{
				System.out.println("Invalid Student Id/Name !!!");
			}
		}
		else
		{
			System.out.println("Invalid Student Id/Name !!!");
		}
	}
	
	/////////////////      End of returnBook() method starts         ////////////////////////////////////
	
	/////////////////      confirmReturn() method starts            ////////////////////////////////////
	
	/**
	 * 
	 * This method checks whether the student Id matches with the Id
	 * of student who received the book.
	 *  
	 */
	
	public String confirmReturn(Integer studentId) 
	{
		Scanner sc = new Scanner(System.in);
		String ch = "n";
		try {
			Connection con = MainClass.getConnection();
			String sql = "Select studentid, bookid, bookname, studentname, dateofissue FROM issuedbookrecord where studentid=?";
			PreparedStatement s = con.prepareStatement(sql);
			s.setInt(1, studentId);
			ResultSet r = s.executeQuery(); // execute
			System.out.println("Details of Book issued at your Id  " + studentId + " are as follows :  ");
			int booksFound = 0;
			while (r.next()) {
				booksFound++;
				Integer bookId = r.getInt(1);
				Integer stId = r.getInt(2);
				String bookName = r.getString(3);
				String stName = r.getString(4);
				String date = r.getString(5);
				System.out.println("Book id= " + bookId + ", Student Id= " + stId + ", Book Name= " + bookName
						+ ", Student Name= " + stName + ", Date Of Issue= " + date);
				System.out.println("\nDo you want to return this book(y/n)");
				ch = sc.next();
			}
			if (booksFound == 0)
				System.out.println("Sorry, nothing to return. No books issued at ur id " + studentId);
		} catch (Exception e) {
			System.out.println(e);
		}
		return ch;
	}

	//////////////////////     End of confirmReturn() Method      //////////////////////////////////////

	//////////////////////     proceedBookReturn() Method starts     //////////////////////////////////////

	/**
	 * 
	 * This method is invoked to complete the final process of book
	 * return after all the details are verified.
	 * 
	 */
	
	public void proceedBookReturn(Integer bookId,Integer studentId) 
	{
		String date,bookName;
		int status = 0;
		try {
			Connection conn = MainClass.getConnection();
			String sql = "SELECT bookname, availablecopy FROM booksrecord where bookid=?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, bookId);
			ResultSet result = statement.executeQuery();
			int quantity = 0;
			result.next();
			quantity = result.getInt("availablecopy");
			bookName = result.getString("bookname");
			PreparedStatement statement2 = conn.prepareStatement("update booksrecord set availablecopy=? where bookid=?");
			statement2.setInt(1, quantity + 1);
			statement2.setInt(2, bookId);
			status = statement2.executeUpdate();
			if (status > 0) {
				System.out.println("Book Returned Successfully...");
				try (Connection conn2 = MainClass.getConnection()) {
					String sql2 = "delete from issuedbookrecord where studentid=?";
					PreparedStatement statement3 = conn2.prepareStatement(sql2);
					statement3.setInt(1, studentId);
					statement3.executeUpdate();
					conn2.close();
					statement3.close();
				} catch (Exception e) {
					System.out.println(e);
				}
				statement2.close();
			}
			conn.close();
			statement.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	//////////////////////     End of proceedBookReturn() Method      //////////////////////////////////////

	////////////////////      addNewBook() Method starts             //////////////////////////////////////
	
	/**
	 * This method is invoked to add a book into the library records.
	 */
	
	public void addNewBook() 
	{
		Integer bookId;
		String bookName;
		String authorName;
		String bookCategory;
		Integer availableCopies;
		Scanner stringScanner = new Scanner(System.in);
		Scanner intScanner = new Scanner(System.in);
		System.out.print("Enter Book ID :  ");
		bookId = intScanner.nextInt();
		System.out.print("Enter Book Name :  ");
		bookName = stringScanner.nextLine();
		System.out.print("Enter Author Name :  ");
		authorName = stringScanner.nextLine();
		System.out.print("Enter Book Category :  ");
		bookCategory = stringScanner.nextLine();
		System.out.print("Enter Number of Copies To Be Added :  ");
		availableCopies = intScanner.nextInt();
		try {
			Connection con = MainClass.getConnection();
			String sql = "insert into booksrecord (bookid, bookname, authorname, bookcategory, availablecopy) VALUES (?,?,?,?,?)";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setInt(1, bookId);
			stmt.setString(2, bookName);
			stmt.setString(3, authorName);
			stmt.setString(4, bookCategory);
			stmt.setInt(5, availableCopies);
			stmt.executeUpdate();
			System.out.println("\nBook Added Successfully...\n\n");
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	////////////////////      End of addNewBook() Method            //////////////////////////////////////

	//////////////////		searchByTitle() Method Starts           //////////////////////////////////////
	
	/**
	 * This method searches a book by its title
	 */
	
	public void searchByTitle() 
	{
		System.out.println("Enter the Title of Book");
		Scanner stringScanner = new Scanner(System.in);
		String title = stringScanner.nextLine();
		try {
			Connection con = MainClass.getConnection();
			String sql = "Select bookid, bookname, authorname, bookcategory, availablecopy FROM booksrecord where bookname=?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, title);
			ResultSet rs = stmt.executeQuery(); // execute
			System.out.println("Books with title name as \"" + title + "\" are as follows :  ");
			int booksFound = 0;
			while (rs.next()) {
				booksFound++;
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String authorn = rs.getString(3);
				String category = rs.getString(4);
				int quantity = rs.getInt(5);
				System.out.println("Book id= " + id + ", Title= " + name + ", Category= " + category + ", Author= "
						+ authorn + ", Available Copies= " + quantity);
			}
			if (booksFound == 0)
				System.out.println("No such books Found !!!");
			con.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	//////////////////		End of searchByTitle() Method            //////////////////////////////////////

	//////////////////		searchByAuthor() Method starts           //////////////////////////////////////	
	
	/**
	 * This method searches a book by its author
	 */
	
	public void searchByAuthor() {
		System.out.print("Enter the Author of Book :  ");
		Scanner stringScanner = new Scanner(System.in);
		String author = stringScanner.nextLine();
		try {
			Connection con = MainClass.getConnection();
			String sql = "Select bookid, bookname, authorname, bookcategory, availablecopy FROM booksrecord where authorname=?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, author);
			ResultSet rs = stmt.executeQuery(); // execute
			System.out.println("Books with author name as " + author + " are as follows :  ");
			int booksFound = 0;
			while (rs.next()) {
				booksFound++;
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String authorn = rs.getString(3);
				String category = rs.getString(4);
				int quantity = rs.getInt(5);
				System.out.println("Book id= " + id + ", Title= " + name + ", Category= " + category + ", Author= "
						+ authorn + ", Available Copies= " + quantity);
			}
			if (booksFound == 0)
				System.out.println("No such books Found !!!");
			con.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	////////////////     End of searchByAuthor() Method            //////////////////////////////////////

	////////////////     viewBook() Method starts                 //////////////////////////////////////	
	
	/**
	 * This method views a book.
	 */
	
	public void viewBook() 
	{
		try {
			Connection con = MainClass.getConnection();
			String sql = "Select bookid, bookname, authorname, bookcategory, availablecopy FROM booksrecord";
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(); // execute
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String authorn = rs.getString(3);
				String category = rs.getString(4);
				int quantity = rs.getInt(5);
				System.out.println("Book id= " + id + ", Title= " + name + ", Category= " + category + ", Author= "
						+ authorn + ", Available Copies= " + quantity);
			}
			con.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	////////////////   			End of viewBook() Method 		//////////////////////////////////////
	
	////////////////   		viewBook() Method starts	 		//////////////////////////////////////
	
	/**
	 * This method views a student.
	 */
	
	public void viewStudent() {
		try {
			Connection con = MainClass.getConnection();
			String sql = "Select studentid, studentname, studentmobile FROM studentsrecord";
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(); // execute
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String mob = rs.getString(3);
				System.out.println("Student id= " + id + ", Student Name= " + name + ", Contact Number= " + mob);
			}
			con.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	////////////////   		End of viewBook() Method 			//////////////////////////////////////
	
	////////////////   		removeOldBook() Method starts		//////////////////////////////////////
	
	/**
	 * This method is used to remove a book from the library records.
	 */
	
	public void removeOldBook() {

		Integer bookId;
		System.out.print("Enter Book ID :  ");
		Scanner intScanner = new Scanner(System.in);
		bookId = intScanner.nextInt();
		try {
			Connection con = MainClass.getConnection();
			String sql = " delete from booksrecord where bookid=?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setInt(1, bookId);
			stmt.executeUpdate();
			System.out.println("\nBook Removed Successfully...\n\n");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//////////////    	End of removeOldBook() Method 			//////////////////////////////////////
	
}
