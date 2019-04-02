import java.sql.Connection;
import java.util.Scanner;

public class Operator {
	Scanner sc = new Scanner(System.in);
	MedicalRecord mr = new MedicalRecord();
	public void displayOperatorOptions(Connection conn) {
		System.out.println("\n"
				+ "\n 1) Add Patient"
				+ "\n 2) Add Medical Record(Check -In) for a Patient"
				+ "\n 3) Update Nurse \n 4) Update Doctor \n 5) Update Patient "
				+ "\n 6) Update Ward \n 7) Update Medical Record"
				+ "\n 8) Delete Nurse \n 9) Delete Doctor \n 10) Delete Patient"
				+ "\n 11) Delete Ward \n 12) Assign Patient to Ward \n 13) Checkout Patient \n 14) View Reports");
			// Check out patient involves generating Billing too
		int choice = sc.nextInt();
		switch (choice){
			case 2:
				mr.checkInPatient(conn);
				break;
			case 7:
				mr.updateMedicalRecord(conn);
				break;
			default:
				System.out.println("Invalid Input");
		}
	}
	// Check which functionalities to implement in this class in Project Report 3
	// Option 17: View Reports will show menu with the list of reports that can be generated
	
	// Harsh Implement
	// Add Operator, UPDATE Operator, Delete Operator 
	public void addOperator(Connection conn) {
		
	}
	public void updateOperator(Connection conn) {
		
	}
	public void deleteOperator(Connection conn) {
		
	}
}
