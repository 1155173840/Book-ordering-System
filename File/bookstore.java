import java.util.*;
import java.io.*;
import java.sql.*;

public class bookstore {
            
    public int bookstoreInterface() throws IOException
	{
		String Output = "";
		Output += "<This is the bookstore interface.>\n";
		Output += "---------------------------------------\n";
		Output += "1. Order Update.\n";
		Output += "2. Order Query.\n";
		Output += "3. N most Popular Book Query.\n";
		Output += "4. Back to main menu.\n";
		Output += "\n";
		
		//Prepare the reader which reads user inputs from the console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
				
		System.out.printf(Output);
		
		while(true)
		{
			System.out.println("Please enter your choice??..");
			input = reader.readLine();		
			if(input.matches("[1-4]"))
			{
                return Integer.parseInt(input);
			}
			else
				System.out.println("Invalid input. Please input again.");
		}
	}
    
    public static void bookstore_main() throws IOException
    {
		// Database driver
        Connection con = DatabaseConnection.connectToOracle();
		
		bookstore myBookstoreObj = new bookstore();
		
		String orderid = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		while (true)
		{
			int choice = myBookstoreObj.bookstoreInterface();
                        

			// Order Update
			if (choice == 1) 
			{
				System.out.printf("Please input the order ID:");
				orderid = reader.readLine();
				
				ResultSet rs3=null;
				PreparedStatement pst = null;
				
				boolean idvalue = false ;
				
				ResultSet rs1 = null, rs2 = null;
				String status = null;
				int quantity = 0, updateStatus1 = 0;
				
				try {
					String select = "SELECT O.shipping_status FROM orders O WHERE O.order_id=?";
					pst = con.prepareStatement(select);
					pst.setString(1, orderid);
					rs1 = pst.executeQuery();
					
					String select2 = "SELECT OL.quantity FROM ordering OL WHERE OL.order_id=?";
					pst = con.prepareStatement(select2);
					pst.setString(1, orderid);
					rs2 = pst.executeQuery();
					
					while(rs1.next()) {
						status = rs1.getString("shipping_status");
					}
					while(rs2.next()) {
						quantity = rs2.getInt("quantity");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				
				if (status.equals("N") && quantity>=1) {
					System.out.printf("the Shipping status of %s is %s and %d books ordered\n", orderid, status, quantity);
					System.out.printf("Are you sure to update the shipping status? (Yes=Y)");
					String updateOpt = reader.readLine();
					if (updateOpt.equals("Y")) {
						
						try {
							String select3 = "UPDATE orders OL SET OL.shipping_status='Y' WHERE OL.order_id=?";
							pst = con.prepareStatement(select3);
							pst.setString(1, orderid);
							updateStatus1 = pst.executeUpdate();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						System.out.printf("Updated shipping status\n\n");
					}
					else{ 
					System.out.printf("Update is cancelled.\n\n");
					}
				}
				else {
				System.out.printf("Update failed. Reason1: order_id doesn't exist. Reason2: the shipping status is Y. Reason3: less than 1 book is ordered.\n\n");
				}
			
			}
			
			// Order Query
			if (choice == 2) 
			{
				System.out.printf("Please input the Month for Order Query (e.g.2005-09):");
				String month=reader.readLine();
				
				ResultSet rs3=null;
				PreparedStatement pst = null;


				//System.out.println(month+"-__");
				
				try {
					String select = "SELECT * FROM orders WHERE o_date LIKE ? ORDER BY order_id";
					pst = con.prepareStatement(select);
					pst.setString(1, month+"-__");
					rs3 = pst.executeQuery();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}

				
				
				int chargeTotal = 0;
				int counter=0;
				try {
					while(rs3.next())
					{
					//	System.out.println("ok here1");
						counter++;
					//	System.out.println("ok here2");

						System.out.printf("Record: %d\n", counter);
					//	System.out.println("ok here3");
						
						String order_id = rs3.getString("order_id");
						String customer_id = rs3.getString("customer_id");
						String date = rs3.getString("o_date");
						int charge = rs3.getInt("charge");
						
						chargeTotal=chargeTotal+charge;
						
						System.out.println("order_id: " + order_id);
						System.out.println("customer_id: " + customer_id);
						System.out.println("date: " + date);
						System.out.printf("charge: %d\n\n", charge);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				if (counter == 0)
					System.out.printf("No record of the month.\n\n");
				else 
					System.out.printf("Total charges of the month is %d\n\n", chargeTotal);
	
				
			}	
			
			// N most Popular Book Query
			if (choice == 3) 
			{
				System.out.printf("Please input the N popular books number:");
				String input = reader.readLine();
				
				int input_num = Integer.parseInt(input);
				
				if(input_num<=0) {
					System.out.printf("Invalid input. Please input again.\n");
				}
				else {
				
				ResultSet rs4=null;
				PreparedStatement pst = null;

			//	System.out.println(input_num);
				
				try {
					String select = "SELECT a.sum, a.ISBN "
							+ "FROM (SELECT sum(quantity) as sum,ISBN FROM ordering GROUP BY ISBN)a "
							+ "ORDER BY a.sum DESC "
							+ "FETCH FIRST ? ROWS ONLY ";
					pst = con.prepareStatement(select);
					pst.setInt(1, input_num);
					rs4 = pst.executeQuery();
							
				} catch (SQLException e) {
					e.printStackTrace();
				}
				

			//	System.out.println("ok here!!!");

				//quantity
				int limit=1000000;
				try {
					while(rs4.next())
					{
						int quantitySum = rs4.getInt("sum");
						
						if (quantitySum < limit) {
							limit = quantitySum;
						}
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			//System.out.println("ok here.");
				
				System.out.printf("ISBN              Title                Copies\n");
				
				ResultSet rs5 = null;
				PreparedStatement pst1 = null;
				
				try {
					String select2 = "SELECT b.ISBN, b.title, c.sum "
								+ "FROM book b, (SELECT sum(quantity) as sum,ISBN FROM ordering GROUP BY ISBN)c "
								+ "WHERE c.sum>=? AND c.ISBN=b.ISBN "
								+ "ORDER BY c.sum DESC, b.title, b.ISBN";
					pst1 = con.prepareStatement(select2);
					pst1.setInt(1, limit);
					rs5 = pst1.executeQuery();
							
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				try {
					while(rs5.next())
					{
						String quantitySum = rs5.getString("sum");
						String ISBN=rs5.getString("ISBN");
						String title=rs5.getString("title");
						System.out.printf("%s     %s     %s\n", ISBN, title, quantitySum );
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				}
			}
            // Go back to main page
            if (choice == 4) break;

        }
	}
}