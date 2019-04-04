import java.sql.Connection;

public class Doctor {

	public void displayDoctorOptions(Connection conn) {
		System.out.println("\n 1) View Patient's Medical History(for given data range) \n 2) View Ward Information "
				+ "\n 3) View Current Treatment Details \n 4) Add Treatment"
				+ "\n 5) Update Treatment"
				+ "\n 6) Update Medical Record");
		
	}
	// Check which functionalities to implement in this class in Project Report 3
	// Custom Implementation: View Ward Info will be showing the ward information and nurse name and contact details
}
