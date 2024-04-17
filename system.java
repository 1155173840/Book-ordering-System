//增加一些报错信息


import java.util.*;
import java.io.*;
import java.sql.*;

public class system{
    public int systemInterface() throws IOException
	{
		String output = "";
		output += "<This is the system interface.>\n";
		output += "---------------------------------------\n";
		output += "1. Create Table.\n";
		output += "2. Delete Table.\n";
		output += "3. Insert Data.\n";
		output += "4. Set System Date.\n";
		output += "5. Back to main menu.\n";
		output += "\n";
		System.out.printf(output);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
		while(true)
        {
            System.out.println("Please enter your choice??..");
            input = reader.readLine();	
            if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5")) 
            {   
               // scanner.close();
                return input.charAt(0) - '0';
            }
            else 
            System.out.println("INVALID INPUT!\n");
        }
	}

    public static void system_main() throws IOException
    {
        Connection con = DatabaseConnection.connectToOracle();

		system mySystemObj = new system();
		
		while (true)
		{
			int choice = mySystemObj.systemInterface();
			System.out.printf("Your choice is %d\n\n", choice);
		
			if (choice == 5) break;	 //back to the main page
		
			// Create Table
			if (choice == 1) 
			{
				
				PreparedStatement pstmt = null;

				String psql = "Create table book("
						+ "ISBN VARCHAR(13),"
						+ "title VARCHAR(100),"
						+ "unit_price INTEGER,"
						+ "no_of_copies INTEGER,"
						+ "PRIMARY KEY (ISBN))";
				//System.out.println(psql);

				
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
					System.out.println("Table 'book' created successfully.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				psql = "Create table customer("
						+ "customer_id VARCHAR(10),"
						+ "name VARCHAR(50),"
						+ "shipping_address VARCHAR(200),"
						+ "credit_card_no VARCHAR(19),"
						+ "PRIMARY KEY (customer_id))";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
					System.out.println("Table 'customer' created successfully.");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
						
				
				psql = "Create table orders("
						+ "order_id VARCHAR(8),"
						+ "o_date VARCHAR(13),"
						+ "shipping_status VARCHAR(1),"
						+ "charge INTEGER,"
						+ "customer_id VARCHAR(10),"
						+ "PRIMARY KEY (order_id),"
						+ "FOREIGN KEY (customer_id) REFERENCES customer(customer_id))";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
					System.out.println("Table 'orders' created successfully.");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				

				
							
				
				psql = "Create table book_author("
						+ "ISBN VARCHAR(13),"
						+ "author_name VARCHAR(50),"
						+ "PRIMARY KEY (ISBN,author_name),"
						+ "FOREIGN KEY (ISBN) REFERENCES book(ISBN))";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
					System.out.println("Table 'book_author' created successfully.");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}	
				
				psql = "Create table ordering("
						+ "order_id VARCHAR(8),"
						+ "ISBN VARCHAR(13),"
						+ "quantity INTEGER,"
						+ "PRIMARY KEY (order_id,ISBN),"
						+ "FOREIGN KEY (order_id) REFERENCES orders(order_id),"
						+ "FOREIGN KEY (ISBN) REFERENCES book(ISBN))";
						
						
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
					System.out.println("Table 'ordering' created successfully.");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}	
				
				
				
			}
			
			// Delete Table
			if (choice == 2) 
			{
				PreparedStatement pstmt = null;

				String psql = "DROP TABLE book_author";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				

				psql = "DROP TABLE ordering";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				psql = "DROP TABLE orders";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
			
				
				psql = "DROP TABLE customer";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				psql = "DROP TABLE book";
				try {
					pstmt = con.prepareStatement(psql);
					int updatestatus = pstmt.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
			}
			
			// Insert Data
			if (choice == 3) 
			{
				// String array stores all txt 
				ArrayList<String> txtList = new ArrayList<String>();
				txtList.add("book.txt");
				txtList.add("customer.txt");
				txtList.add("orders.txt");
				txtList.add("ordering.txt");
				txtList.add("book_author.txt");

				
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String input = null;
				System.out.println("Please enter the folder path");
				input = reader.readLine();
				String fn = input + "/" + txtList.get(0);
				File file = new File(fn);
				int success = 0;
				for (int i = 0; i < 5; i++)
				{
					fn = input + "/" + txtList.get(i);
					file = new File(fn);
					if (!file.exists()) {
				    System.out.println("Wrong path");
				    break;
				    } else 
				    {
					success++;
					}
					}
				
				if (success==5)
				{
				//1111111111111book
				String fileName = input + "/" + txtList.get(0);
				file= new File(fileName);
				
				// prepare the buffer reader
				reader= new BufferedReader(new FileReader(file));;
				
				PreparedStatement pstmt = null;
				
				// 1 represents the corresponding attribute is type String, 0 represents Int
				int StrOrIntbook[] = {1, 1, 0, 0};
				String tempString= null;
				
				// read line by line
				String sql = null;
				boolean txtNotNULL = false;
				while ((tempString = reader.readLine()) != null) // a while loop read one txt
				{
					txtNotNULL = true;
					sql = null;
					String stringarray[] = tempString.split("\\|");
					
					//avoid ' problem
					for (int c = 0; c < stringarray.length; c++)
					{
					int index=stringarray[c].indexOf("'");
						if (index!=-1) {
							StringBuffer a = new StringBuffer();
							a.append(stringarray[c]).insert(index,"'");
							stringarray[c] = a.toString();
						}
					}
					
					sql =  "INSERT INTO book VALUES "
						 + "(";
					for (int i = 0; i < stringarray.length; i++)
					{
						if (i != 0) //Whether to add a comma
							sql += ", ";
						if (StrOrIntbook[i] == 1) // Is a String
							sql += "'" + stringarray[i] + "'";
						if (StrOrIntbook[i] == 0) // Is a Int
						    sql += stringarray[i];
					}
					sql += ")";
					
					if (txtNotNULL)
					{
						try {
							pstmt = con.prepareStatement(sql);
							int updatestatus = pstmt.executeUpdate();
						} catch (SQLException e) 
						{
							e.printStackTrace();
						}
					}	
					
				}
				reader.close();
				
				
				//222222222customer
				fileName = input + "/" + txtList.get(1);
				file= new File(fileName);
				
				// prepare the buffer reader
				reader= new BufferedReader(new FileReader(file));;
				
				// 1 represents the corresponding attribute is type String, 0 represents Int
				int StrOrIntcustomer[] = {1, 1, 1, 1};
				
				// read line by line
				sql = null;
				txtNotNULL = false;
				while ((tempString = reader.readLine()) != null) // a while loop read one txt
				{
					txtNotNULL = true;
					sql = null;
					String stringarray[] = tempString.split("\\|");		
					
					//avoid ' problem
					for (int c = 0; c < stringarray.length; c++)
					{
					int index=stringarray[c].indexOf("'");
						if (index!=-1) {
							StringBuffer a = new StringBuffer();
							a.append(stringarray[c]).insert(index,"'");
							stringarray[c] = a.toString();
						}
					}
					
					sql =  "INSERT INTO customer VALUES "
						 + "(";
					for (int i = 0; i < stringarray.length; i++)
					{
						if (i != 0) //Whether to add a comma
							sql += ", ";
						if (StrOrIntcustomer[i] == 1) // Is a String
							sql += "'" + stringarray[i] + "'";
						if (StrOrIntcustomer[i] == 0) // Is a Int
						    sql += stringarray[i];
					}
					sql += ")";
					
					if (txtNotNULL)
					{
						try {
							pstmt = con.prepareStatement(sql);
							int updatestatus = pstmt.executeUpdate();
						} catch (SQLException e) 
						{
							e.printStackTrace();
						}
					}	
					
				}
				reader.close();
				
				
				//33333333orders
				fileName = input + "/" + txtList.get(2);
				file= new File(fileName);
				
				// prepare the buffer reader
				reader= new BufferedReader(new FileReader(file));;
				
				// 1 represents the corresponding attribute is type String, 0 represents Int
				int StrOrIntorders[] = {1,1,1,0,1};
				
				// read line by line
				sql = null;
				txtNotNULL = false;
				while ((tempString = reader.readLine()) != null) // a while loop read one txt
				{
					txtNotNULL = true;
					sql = null;
					String stringarray[] = tempString.split("\\|");	
					
					//avoid ' problem
					for (int c = 0; c < stringarray.length; c++)
					{
					int index=stringarray[c].indexOf("'");
						if (index!=-1) {
							StringBuffer a = new StringBuffer();
							a.append(stringarray[c]).insert(index,"'");
							stringarray[c] = a.toString();
						}
					}
					
					sql =  "INSERT INTO orders VALUES "
						 + "(";
					for (int i = 0; i < stringarray.length; i++)
					{
						if (i != 0) //Whether to add a comma
							sql += ", ";
						if (StrOrIntorders[i] == 1) // Is a String
							sql += "'" + stringarray[i] + "'";
						if (StrOrIntorders[i] == 0) // Is a Int
						    sql += stringarray[i];
					}
					sql += ")";
					
					if (txtNotNULL)
					{
						try {
							pstmt = con.prepareStatement(sql);
							int updatestatus = pstmt.executeUpdate();
						} catch (SQLException e) 
						{
							e.printStackTrace();
						}
					}	
					
				}
				reader.close();
				
				
				//444444ordering
				fileName = input + "/" + txtList.get(3);
				file= new File(fileName);
				
				// prepare the buffer reader
				reader= new BufferedReader(new FileReader(file));;
				
				// 1 represents the corresponding attribute is type String, 0 represents Int
				int StrOrIntordering[] = {1,1,0};
				
				// read line by line
				sql = null;
				txtNotNULL = false;
				while ((tempString = reader.readLine()) != null) // a while loop read one txt
				{
					txtNotNULL = true;
					sql = null;
					String stringarray[] = tempString.split("\\|");		
					
					//avoid ' problem
					for (int c = 0; c < stringarray.length; c++)
					{
					int index=stringarray[c].indexOf("'");
						if (index!=-1) {
							StringBuffer a = new StringBuffer();
							a.append(stringarray[c]).insert(index,"'");
							stringarray[c] = a.toString();
						}
					}
					
					
					sql =  "INSERT INTO ordering VALUES "
						 + "(";
					for (int i = 0; i < stringarray.length; i++)
					{
						if (i != 0) //Whether to add a comma
							sql += ", ";
						if (StrOrIntordering[i] == 1) // Is a String
							sql += "'" + stringarray[i] + "'";
						if (StrOrIntordering[i] == 0) // Is a Int
						    sql += stringarray[i];
					}
					sql += ")";
										
					if (txtNotNULL)
					{
						try {
							pstmt = con.prepareStatement(sql);
							int updatestatus = pstmt.executeUpdate();
						} catch (SQLException e) 
						{
							e.printStackTrace();
						}
					}	
					
				}
				reader.close();
				
				
				//5555555555book_author
				fileName = input + "/" + txtList.get(4);
				file= new File(fileName);
				
				// prepare the buffer reader
				reader= new BufferedReader(new FileReader(file));;
				
				// 1 represents the corresponding attribute is type String, 0 represents Int
				int StrOrIntbook_author[] = {1, 1};
				
				// read line by line
				sql = null;
				txtNotNULL = false;
				while ((tempString = reader.readLine()) != null) // a while loop read one txt
				{
					txtNotNULL = true;
					sql = null;
					String stringarray[] = tempString.split("\\|");	
					
					//avoid ' problem
					for (int c = 0; c < stringarray.length; c++)
					{
					int index=stringarray[c].indexOf("'");
						if (index!=-1) {
							StringBuffer a = new StringBuffer();
							a.append(stringarray[c]).insert(index,"'");
							stringarray[c] = a.toString();
						}
					}
					
					
					sql =  "INSERT INTO book_author VALUES "
						 + "(";
					for (int i = 0; i < stringarray.length; i++)
					{
						if (i != 0) //Whether to add a comma
							sql += ", ";
						if (StrOrIntbook_author[i] == 1) // Is a String
							sql += "'" + stringarray[i] + "'";
						if (StrOrIntbook_author[i] == 0) // Is a Int
						    sql += stringarray[i];
					}
					sql += ")";
										
					if (txtNotNULL)
					{
						try {
							pstmt = con.prepareStatement(sql);
							int updatestatus = pstmt.executeUpdate();
						} catch (SQLException e) 
						{
							e.printStackTrace();
						}
					}
					
				}
				reader.close();
				System.out.println("Processing...Data is loaded!");
				}
				}
				
			// Set System Date
			if (choice == 4)
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String input = null;
				System.out.printf("Please Input the date (YYYYMMDD): ");
				input = reader.readLine();
				
				if (input.compareTo("00000101")<0||input.compareTo("99991231")>0) {
					System.out.println("Invalid input!");
				}
				else {
				
				StringBuffer sb = new StringBuffer(); 
				sb.append(input).insert(4,"-");
				sb.insert(7,"-");
				input = sb.toString();
				
				String sql = "SELECT * FROM orders ORDER BY o_date DESC";
				
				PreparedStatement pstmt = null;
				String odate = null;
				try {
					pstmt = con.prepareStatement(sql);
					ResultSet rs = pstmt.executeQuery();
					while(rs.next())
					{
						odate = rs.getString("o_date");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				System.out.printf("Latest date in orders: %s\n", odate);
				
				String finaldate = null;
				if(input.compareTo(odate) > 0)
					finaldate = input;
				else
					finaldate = odate;
				if(mainsystem.systemdate.compareTo(finaldate) < 0)
					mainsystem.systemdate = finaldate;
				
				System.out.println("Today is " + mainsystem.systemdate);
				}
			}
		}
		

			
					
				
        /* 
        system mysystem = new system();
        while(true)
        {
            int result = mysystem.systemInterface();
            if(result==1)//create table
            {
                String[] sqlStatements = {
                    "CREATE TABLE book(ISBN CHAR(13), title CHAR(100), unit_price INTEGER, no_of_copies INTEGER, PRIMARY KEY (ISBN))",
                    "CREATE TABLE customer(customer_id CHAR(10), name CHAR(50), shipping_address CHAR(200), credit_card_no CHAR(19), PRIMARY KEY (customer_id))",
                    "CREATE TABLE orders(order_id CHAR(8), o_date CHAR(13), shipping_status CHAR(1), charge INTEGER, customer_id CHAR(10), PRIMARY KEY (order_id))",
                    "CREATE TABLE ordering(order_id CHAR(8), ISBN CHAR(13), quantity INTEGER, PRIMARY KEY (order_id,ISBN), FOREIGN KEY (order_id) REFERENCES orders(order_id), FOREIGN KEY (ISBN) REFERENCES book(ISBN))",
                    "CREATE TABLE book_author(ISBN CHAR(13), author_name CHAR(50), PRIMARY KEY (ISBN,author_name), FOREIGN KEY (ISBN) REFERENCES book(ISBN))"
                };
                for (String sql : sqlStatements) 
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.executeUpdate();
                    }
                    catch (SQLException e) {
                            e.printStackTrace();
                    }
            }
            if (result == 2) //delete table
            {
                String[] tables = {"book_author", "ordering", "orders", "customer", "book"};
                for (String table : tables) {
                    String sql = "DROP TABLE IF EXISTS " + table;
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.executeUpdate();
                        System.out.println("Table " + table + " dropped successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error dropping table " + table);
                        e.printStackTrace();
                    }
                }
            }
            if (result == 3) //insert data
            {
                String[] txtList = {"book.txt", "customer.txt", "orders.txt", "ordering.txt", "book_author.txt"};
                int[][] strOrIntList = {{1, 1, 0, 0}, {1, 1, 1, 1}, {1, 1, 1, 0, 1}, {1, 1, 0}, {1, 1}};//1 for string,0 for int
            
                Scanner scanner = new Scanner(System.in);
                System.out.println("Please enter the folder path");
                String input = scanner.nextLine();
            
                for (int i = 0; i < txtList.length; i++) {
                    String fileName = input + "/" + txtList[i];
                    File file = new File(fileName);
                    if (!file.exists()) {
                        System.out.println("File " + fileName + " does not exist.");
                        continue;
                    }
                    BufferedReader reader = new BufferedReader(new FileReader(file));

                    String temp;
                    while ((temp = reader.readLine()) != null) {
                        String[] stringArray = temp.split("\\|");
                        for (int k = 0; k < stringArray.length; k++) {
                            int index = stringArray[k].indexOf("'");
                            if (index != -1) {
                                StringBuffer a = new StringBuffer();
                                a.append(stringArray[k]).insert(index, "'");
                                stringArray[k] = a.toString();
                            }
                        }
            
                        String sql =  "INSERT INTO " + txtList[i].substring(0, txtList[i].length() - 4) + " VALUES (";
                        for (int j = 0; j < stringArray.length; j++) {
                            if (j != 0) 
                                sql += ", ";
                            if (strOrIntList[i][j] == 1) // Is a String
                                sql += "'" + stringArray[j] + "'";
                            if (strOrIntList[i][j] == 0) // Is a Int
                                sql += stringArray[j];
                        }
                        sql += ")";
            
                        try (PreparedStatement ps = con.prepareStatement(sql)) {
                            ps.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    reader.close();
                }
                scanner.close();
            }
            if (result == 4) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Please Input the date (YYYYMMDD): ");
                String input = scanner.nextLine();
            
                if (input.length() != 8 || !input.matches("\\d{8}")) {
                    System.out.println("Invalid input! Please enter a date in the format YYYYMMDD.");
                } else {
                    //int year = Integer.parseInt(input.substring(0, 4));
                    int month = Integer.parseInt(input.substring(4, 6));
                    int day = Integer.parseInt(input.substring(6, 8));
            
                    if (month < 1 || month > 12 || day < 1 || day > 31) {
                        System.out.println("Invalid date! Please check the month and day values.");
                    } else {
                        input = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6);
            
                        String sql = "SELECT * FROM orders ORDER BY o_date DESC LIMIT 1";
                        String odate = null;
                        try (PreparedStatement ps = con.prepareStatement(sql)) {
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                odate = rs.getString("o_date");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
            
                        System.out.printf("Latest date in orders: %s\n", odate);
            
                        if (input.compareTo(odate) > 0) {
                            mainsystem.systemdate = input;
                        } else if (mainsystem.systemdate.compareTo(odate) < 0) {
                            mainsystem.systemdate = odate;
                        }
            
                        System.out.println("Today is " + mainsystem.systemdate);
                    }
                }
                scanner.close();
            }
            if(result==5)
            break; 
            
        }
        */


    }
}


