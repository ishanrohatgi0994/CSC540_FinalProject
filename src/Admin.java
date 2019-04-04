import java.sql.Connection;
import java.util.Scanner;

public class Admin {
	Scanner sc = new Scanner(System.in);
	Operator op = new Operator();

	public void displayAdminOptions(Connection conn) {
		System.out.println(" \n 1) Add Doctor \n 2) Add Nurse \n 3) Add Operator \n 4) Add Ward"
				+ "\n 5) Assign Nurse to ward \n 6) Update Operator \n 7) Delete Operator \n 8) View Reports");
		int choice = sc.nextInt();
		switch (choice) {
			case 3:
				op.addOperator(conn);
				break;
			case 6:
				op.updateOperator(conn);
				break;
			case 7:
				op.deleteOperator(conn);
			case 8:
				op.viewAllOperators(conn);
			
		}
	}
	// Only view. Include all operations that Operator can do. Implementing Operations will be part of operator class 
}
