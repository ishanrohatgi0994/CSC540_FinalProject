import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Nurse {
	Scanner sc = new Scanner (System.in);
	MedicalRecord mr = new MedicalRecord();
	Treatment treatment = new Treatment();
	Ward ward = new Ward();
	
	public void displayNurseOptions(Connection conn) {
		System.out.println("\n " +
				"1) Update Medical Record \n " +
				"2) Enter Treatment (Test) Details \n " +
				"3) View Managed Ward Information \n " +
				"4) View Treatment Details \n " +
				"5) View Medical Record for Patient \n");
		int choice = sc.nextInt();
		switch(choice) {
		case 1: mr.updateMedicalRecord(conn);
				break;
		case 2: treatment.addTreatment(conn);
				break;
		case 3: try {
			Ward.viewWardInformationForNurse(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		break;
		case 4: treatment.viewCurrentTreatmentDetails(conn);
				break;
		case 5: mr.viewMedicalRecordForPatient(conn);
				break;
		default: 
			System.out.println("Enter valid Input");
		}
	}
	// Add Nurse
	
	public void addNurse(Connection conn) {
		// Fetching Details to create new Nurse
		
		System.out.println("Enter Nurse name");
		String name = sc.nextLine();
		System.out.println("Enter Nurse Age");
		int age = sc.nextInt();
		System.out.println("Enter Nurse Gender : M/F");
		String gender = sc.next();
		System.out.println("Enter Nurse Phone");
		BigInteger phone = sc.nextBigInteger();
		System.out.println("Enter Nurse's Department");
		sc.nextLine();
		String dept = sc.nextLine();
		System.out.println("Enter Nurse job_title");
		String title = sc.nextLine();
		System.out.println("Enter Nurse Address");
		String address = sc.nextLine();
		System.out.println("Enter Nurse Status (Either 0 or 1)");
		int status = sc.nextInt();
		
		try {
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO Nurse (name,age,gender,phone,dept,professional_title,address,status)"+
														"VALUES (?,?,?,?,?,?,?,?)");
			stmt.setString(1, name);
			stmt.setInt(2, age);
			stmt.setString(3, gender);
			stmt.setBigDecimal(4, new BigDecimal(phone));
			stmt.setString(5, dept);
			stmt.setString(6, title);
			stmt.setString(7, address);
			stmt.setInt(8, status);
			stmt.executeUpdate(); // Insertion done
			System.out.println();
			System.out.println("Nurse Insertion Successful");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Nurse Creation Failed");
		}
	}
	
	//Update Nurse
	public void updateNurse(Connection conn) {
		System.out.println("Enter Nurse name whose details needs to be updated");
		String name = sc.nextLine();
		System.out.println("Enter the phone number");
		BigInteger phone = sc.nextBigInteger();
		
		try {
			PreparedStatement stmt=conn.prepareStatement("Select * from Nurse where name =? and phone =?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setString(1, name);
			stmt.setBigDecimal(2, new BigDecimal(phone));
			ResultSet rs = stmt.executeQuery(); 
			String ch;
			
			if (!rs.next()) {
				System.out.print("Nurse not Found");
			}
			else {
				do {
					System.out.println("Do you want to update name (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new name");
						sc.nextLine();
						String new_name = sc.nextLine();
						rs.updateString("name", new_name);
					}
					System.out.println("Do you want to update age (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new age");
						sc.nextLine();
						int new_age = sc.nextInt();
						rs.updateInt("age", new_age);
					}
					System.out.println("Do you want to update gender (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter Gender");
						sc.nextLine();
						String new_gender = sc.next();
						rs.updateString("gender", new_gender);
					}
					System.out.println("Do you want to update phone (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new phone number");
						sc.nextLine();
						BigInteger new_phone = sc.nextBigInteger();
						rs.updateBigDecimal("phone", new BigDecimal(new_phone));
					}
					System.out.println("Do you want to department name (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new department");
						sc.nextLine();
						String new_department = sc.nextLine();
						rs.updateString("dept", new_department);
					}
					System.out.println("Do you want to update professional title (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new professional title");
						sc.nextLine();
						String new_job_title = sc.nextLine();
						rs.updateString("professional_title", new_job_title);
					}
					System.out.println("Do you want to update address (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new address");
						sc.nextLine();
						String new_address = sc.nextLine();
						rs.updateString("address", new_address);
					}
					System.out.println("Do you want to update status (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new address");
						sc.nextLine();
						int status = sc.nextInt();
						rs.updateInt("status", status);
					}
				rs.updateRow();	
				} while (rs.next());
				System.out.println("Updated Nurse successful");
			}			
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Update Nurse Record unsuccessful");
		}
	}
	
	//Delete Nurse
	public void deleteNurse(Connection conn) {
		System.out.println("Enter Nurse name who needs to be deleted");
		String name = sc.nextLine();
		System.out.println("Enter the phone number");
		BigInteger phone = sc.nextBigInteger();
		
		try {
			PreparedStatement stmt=conn.prepareStatement("Delete from nurse where name =? and phone =?");
			stmt.setString(1, name);
			stmt.setBigDecimal(2, new BigDecimal(phone));
			stmt.executeUpdate(); // Will delete if nurse exists else no side effects
			System.out.println("Deleted Nurse successful");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Deletion of Nurse unsuccessful");
		}
	}

	//View all Nurses
	public void viewAllNurses(Connection conn) {
		try {
			PreparedStatement stmt=conn.prepareStatement("Select * from Nurse");
			ResultSet rs = stmt.executeQuery();
			
			if (!rs.next()) {
				System.out.println("Nurses Information not found");
			}
			else {
				System.out.print("Name \t\t Age \t Gender \t Phone \t\t Departmet \t\t JobTitle \t\t Address \n");
				do {
					System.out.println(rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t"+rs.getString(4) + "\t"+rs.getBigDecimal(5) + "\t\t"+rs.getString(6) + 
							"\t\t"+rs.getString(7) + "\t\t"+rs.getString(8));
				} while (rs.next());
			}
		}catch (Exception e) {
			System.out.println("Error in viewing Nurses");
		}
	}
	
	
}
