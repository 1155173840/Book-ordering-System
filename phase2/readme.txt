CSCI3170 Group 14
Chen Meifang	1155173961
Chen Yingxin	1155173751
Yu Chaodi	1155173840
This project can be run on macOS and Linux, and it's not tested in other environments.

Instructions to compile: 
1. Put the files "DatabaseConnection.java", "mainsystem.java", "system.java", "bookstore.java", "Customer.java","main.java" "ojdbc7.jar" in the same folder.
2. Put the test data in a subfolder of the previous folder and remember the path to the subfolder.
3. For macOS, go to the folder in terminal using command like "cd path/to/folder". Start by the command "javac *.java", and then use the command "java -cp .:ojdbc7.jar main.java" to compile the files.
For Linux (e.g. Oracle VM VirtualBox), open the terminal in the folder. Start by the command "javac -d . main.java", and then use the command "java -cp .:ojdbc7.jar main" to compile the files.

Instructions to run: 
1. After the previous steps, you can enter the Book Ordering System and see the interface. Choose the options by inputting the option number and clicking “Enter” on your keyboard. 
2. Use "Create Table" and "Insert Table" in the system interface to build database. 
3. Remember to use "Delete Table" first if you are not using "Create Table" for the first time. 
4. Input the path of the subfolder as the folder path for "Insert Data". 
