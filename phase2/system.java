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
                return input.charAt(0) - '0';
            else 
            System.out.println("INVALID INPUT!\n");
        }
	}

    public static void system_main() throws IOException
    {
        Connection con = DatabaseConnection.connectToOracle();
        system mysystem = new system();
        while(true)
        {
            int result = mysystem.systemInterface();
            if(result==1)//create table
            {
                String[] sqlStatements = {
                    // "CREATE TABLE book(ISBN CHAR(13), title VARCHAR2(100), unit_price NUMBER, no_of_copies NUMBER, PRIMARY KEY (ISBN));",
                    "CREATE TABLE book(ISBN CHAR(13), title CHAR(100), unit_price INTEGER, no_of_copies INTEGER, PRIMARY KEY (ISBN))",
                    "CREATE TABLE customer(customer_id CHAR(10), name CHAR(50), shipping_address CHAR(200), credit_card_no CHAR(19), PRIMARY KEY (customer_id))",
                    "CREATE TABLE orders(order_id CHAR(8), o_date CHAR(13), shipping_status CHAR(1), charge INTEGER, customer_id CHAR(10), PRIMARY KEY (order_id))",
                    "CREATE TABLE ordering(order_id CHAR(8), ISBN CHAR(13), quantity INTEGER, PRIMARY KEY (order_id,ISBN), FOREIGN KEY (order_id) REFERENCES orders(order_id), FOREIGN KEY (ISBN) REFERENCES book(ISBN))",
                    "CREATE TABLE book_author(ISBN CHAR(13), author_name CHAR(50), PRIMARY KEY (ISBN,author_name), FOREIGN KEY (ISBN) REFERENCES book(ISBN))"
                    };
                String[] table1 = {"book", "customer", "orders", "ordering","book_author"};
                int i = 0;
                for (String sql : sqlStatements){ 
                    try {
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.executeUpdate();
                        System.out.println("Table "+ table1[i] +" created successfully.");
                        i++;
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (result == 2) //delete table
            {
                String[] tables = {"book_author", "ordering", "orders", "customer", "book"};
                for (String table : tables) {
                    String sql = "drop table " + table;
                    try {
                        PreparedStatement ps = con.prepareStatement(sql);
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String input = null;
                System.out.println("Please enter the folder path");
                input = reader.readLine();
            
                for (int i = 0; i < txtList.length; i++) {
                    //System.out.println(txtList[i].substring(0, txtList[i].length() - 4));

                    String fileName = input + "/" + txtList[i];
                    File file = new File(fileName);
                    if (!file.exists()) {
                        System.out.println("File " + fileName + " does not exist.");
                        continue;
                    }
                    reader = new BufferedReader(new FileReader(file));

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
            
                        try {
                            PreparedStatement ps = con.prepareStatement(sql);
                            ps.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                reader.close();
                System.out.println("Processing...Data is loaded!");
            }

            if (result == 4) {
                // Scanner scanner = new Scanner(System.in);
                //Scanner scanner = new Scanner(new InputStreamReader(System.in));
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Please Input the date (YYYYMMDD): ");
                // String input = scanner.nextLine();
                String input = reader.readLine();
            
                if (input.length() != 8 || !input.matches("\\d{8}")) {
                    System.out.println("Invalid input! Please enter a date in the format YYYYMMDD.");
                } else {
                    int year = Integer.parseInt(input.substring(0, 4));
                    int month = Integer.parseInt(input.substring(4, 6));
                    int day = Integer.parseInt(input.substring(6, 8));
            
                    if (month < 1 || month > 12 || day < 1 || day > 31) {
                        System.out.println("Invalid date! Please check the month and day values.");
                    } else {
                        input = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6);
            
                        String sql = "SELECT * FROM orders ORDER BY o_date DESC";
                        //  FETCH FIRST 1 ROWS ONLY
                        String odate = null;
                        try {
                            PreparedStatement ps = con.prepareStatement(sql);
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
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
                //reader.close();
            }
            if(result==5)
            break; 
        }
    }
}


