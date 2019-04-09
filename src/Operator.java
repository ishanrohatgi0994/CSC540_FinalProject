import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
				+ "\n 11) Delete Ward \n 12) Assign Patient to Ward \n 13) Checkout Patient \n 14) View Patient\n"
				+" 15) View Reports");
			// Check out patient involves generating Billing too
		int choice = sc.nextInt();
		switch (choice){
			case 1:
				try{
					Patient.addPatient(conn);
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case 2:
				mr.checkInPatient(conn);
				break;
			case 4:
				try {
					Doctor.updateDoctor(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 5:
				try {
					Patient.updatePatient(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 6:
				try {
					Ward.updateWard(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 7:
				mr.updateMedicalRecord(conn);
				break;
			case 9:
				try {
					Doctor.deleteDoctor(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 10:
				try {
					Patient.deletePatient(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 11:
				try {
					Ward.deleteWard(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 12:
				try {
					Patient.assignWardToPatient(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 13:
				try {
					Patient.checkoutPatient(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 14:
				try {
					Patient.viewPatientsByIDs(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 15:
				Ward.getWardUsageHistory(conn);
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
			stmt.executeUpdate(); // Insertion done
			System.out.println();
			System.out.println("Operator Insertion Successful");
		}
		catch(Exception e) {
			System.out.println("Operator Creation Failed");
		}
		
	}
	
	public void updateOperator(Connection conn) {
		System.out.println("Enter Operator name whose details needs to be updated");
		String name = sc.nextLine();
		System.out.println("Enter the phone number");
		BigInteger phone = sc.nextBigInteger();
		
		try {
			PreparedStatement stmt=conn.prepareStatement("Select * from operator where name =? and phone =?",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setString(1, name);
			stmt.setBigDecimal(2, new BigDecimal(phone));
			ResultSet rs = stmt.executeQuery(); 
			String ch;
			
			if (!rs.next()) {
				System.out.println("Operator not found");
			}
			else {
				do {
					//System.out.println(rs.getString("name"));
					System.out.println("Do you want to update name (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new name");
						sc.nextLine();
						String new_name = sc.nextLine();
						rs.updateString("name", new_name);
						System.out.println("Update Operator name successful");
					}
					System.out.println("Do you want to update age (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new age");
						int new_age = sc.nextInt();
						rs.updateInt("age", new_age);
						System.out.println("Update Operator age successful");
					}
					System.out.println("Do you want to update gender (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter Gender");
						String new_gender = sc.next();
						rs.updateString("gender", new_gender);
						System.out.println("Update Operator gender successful");
					}
					System.out.println("Do you want to update phone (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new phone number");
						BigInteger new_phone = sc.nextBigInteger();
						rs.updateBigDecimal("phone", new BigDecimal(new_phone));
						System.out.println("Update Operator phone successful");
					}
					System.out.println("Do you want to department name (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new department");
						sc.nextLine();
						String new_department = sc.nextLine();
						rs.updateString("department", new_department);
						System.out.println("Update Operator department successful");
					}
					System.out.println("Do you want to update job_title (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new job title");
						sc.nextLine();
						String new_job_title = sc.nextLine();
						rs.updateString("job_title", new_job_title);
						System.out.println("Update Operator Job Title successful");
					}
					System.out.println("Do you want to update address (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter new address");
						sc.nextLine();
						String new_address = sc.nextLine();
						rs.updateString("address", new_address);
						System.out.println("Update Operator address successful");
					}
					rs.updateRow();	
				} while (rs.next());
				System.out.println ("Updated, Thankyou!!");
			}
		}catch (Exception e) {
			System.out.println("Update Operator Record unsuccessful");
		}
	}
	
	public void deleteOperator(Connection conn) {
		System.out.println("Enter Operator name who needs to be deleted");
		String name = sc.nextLine();
		System.out.println("Enter the phone number");
		BigInteger phone = sc.nextBigInteger();
		
		try {
			PreparedStatement stmt=conn.prepareStatement("Delete from operator where name =? and phone =?");
			stmt.setString(1, name);
			stmt.setBigDecimal(2, new BigDecimal(phone));
			stmt.executeUpdate(); // Will delete if operator exists else no side effects
			System.out.println("Delete Operator successful");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Delete Operator unsuccessful");
		}
	}

	public void viewAllOperators(Connection conn) {
		try {
			PreparedStatement stmt=conn.prepareStatement("Select * from operator");
			ResultSet rs = stmt.executeQuery();
			System.out.print("Name \t\t Age \t Gender \t Phone \t\t Departmet \t\t JobTitle \t\t Address \n");
			while(rs.next()) {
				System.out.println(rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t"+rs.getString(4) + "\t"+rs.getBigDecimal(5) + "\t\t"+rs.getString(6) + 
								"\t\t"+rs.getString(7) + "\t\t"+rs.getString(8));
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error in viewing operators");
		}
		
	}
}
