import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
		// Fetching Details to create new Operator
		
		System.out.println("Enter Operator name");
		String name = sc.nextLine();
		System.out.println("Enter Operator Age");
		int age = sc.nextInt();
		System.out.println("Enter Operator Gender : M/F");
		String gender = sc.next();
		System.out.println("Enter Operator Phone");
		BigInteger phone = sc.nextBigInteger();
		System.out.println("Enter Operator's Department");
		sc.nextLine();
		String dept = sc.nextLine();
		System.out.println("Enter Operator job_title");
		String title = sc.nextLine();
		System.out.println("Enter Operator Address");
		String address = sc.nextLine();
		try {
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO operator (name,age,gender,phone,department,job_title,address)"+
														"VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, name);
			stmt.setInt(2, age);
			stmt.setString(3, gender);
			stmt.setBigDecimal(4, new BigDecimal(phone));
			stmt.setString(5, dept);
			stmt.setString(6, title);
			stmt.setString(7, address);
			stmt.executeUpdate();
			System.out.println();
			System.out.println("Operator Insertion Successful");
		}
		catch(Exception e) {
			System.out.println("Operator Creation Failed");
		}
		
	}
	public void updateOperator(Connection conn) {
		
		
	}
	public void deleteOperator(Connection conn) {
		
	}
}
