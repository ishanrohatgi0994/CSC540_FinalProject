import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {
	Scanner sc = new Scanner(System.in);
	Operator op = new Operator();
	Nurse nurse = new Nurse();
	Doctor doctor = new Doctor();

	public void displayAdminOptions(Connection conn) {
		System.out.println(" \n 1) Add Doctor \n 2) Add Nurse \n 3) Add Operator \n 4) Add Ward"
				+ "\n 5)Update Ward \n 6) Update Operator \n 7)Update Nurse \n" +
				" 8) Update Doctor \n" + " 9) Delete Operator \n " + " 10) Delete Nurse \n" +
				" 11) Delete Doctor \n" + "12) Delete Ward \n" + " 13) View All Operators \n" +
				" 14) View All Nurses \n" + " 15) View All Doctors \n 16) View Operator By ID" + "\n"
				+ "17) Add Patient \n 18) Update Patient \n 19) Delete Patient \n 20) View Patient \n"
				+ "21) View All Staff By Role Report\n 22) Ward Usage Status (percent usage) Report \n 23) view and pay bill \n "
				+ "24) Get number of patients for date range Report \n"
				+ "25) Medical History for given time range Report \n " 
				+ "26) List of patients currently treated by a  given doctor Report \n");
		int choice = sc.nextInt();
		switch (choice) {
			case 1:
				try{
					doctor.addDoctor(conn);
				} catch(Exception e) {
					System.out.println("Error occured while performing operation");
				}
				break;
			case 2: 
				try {
					nurse.addNurse(conn);
				} catch (Exception e) {
					System.out.println("Error occured while performing operation");
				}
				break;
			case 3:
				try {
					op.addOperator(conn);
				} catch (Exception e) {
					System.out.println("Error occured while performing operation");
				}
					break;
			case 4:
				try{
					Ward.addWard(conn);
				} catch(Exception e) {
					System.out.println("Error occured while performing operation");
				}
				break;
			case 5:
			try {
				Ward.updateWard(conn);
			} catch (Exception e1) {
				System.out.println("Error while updating ward");
			}
				break;
			case 6:
				try {
					op.updateOperator(conn);
				} catch (Exception e2) {
					System.out.println("Error occured while performing operation");
				}
					break;
			case 7:
				try {
					nurse.updateNurse(conn);
				} catch (Exception e) {
					System.out.println("Error occured while performing operation");
				}
				break;
			case 8:
				try {
					Doctor.updateDoctor(conn);
				} catch (Exception e) {
					System.out.println("Error occured while performing operation");
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
					System.out.println("Error occured while performing operation");
				}
				break;
			case 12:
			try {
				Ward.deleteWard(conn);
			} catch (Exception e2) {
				System.out.println("Error in ward deletion");
			}
				break;
			case 13:
				op.viewAllOperators(conn);
				break;
			case 14:
				nurse.viewAllNurses(conn);
				break;
			case 15:
				doctor.viewAllDoctors(conn);
				break;
			case 16:
				op.viewOperator(conn);
				break;
			case 17:
			try {
				Patient.addPatient(conn);
			} catch (Exception e1) {
				System.out.println("Add patient unsuccessful");
			}
				break;
			case 18:
				try {
					Patient.updatePatient(conn);
				} catch (Exception e1) {
					System.out.println("Update patient unsuccessful");
				}
					break;
			case 19:
				try {
					Patient.deletePatient(conn);
				} catch (Exception e1) {
					System.out.println("Delete patient unsuccessful");
				}
					break;
			case 20:
				try {
					Patient.viewPatientsByIDs(conn);
				} catch (Exception e1) {
					System.out.println("View patient unsuccessful");
				}
					break;
						
			case 21: try {
				viewAllStaffByRole(conn);
			} catch (Exception e) {
				System.out.println("Error while performing operation");
			}
				break;
				
			case 22:
				Ward.getCurrentWardUsageStatus(conn);
				break;
				
			case 23:
				try {
					Patient.viewAndPayBill(conn);
				} catch (Exception e) {
					System.out.println("Error while generating bill");
				}
				break;
				
			case 24:
				try {
					MedicalRecord.getPatientPerMonth(conn);
				} catch (Exception e) {
					System.out.println("Error while getting patients");
				}
				break;
				
			case 25:
			try {
				Patient.viewMedicalHistory(conn);
			} catch (Exception e) {
				System.out.println("Error while medical history for patients");
			}
				break;
			case 26:
			try {
				Doctor.getAllPatientsForDoctor(conn);
			} catch (IOException e) {
				System.out.println("Error while getting patients");
			}
				break;
				
			default:
				System.out.println("Invalid Option Selected");
			
		}
	}
	// Only view. Include all operations that Operator can do. Implementing Operations will be part of operator class
	
	public void viewAllStaffByRole(Connection conn) throws Exception{
		//View all Staff information grouped by their Role
		String query = "select * from (select name, age, gender, phone, dept, professional_title, address,status, 'Nurse' as Role from nurse where nurse.status <> 0 \r\n" + 
				"UNION select name, age, gender, phone, dept, professional_title, address,status, 'Doctor' as Role from doctor where doctor.status <> 0\r\n" + 
				"UNION select name, age, gender, phone, department, job_title, address, null as status, 'Operator' as Role from operator) \r\n" + 
				"AS StaffTable order by Role;" ; 
				
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			if(rs.getInt(8) == 0){ //Checking if the status attribute is null, this case is for operator who does not have a status column          
				System.out.println(rs.getString(1) + "\t\t\t" + rs.getInt(2) + "\t\t"+rs.getString(3) + "\t"+rs.getBigDecimal(4) + "\t\t"+rs.getString(5) +
						"\t\t"+rs.getString(6) + "\t\t"+rs.getString(7) + "\t\t"+ " N/A" + " \t\t Doctor");
			}
			else {
				System.out.println(rs.getString(1) + "\t\t\t" + rs.getInt(2) + "\t\t"+rs.getString(3) + "\t"+rs.getBigDecimal(4) + "\t\t"+rs.getString(5) +
				"\t\t"+rs.getString(6) + "\t\t"+rs.getString(7) + "\t\t"+rs.getInt(8) + " \t\t Doctor");
			}
		}
	}
}
