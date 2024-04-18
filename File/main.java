import java.util.*;
import java.io.*;

public class main {
    public static void main(String[] args) throws IOException {
        mainsystem mainsystem = new mainsystem();
        while(true)
        {
            
            int result = mainsystem.mainInterface();
            if(result==1)
            system.system_main();
            if(result==2) 
            Customer.customer_main(); 
            if(result==3) 
            bookstore.bookstore_main();
            if(result==4)
            System.out.println("The System Date is now: " + mainsystem.systemdate);
            if(result==5)
            break;
        }
    }

}   

    





