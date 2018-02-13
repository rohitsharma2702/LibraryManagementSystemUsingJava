package librarymanagement;

import java.sql.*;
import java.util.*;

/**
 * 
 * This is the main class of this project which contains the main method.
 *
 */

public class MainClass {

	/////////////////    getConnection() method starts       //////////////////////////////////////
	
	/**
	 * 
	 * This method establishes the connection with the database 
	 * and returns the Connection object
	 *
	 */
	public static Connection getConnection()
	{
		Connection con = null;
		try{
			Class.forName("com.mysql.jdbc.Driver"); // register driver class
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
		}
		catch(Exception ex){
		System.out.println("Error :  " + ex.getMessage());
		}
		return con; 
	}
	
	///////////////   End of getConnection() method starts       ////////////////////////////////////////
	
	/////////////////    checkStudentAccount() method starts       //////////////////////////////////////
	
	/**
	 * This method checks the account of a student.
	 */
	
	public void checkStudentAccount() {
		System.out.println("Enter your Student Id :  ");
		Scanner sc = new Scanner(System.in);
		int studentId = sc.nextInt();
		try {
			Connection con = MainClass.getConnection();
			String sql = "Select studentid, bookid, bookname, studentname, dateofissue FROM issuedbookrecord where studentid=?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setInt(1, studentId);
			ResultSet rs = stmt.executeQuery(); // execute
			System.out.println("Books issued at ur Id  " + studentId + " are as follow:");
			int booksFound = 0;
			while (rs.next()) {
				booksFound++;
				int bId = rs.getInt(1);
				int stId = rs.getInt(2);
				String bName = rs.getString(3);
				String sName = rs.getString(4);
				String date = rs.getString(5);
				System.out.println("Book id= " + bId + ", Student Id= " + stId + ", Book Name= " + bName
						+ ", Student Name= " + sName + ", Date Of Issue= " + date);
			}
			if (booksFound == 0)
				System.out.println("No books issued at ur id " + studentId);
			con.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/////////////////    End of checkStudentAccount() method 		 //////////////////////////////////////
	
	/////////////////    main() method starts						 //////////////////////////////////////	
	
	/**
	 * 
	 * This is the main method of the project.
	 */
	
	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		Librarian librarian = new Librarian();
		Integer choice = 0;
		System.out.println("************Welcome to the Library************");
		System.out.println(
				"----------------------------------------------" + "\n----------------------------------------------");
		System.out.println("************Welcome to the Library************");

		while (choice != 3) {
			System.out.println("1:Enter as a librarian");
			System.out.println("2:Enter as a student");
			System.out.println("3:exit");
			choice = input.nextInt();

			switch (choice) {
			case 1:
				System.out.println("Enter username");
				String Lname = input.nextLine();
				System.out.println(" Enter Password");
				String lpassword = input.nextLine();
				if (Lname.equalsIgnoreCase(librarian.username) && lpassword.equals(librarian.password)) {
					System.out.println("welcome Mr" + ". " + librarian.librarianName);
					int operation = 0;
					int ch = 0;
					int op = 0;
					while (operation != 8) {
						System.out.println("Here is a list of operation you can perform ");
						System.out.println("1: Register New Student");
						System.out.println("2: Issue Books");
						System.out.println("3: Return Books");
						System.out.println("4: Add a new book in library");
						System.out.println("5:search new book");
						System.out.println("6:Viewbooks");
						System.out.println("7:Remove old book");
						System.out.println("8:exit");
						operation = input.nextInt();
						switch (operation) {
						case 1:
							System.out.println("Please Register A Student...\n");
							librarian.registerStudent();
							break;
						case 2:
							System.out.println("Please Issue a Book...\n");
							librarian.issueBook();
							break;
						case 3:
							System.out.println("Please Return Your Book...\n");
							librarian.returnBook();
							break;
						case 4:
							System.out.println("Please Add a New Book...\n");
							librarian.addNewBook();
							break;
						case 5:
							Scanner sc = new Scanner(System.in);
							do {
								System.out.println(
										"\n\nPlease select option \n1.SEARCH BY AUTHOR \n 2.SEARCH BY TITLE \n 3.For exit");
								ch = sc.nextInt();
								switch (ch) {
								case 1:
									librarian.searchByAuthor();
									break;
								case 2:
									librarian.searchByTitle();
									break;
								}
							} while (ch != 3);
							break;
						default:
							break;
						case 6:
							Scanner inp = new Scanner(System.in);
							System.out.println(
									"\n\nplease select option \n1.view book record\n 2.see the student record \n3.exit ");
							op = inp.nextInt();
							switch (op) {
							case 1:
								librarian.viewBook();
								break;
							case 2:
								librarian.viewStudent();
								break;
							}
							break;
						case 7:
							System.out.println("Please Remove a New Book...\n");
							librarian.removeOldBook();
							break;
						}
					}
				} else {
					System.out.println("Incorrect Username/Password !!!");
					System.out.println("Try Again !!!");
				}
				break;
			case 2:
				int schoice = 0;
				int sch = 0;
				int vch = 0;
				while (schoice != 4) {
					System.out.println("1:Search for a book");
					System.out.println("2:Check out your account");
					System.out.println("3:view BooK");
					System.out.println("4:exit");
					schoice = input.nextInt();
					switch (schoice) {
					case 1:
						Scanner sc = new Scanner(System.in);
						do {
							System.out.println(
									"\n\nplease select option \n1.SEARCH BY AUTHOR \n 2.SEARCH BY TITLE \n 3.For exit");
							sch = sc.nextInt();
							switch (sch) {
							case 1:
								librarian.searchByAuthor();
								break;
							case 2:
								librarian.searchByTitle();
								break;
							}
						} while (sch != 3);
						break;
					case 2:
						System.out.println("\nPlease Check Your Account...");
						MainClass student = new MainClass();
						student.checkStudentAccount();
						break;
					case 3:
						Scanner inp = new Scanner(System.in);
						System.out.println("\n\nPlease select option \n1.View book record \n2.Exit ");
						vch = inp.nextInt();
						switch (vch) {
						case 1:
							librarian.viewBook();
							break;
						default:
							break;
						}
					default:
						break;
					}
					String Sname = input.next();
					break;
				}
			}
		}
	}
	
	/////////////////    	End of main() method 				//////////////////////////////////////
	
}
