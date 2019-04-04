import java.sql.Connection;
import java.util.Scanner;

public class Nurse {
	Scanner sc = new Scanner (System.in);
	MedicalRecord mr = new MedicalRecord();
	public void displayNurseOptions(Connection conn) {
		System.out.println("\n 1) Update Medical Record \n 2) Enter Treatment (Test) Details "
				+ "\n 3) View Managed Ward Information \n 4) View Treatment Details "
				+ "\n 5) View Medical Record for Patient");
		int choice = sc.nextInt();
		switch(choice) {
		case 5:
			mr.viewMedicalRecordForPatient(conn);
		}
	}
	// Add Nurse, Delete Nurse, Update Nurse
	// Check which functionalities to implement in this class in Project Report 3
}
