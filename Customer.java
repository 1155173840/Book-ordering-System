import java.util.*;
import java.io.*;
import java.sql.*;

public class Customer {
	
	
	public int customerInterfaceHandler() throws IOException
	{
        
		String OutputString = "";
		OutputString += "<This is the customer interface.>\n";
		OutputString += "---------------------------------------\n";
		OutputString += "1. Book Search.\n";
		OutputString += "2. Order Creation.\n";
		OutputString += "3. Order Altering.\n";
		OutputString += "4. Order Query.\n";
		OutputString += "5. Back to main menu.\n";
		OutputString += "\n";
		
		//Prepare the reader which reads user inputs from the console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
				
		System.out.printf(OutputString);
		
		while(true)
		{
			System.out.println("What is your choice??..");
			input = reader.readLine();		
			if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5"))
			{
				int choice = input.charAt(0) - '0';
				return choice;
			}
			else
				System.out.println("INVALID INPUT!");
		}
	}
	
	
	public int performBookSearch(Connection conObj) throws IOException, SQLException
	{
		int status = 0;
		
		String OutputString = "";
		OutputString += "What do u want to search??\n";
		OutputString += "1 ISBN\n";
		OutputString += "2 Book Title\n";
		OutputString += "3 Author Name\n";
		OutputString += "4 Exit\n";
		OutputString += "Your choice?...";
		

        
		// prepare the reader which reads user inputs from the console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String inputChoice = null;
		
		while(true) // loop in the interface until the user chooses to exit
		{
			// query user input
			int choice;
			while(true)
			{
				System.out.printf(OutputString);
				inputChoice = reader.readLine();
				if(inputChoice.equals("1") || inputChoice.equals("2") || inputChoice.equals("3") || inputChoice.equals("4"))
				{
					choice = inputChoice.charAt(0) - '0';
					break;
				}
				else
					System.out.println("INVALID INPUT!\n");
			}
			
			// exit
			if (choice == 4)  return status;	
			
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			
			// search ISBN
			if (choice == 1)
			{
				System.out.println("Input the ISBN:");
				String inputISBN = reader.readLine();
				
				String psql = "SELECT * FROM book B WHERE B.ISBN = ? ORDER BY B.title, B.ISBN";

				pstmt = conObj.prepareStatement(psql);
				pstmt.setString(1, inputISBN);			
				
				status = 1;
			}
			
			// search book title
			else if (choice == 2)
			{
				System.out.println("Input the Book Title:");
				String inputBooktitle = reader.readLine();
				
				String psql = "SELECT * FROM book B WHERE B.title LIKE ? ORDER BY B.title, B.ISBN";
				
				pstmt = conObj.prepareStatement(psql);
				pstmt.setString(1, "%"+inputBooktitle+"%");
				
				status = 2;
			}
			
			// search author name
			else if (choice == 3)
			{
				System.out.println("Input the Author Name:");
				String inputAuthorName = reader.readLine();
				
				String psql = "SELECT B.ISBN, B.title, B.unit_price, B.no_of_copies "
						    + "FROM book B, book_author BA "
						    + "WHERE B.ISBN = BA.ISBN AND BA.author_name LIKE ? "
						    + "ORDER BY B.title, B.ISBN";
				
				pstmt = conObj.prepareStatement(psql);
				pstmt.setString(1, "%"+inputAuthorName+"%");

				status = 3;
			}
			
			rs = pstmt.executeQuery();
			
			// output the result
			// outputting a list of authors is realized here
			int counter = 0;
			while (rs.next())
			{
				counter++;
				String ISBN = rs.getString("ISBN");
				String title = rs.getString("title");
				int unit_price = rs.getInt("unit_price");
				int no_of_copies = rs.getInt("no_of_copies");
				System.out.printf("======= %d-th record:\n", counter);
				System.out.println("ISBN: " + ISBN + "   "
						         + "Book title: " + title + "   "
						         + "Unit price: " + unit_price + "   "
						         + "No of available: " + no_of_copies + "   ");
				
				// output a list of authors
				ResultSet rs2 = null;
				PreparedStatement pstmt2 = null;			
				String psql2 = "SELECT author_name FROM book_author BA WHERE BA.ISBN = ? ORDER BY author_name";
				pstmt2 = conObj.prepareStatement(psql2);
				pstmt2.setString(1, ISBN);
				rs2 = pstmt2.executeQuery();
				
				System.out.println("Authors:");
				int innerCounter = 0;
				while (rs2.next())
				{
					innerCounter++;
					System.out.printf("%d: %s\n", innerCounter, rs2.getString("author_name"));
				}
				System.out.println("");
			}
			if (counter == 0) System.out.println("No records found.\n");
		}		
	}

	
	public int createOrder(Connection conObj) throws IOException, SQLException
	{
		int status = 0;
		boolean firstOrder = true;
		String neworderid = null;
		
		//Prepare the reader which reads user inputs from the console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));		
		
		System.out.println("Please enter your customerID??");
		
		String customerID = reader.readLine();
		
		System.out.println(">> What books do you want to order??");
		System.out.println(">> Input ISBN and then the quantity.");
		System.out.println(">> You can press \"L\" to see ordered list, or \"F\" to finish ordering.");

		//System.out.print(firstOrder);
		//System.out.println("========");
		while(true)
		{		
			String book_ISBN = null;
			int book_quantity = 0;
			
			//????????
			System.out.print("Please enter the book's ISBN: ");
			
			book_ISBN = reader.readLine();
			
			if (book_ISBN.equals("F"))	return status;
			
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			
			if (book_ISBN.equals("L"))
			{
				// see ordered list
				String psql = "SELECT OL.ISBN, OL.quantity "
						    + "FROM orders O, ordering OL "
						    + "WHERE O.order_id = ? AND O.order_id = OL.order_id";
				pstmt = conObj.prepareStatement(psql);
				pstmt.setString(1, neworderid);
				
				rs = pstmt.executeQuery();
				
				// output the result
				System.out.println("ISBN		 NUMBER:\n");
				while(rs.next())
				{
					String ISBN = rs.getString("ISBN");
					String title = rs.getString("quantity");
					System.out.println(ISBN + "    " + title);
				}			
				status = 1;
			}
			else
			{	
				// check if the ISBN input exist.
				boolean ISBN_Valid = false;
				String asql = "SELECT B.ISBN FROM book B WHERE B.ISBN = ?";
				pstmt = conObj.prepareStatement(asql);
				pstmt.setString(1, book_ISBN);
				rs = pstmt.executeQuery();
				while (rs.next())
					ISBN_Valid = true;
				
				if (ISBN_Valid == false)
				{
					System.out.println("ISBN not found. Please input again:");
					continue;
				}
				
				System.out.printf("Please enter the quantity of the order: ");
				while (true)
				{
					book_quantity = Integer.parseInt(reader.readLine());
					
					// we define that negative quantity means quitting
					if (book_quantity < 0)
						break;
					
					// check if the quantity are available
					String psql = "SELECT B.no_of_copies, B.unit_price "
						    	+ "FROM book B "
						    	+ "WHERE B.ISBN = ?";
					pstmt = conObj.prepareStatement(psql);
					pstmt.setString(1, book_ISBN);
					rs = pstmt.executeQuery();
					int no_of_copies_available = 0;
					int book_unit_price = 0;
					while (rs.next()) {
						no_of_copies_available = rs.getInt("no_of_copies");
						book_unit_price = rs.getInt("unit_price");
					}
					// quantity ordered not available
					if (no_of_copies_available < book_quantity)
						System.out.printf("Quantity requested exceeds the maximum number available!\n"
										+ "Please input a number >= 0 and <= %d.\n", no_of_copies_available);
					else
					{ 
						// if it's the first order, perform insert, else perform update
						if (firstOrder)
						{	
							firstOrder = false;
							//get the latest the order_id first
							String psq2 = "SELECT O.order_id "
							    		+ "FROM orders O "
							    		+ "ORDER BY O.order_id DESC "
							    		+ "LIMIT 1 ";
							pstmt = conObj.prepareStatement(psq2);
							rs = pstmt.executeQuery();
							
							String lastestorderid = null;
							while (rs.next()) {
								lastestorderid = rs.getString("order_id");
							}
							
							int numneworderid = Integer.parseInt(lastestorderid)+1;
							neworderid = String.format("%08d", numneworderid);
							
							psql = "INSERT INTO orders VALUES (?, ?, 'N', ?, ?)";
							pstmt = conObj.prepareStatement(psql);
							pstmt.setString(1, neworderid);
							pstmt.setString(2, mainsystem.systemdate);
							pstmt.setInt(3, book_quantity * book_unit_price);
							pstmt.setString(4, customerID);
						}
						else
						{
							psql = "UPDATE orders O "
								 + " SET O.o_date = ?, O.charge = O.charge + ? "
								 + " WHERE O.order_id = ?";
							pstmt = conObj.prepareStatement(psql);
							pstmt.setString(1, mainsystem.systemdate);
							pstmt.setInt(2, book_quantity * book_unit_price);
							pstmt.setString(3, neworderid);
						}
						int updateStatus1 = pstmt.executeUpdate();	
						
						pstmt.close();
						
						// add book
						// check whether the book has been in the ordering list
						psql = "SELECT OL.ISBN "
							 + "FROM ordering OL "
							 + "WHERE OL.order_id = ? AND OL.ISBN = ?";
						pstmt = conObj.prepareStatement(psql);
						pstmt.setString(1, neworderid);
						pstmt.setString(2, book_ISBN);
						boolean bookHasBeenOrdered = false;
						rs = pstmt.executeQuery();
						while(rs.next())
							bookHasBeenOrdered = true;
						
						//System.out.println("CHECK BOOK EXIST FINISHED");///////////
						
						// if the book has been ordered before, perform update, else perform insert
						if (bookHasBeenOrdered == false)
						{
							//firstOrder = false;
							psql = "INSERT INTO ordering VALUES (?, ?, ?)";
							pstmt = conObj.prepareStatement(psql);
							pstmt.setString(1, neworderid);
							pstmt.setString(2, book_ISBN);
							pstmt.setInt(3, book_quantity);
							
							//System.out.println("11111111111111111111111111");
						}
						else
						{
							psql = "UPDATE ordering OL "
								 + "SET OL.quantity = OL.quantity + ? "
								 + "WHERE OL.order_id = ? AND OL.ISBN = ?";
							pstmt = conObj.prepareStatement(psql);
							pstmt.setInt(1, book_quantity);
							pstmt.setString(2, neworderid);
							pstmt.setString(3, book_ISBN);
							
							//System.out.println("2222222222222222222222222222");
						}
						int updateStatus2 = pstmt.executeUpdate();
						
						// update num_of_copies_available
						psql = "UPDATE book B "
							 + "SET B.no_of_copies = B.no_of_copies - ? "
							 + "WHERE B.ISBN = ?";
						pstmt = conObj.prepareStatement(psql);
						pstmt.setInt(1, book_quantity);
						pstmt.setString(2, book_ISBN);
						int updateStatus3 = pstmt.executeUpdate();
						
						
						//System.out.printf("updateStatus1: %d      updateStatus2: %d      "
						//		        + "updateStatus3: %d\n", updateStatus1, updateStatus2, updateStatus3);
						break;
					}
				}
				status = 2;
				
			}
			
		}
	}

	
	public int alterOrder(Connection conObj) throws IOException, SQLException
	{
		int status = 0;
		
		//Prepare the reader which reads user inputs from the console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));		
				
		System.out.println("Please enter the OrderID that you want to change:");
		
		String orderID = reader.readLine();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;	
		
		
		// display order info
		String psql = "SELECT * "
			    + "FROM orders O "
			    + "WHERE O.order_id = ?";
		pstmt = conObj.prepareStatement(psql);
		pstmt.setString(1, orderID);		
		rs = pstmt.executeQuery();
		
		String order_id = null;
		int charge = 0;
		String shipping_status = null;
		String customerID = null;
		
		while(rs.next()) {
			order_id = rs.getString("order_id");
			charge = rs.getInt("charge");
			shipping_status = rs.getString("shipping_status");
			customerID = rs.getString("customer_id");
		}
		
		
		System.out.print("OrderID: " + order_id + "   ");
		System.out.print("customerID: " + customerID + "   ");
		System.out.printf("charge: %d    ", charge);
		System.out.println("shipping status: " + shipping_status + "\n");
				
		
		// display the book list
		psql = "SELECT OL.ISBN, OL.quantity "
			 + "FROM ordering OL "
			 + "WHERE OL.order_id = ?";
		pstmt = conObj.prepareStatement(psql);
		pstmt.setString(1, orderID);
		rs = pstmt.executeQuery();
		
		int counter = 0;
		ArrayList<String> bookList = new ArrayList<String>();
		ArrayList<Integer> bookQuantity = new ArrayList<Integer>();
		while(rs.next())
		{
			counter++;
			bookList.add(rs.getString("ISBN"));
			bookQuantity.add(rs.getInt("quantity"));
			
			System.out.printf("book_no: %d   ", counter);
			System.out.printf("ISBN = %S   ", bookList.get(counter - 1));
			System.out.printf("quantity = %S   \n", bookQuantity.get(counter - 1));			
		}
		
		
		// check the shipping status, if = "Y", return directly.
		if (shipping_status.equals("Y")) {
				System.out.println("Altering request fails. The order has been shipped.");
				return 0;
		}
		
		// shipping status = "N", continue.
		System.out.println("Which book you want to alter (input book no.):");
		int bookNum = Integer.parseInt(reader.readLine());
		
		String addOrRemove = null;
		while(true)
		{
			System.out.println("input add or remove:");
			addOrRemove = reader.readLine();
			if (addOrRemove.equals("add") || addOrRemove.equals("remove"))
				break;
			System.out.println("Invalid choice!");
		}
		
		while (true)
		{
			System.out.println("input the number:");
			int alterNum = Integer.parseInt(reader.readLine());	
					
			psql = "SELECT B.no_of_copies, B.unit_price "
				 + "FROM book B "
				 + "WHERE B.ISBN = ?";
			pstmt = conObj.prepareStatement(psql);
			pstmt.setString(1, bookList.get(bookNum - 1));
			rs = pstmt.executeQuery();
			
			int no_of_copies_available = -1;
			int unit_price = -1;
			
			while (rs.next()) {	
				no_of_copies_available = rs.getInt("no_of_copies");
				unit_price = rs.getInt("unit_price");
			}
			
			// check whether the quantity altering is valid		
			// quantity added invalid
			if (addOrRemove.equals("add") && no_of_copies_available < alterNum)
			{
				System.out.printf("quantity requested exceeds the max quantity available %d !\n", no_of_copies_available);
				continue;
			}
			// quantity removed invalid
			else if (addOrRemove.equals("remove") && (alterNum < 0 || alterNum > bookQuantity.get(bookNum - 1)) )
			{
				System.out.println("the removing quantity is invalid!");
				continue;
			}
				
			// The quantity is valid, continue.
			System.out.println("The update is OK!");	
					
			// update the quantity
			int updated_quantity = 0; 
			if (addOrRemove.equals("add")) {
				updated_quantity = bookQuantity.get(bookNum - 1) + alterNum;
				no_of_copies_available -= alterNum;
			}
			if (addOrRemove.equals("remove")) {
				updated_quantity = bookQuantity.get(bookNum - 1) - alterNum;
				no_of_copies_available += alterNum;
			}
			bookQuantity.set(bookNum - 1, updated_quantity);
			
			String temp2 = bookList.get(bookNum - 1); 
			
			// perform updating table "book", "ordering" and "orders"
			psql = "UPDATE book B SET B.no_of_copies = ? WHERE B.ISBN = ?";
			pstmt = conObj.prepareStatement(psql);
			pstmt.setInt(1, no_of_copies_available);
			pstmt.setString(2, temp2);
			pstmt.executeUpdate(); 
			
			psql = "UPDATE ordering OL " 
				 + "SET OL.quantity = ? "
				 + "WHERE OL.order_id = ? AND OL.ISBN = ?";
			pstmt = conObj.prepareStatement(psql);
			pstmt.setInt(1, updated_quantity);
			pstmt.setString(2, orderID);
			pstmt.setString(3, bookList.get(bookNum - 1));
			pstmt.executeUpdate(); 
					
			psql  = "UPDATE orders O "; 
			if (addOrRemove.equals("add")) {
				psql += "SET O.charge = O.charge + ?, O.o_date = ? ";
			}
			if (addOrRemove.equals("remove")) {
				psql += "SET O.charge = O.charge - ?, O.o_date = ? ";
			}
			psql += "WHERE O.order_id = ?";
			pstmt = conObj.prepareStatement(psql);
			pstmt.setInt(1, alterNum * unit_price);
			pstmt.setString(2, mainsystem.systemdate);
			pstmt.setString(3, orderID);
			pstmt.executeUpdate(); 
			
			System.out.println("The update is done!");
			
			// print order info and book list again
			System.out.print("OrderID: " + order_id + "   ");
			System.out.print("customerID: " + customerID + "   ");
			System.out.printf("charge: %d    ", charge);
			System.out.println("shipping status: " + shipping_status + "\n");
			for (int i = 0; i < bookList.size(); i++) {
				System.out.printf("book_no: %d   ", i + 1);
				System.out.printf("ISBN = %S   ", bookList.get(i));
				System.out.printf("quantity = %S   \n", bookQuantity.get(i));		
	        }
			
			break;
		}			
		return status;
	}
	
	
	public int queryOrder(Connection conObj) throws IOException, SQLException
	{
		int status = 0;
		
		//Prepare the reader which reads user inputs from the console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));		
				
		System.out.print("Please Input customer ID:");
		String customerID = reader.readLine();
		
		System.out.print("Please Input the Year:");
		String year = reader.readLine();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;	

		String psql = "SELECT * "
			    	+ "FROM orders O "
			    	+ "WHERE O.customer_id = ? AND O.o_date LIKE ? "
			    	+ "ORDER BY O.order_id";
	
		pstmt = conObj.prepareStatement(psql);
		pstmt.setString(1, customerID);
		pstmt.setString(2, year+"-__-__");
	
		rs = pstmt.executeQuery();
	
		// output the result
		int counter = 0;
		while(rs.next())
		{
			counter++;
			System.out.printf("\nRecord: %d\n", counter);
			
			String order_id = rs.getString("order_id");
			String o_date = rs.getString("o_date");
			int charge = rs.getInt("charge");
			String shipping_status = rs.getString("shipping_status");
			
			System.out.println("OrderID: " + order_id);
			System.out.println("OrderDate: " + o_date);
			System.out.printf("charge: %d\n", charge);
			System.out.println("shipping status: " + shipping_status);
		}		
		if (counter == 0)
			System.out.println("No records found.");
		
		status = 1;
		
		return status;
	}
	
	
	public static void customer_main() throws IOException
	{
		
        Connection con = DatabaseConnection.connectToOracle();
		Customer myCustomerObj = new Customer();
		while (true)
		{
			int choice = myCustomerObj.customerInterfaceHandler();
	
		
			if (choice == 5) break;	 //back to the main page
		
			// Book Search
			if (choice == 1) 
			{
				int status = 0;
				try 
				{
					status = myCustomerObj.performBookSearch(con);
				} catch (IOException | SQLException e) 
				{
					e.printStackTrace();
				}
				System.out.println("You have finished Book Search!    \n");
			}
			
			// Order Creation 
			if (choice == 2) 
			{
				int status = 0;
				try 
				{
					status = myCustomerObj.createOrder(con);
				} catch (IOException | SQLException e) 
				{
					
					e.printStackTrace();
				}
				System.out.println("You have finished Order Creation!    \n");
			}	
			
			// Order Altering
			if (choice == 3) 
			{
				int status = 0;
				try 
				{
					status = myCustomerObj.alterOrder(con);
				} catch (IOException | SQLException e) 
				{
					
					e.printStackTrace();
				}
				System.out.println("You have finished altering order!    \n");			
			}
			
			// Order Query
			if (choice == 4)
			{
				int status = 0;
				try 
				{
					status = myCustomerObj.queryOrder(con);
				} catch (IOException | SQLException e) 
				{
					
					e.printStackTrace();
				}
				System.out.println("You have finished Order Query!    \n");
			}
			
		}
		
	}
		
}
