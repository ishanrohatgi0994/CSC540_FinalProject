import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
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
				+" 15) View Reports\n 16) view and pay bill\n");
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
					Patient.assignOrUpdateWardToPatient(conn);
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
				Ward.getCurrentWardUsageStatus(conn);
				break;
			case 16:
				try {
					Patient.viewAndPayBill(conn);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			default:
				System.out.println("Invalid Input");
		}
	}
	// Check which functionalities to implement in this class in Project Report 3
	// Option 17: View Reports will show menu with the list of reports that can be generated
	
	// Add Operator, UPDATE Operator, Delete Operator 
	public void addOperator(Connection conn) throws Exception {
		// Fetching Details to create new Operator
		
		String name, address, title, dept, gender;
		BigInteger phone;
		Integer age;

		// Interactively read each attributes
		name = Utils.readAttribute("name", "Operator", false);

		String ageString = Utils.readAttribute("age", "Operator", true);
		if(ageString.equals("")){
			age = null;
		}else {
			age = Integer.parseInt(ageString);
		}

		gender = Utils.readAttribute("gender", "Operator", true);
		if(gender.equals("")) {
			gender = null;
		}

		phone = new BigInteger(Utils.readAttribute("phone number", "Operator", false));

		dept = Utils.readAttribute("department", "Operator", true);
		if(dept.equals("")) {
			dept = null;
		}

		title = Utils.readAttribute("professional title", "Operator", true);
		if(title.equals("")){
			title = null;
		}

		address = Utils.readAttribute("address", "Operator", true);
		if(address.equals("")) {
			address = null;
		}

		try {
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO operator (name,age,gender,phone,department,job_title,address)"+
														"VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, name);
			if(age != null) {
                stmt.setInt(2, age);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
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
	
	public void updateOperator(Connection conn) throws Exception {
		String name = Utils.readAttribute("name", "Operator", false);
		System.out.println("Enter the phone number");
		BigInteger phone = new BigInteger(Utils.readAttribute("phone number", "Operator", false));
		
        String UpdateQuery = "UPDATE operator SET ";
        String[] attributes = {"name", "age", "gender", "phone", "department", "job_title", "address"};
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Build the update query interactively
        for (String attribute: attributes) {
            System.out.println("Should "+ attribute+" be updated? (y/n)");
            if (br.readLine().equals("y")) {
                String val = Utils.readAttribute(attribute, "Operator", false);
                if(attribute.equals("ssn") || attribute.equals("phone")) {
                    UpdateQuery = UpdateQuery + attribute + "="+ new BigInteger(val) + ",";
                }
                else if (attribute.equals("age")) {
                    UpdateQuery = UpdateQuery + attribute + "=" + val + ",";
                }
                else {
                    UpdateQuery = UpdateQuery + attribute + "='" + val+"',";
                }
            }
        }
        if (UpdateQuery != null && UpdateQuery.length() > 0 && UpdateQuery.charAt(UpdateQuery.length() - 1) == ',') {
        	UpdateQuery = UpdateQuery.substring(0, UpdateQuery.length() - 1);
        }
        
        UpdateQuery = UpdateQuery + " WHERE name='"+name+ "' and phone= " + phone;			
        
        try {
        	Statement stmt = conn.createStatement();
        	System.out.println(UpdateQuery);
            System.out.println("Successfully updated operator record");
		}catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Update operator Record unsuccessful");
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
			System.out.print("Name \t\t Age \t Gender \t Phone \t\t Departmet \t\t JobTitle \t\t Address \t\t Role-Type\n");
			while(rs.next()) {
				System.out.println(rs.getString(2) + "\t" + rs.getInt(3) + "\t\t"+rs.getString(4) + "\t"+rs.getBigDecimal(5) + "\t\t"+rs.getString(6) + 
								"\t\t"+rs.getString(7) + "\t\t"+rs.getString(8)+ "\t Operator");
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error in viewing operators");
		}
	}
	
	public void viewOperator(Connection conn) {
		System.out.println("Enter Operator ID");
		int o_id = sc.nextInt();
		try {
			PreparedStatement stmt=conn.prepareStatement("Select * from operator where oper_id = ?");
			stmt.setInt(1, o_id);
			ResultSet rs = stmt.executeQuery();
			System.out.print("Name \t\t Age \t Gender \t Phone \t\t Departmet \t\t JobTitle \t\t Address \t\t Role-Type\n");
			while(rs.next()) {
				System.out.println(rs.getString(2) + "\t" + rs.getInt(3) + "\t\t"+rs.getString(4) + "\t"+rs.getBigDecimal(5) + "\t\t"+rs.getString(6) + 
								"\t\t"+rs.getString(7) + "\t\t"+rs.getString(8)+ "\t Operator");
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error in viewing operators");
		}
	}
}
