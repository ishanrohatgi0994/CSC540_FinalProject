import java.sql.Connection;
import java.util.Scanner;

public class Admin {
	Scanner sc = new Scanner(System.in);
	Operator op = new Operator();
	Nurse nurse = new Nurse();
	Doctor doctor = new Doctor();

	public void displayAdminOptions(Connection conn) {
		System.out.println(" \n 1) Add Doctor \n 2) Add Nurse \n 3) Add Operator \n 4) Add Ward"
				+ "\n 5) Assign Nurse to ward \n 6) Update Operator \n 7)Update Nurse \n" +
				" 8) Update Doctor \n" + " 9) Delete Operator \n " + " 10) Delete Nurse \n" +
				" 11) Delete Doctor \n" + " 12) View All Operators \n" +
				" 13) View All Nurses \n" + " 14) View All Doctors \n 15) View Operator By ID\n");
		int choice = sc.nextInt();
		switch (choice) {
			case 1:
				try{
					doctor.addDoctor(conn);
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case 2: 
				try {
					nurse.addNurse(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:
				op.addOperator(conn);
				break;
			case 4:
				try{
					Ward.addWard(conn);
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case 6:
				op.updateOperator(conn);
				break;
			case 7:
				try {
					nurse.updateNurse(conn);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case 8:
				try {
					Doctor.updateDoctor(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 9:
				op.deleteOperator(conn);
				break;
			case 10:
				nurse.deleteNurse(conn);
				break;
			case 11:
				try {
					Doctor.deleteDoctor(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 12:
				op.viewAllOperators(conn);
				break;
			case 13:
				nurse.viewAllNurses(conn);
				break;
			case 14:
				doctor.viewAllDoctors(conn);
				break;
			case 15:
				op.viewOperator(conn);
				break;
			default:
				System.out.println("Invalid Option Selected");
			
		}
	}
	// Only view. Include all operations that Operator can do. Implementing Operations will be part of operator class 
}
