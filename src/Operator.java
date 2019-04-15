import java.io.BufferedReader;
import java.io.IOException;
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
				+ "\n 1) Check -In Patient"
				+ "\n 2) Update Patient "
				+ "\n 3) Update Ward \n 4) Update Medical Record for Patient"
				+ "\n 5) Delete Patient"
				+ "\n 6) Assign/Update Patient to Ward \n 7) Checkout Patient \n 8) View Patients\n"
				+" 9) Ward Usage Status (percent usage) Report \n 10) view and pay bill \n 11) Get number of patients for date range Report\n"
				+ "12) Medical History for given time range Report \n "
				+ "13) List of patients currently treated by a  given doctor Report");
			// Check out patient involves generating Billing too
		int choice = sc.nextInt();
		switch (choice){
			case 1:
			try {
				mr.checkInPatient(conn);
			} catch (Exception e1) {
				System.out.println("Error in checking in patient");
			}
				break;
			case 2:
				try {
					Patient.updatePatient(conn);
				} catch (Exception e) {
					System.out.println("Error while update patient. Try again");
				}
				break;
			case 3:
				try {
					Ward.updateWard(conn);
				} catch (Exception e) {
					System.out.println("Error while updating ward");
				}
				break;
			case 4:
				mr.updateMedicalRecord(conn);
				break;
			case 5:
				try {
					Patient.deletePatient(conn);
				} catch (Exception e) {
					System.out.println("Error while deleting patient");
				}
				break;
			case 6:
				try {
					Patient.assignOrUpdateWardToPatient(conn);
				} catch (Exception e) {
					System.out.println("Error while assigning ward");
				}
				break;
			case 7:
				try {
					Patient.checkoutPatient(conn);
				} catch (Exception e) {
					System.out.println("Error while checking out patient");
				}
				break;
			case 8:
				try {
					Patient.viewPatientsByIDs(conn);
				} catch (Exception e) {
					System.out.println("Error while viewing Patient information");
				}
				break;
			case 9:
				Ward.getCurrentWardUsageStatus(conn);
				break;
			case 10:
				try {
					Patient.viewAndPayBill(conn);
				} catch (Exception e) {
					System.out.println("Error while generating bill");
				}
				break;
			case 11:
				try {
					MedicalRecord.getPatientPerMonth(conn);
				} catch (Exception e) {
					System.out.println("Error while getting patients");
				}
				break;
			case 12:
			try {
				Patient.viewMedicalHistory(conn);
			} catch (Exception e) {
				System.out.println("Error while medical history for patients");
			}
				break;
			case 13:
			try {
				Doctor.getAllPatientsForDoctor(conn);
			} catch (IOException e) {
				System.out.println("Error while getting patients");
			}
				break;
			default:
				System.out.println("Invalid Input");
		}
	}
	// Option 17: View Reports will show menu with the list of reports that can be generated
	
	// Add Operator - Inserts a new Operator with attributes like (Name,Age,Gender,Phone,Department, Job_title, Address)to Database.
	
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

            // Get the inserted id
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
            if(rs1.next()) {
                System.out.println("Successfully inserted operator record with ID " + rs1.getInt(1));
            } else {
                System.out.println("Successfully inserted patient record. Bud ID could not be retrieved.");
            }
		}
		catch(Exception e) {
			System.out.println("Operator Creation Failed");
		}
		
	}
	
	/* UPDATE Operator - 
		Takes the name and phone number of the operator which needs to be updated (as they uniquely determine all other attributes. 
		Operator Id can also be used)and updates the attribute by asking for corresponding new input values for attributes 
		(Name, Age,Gender,Phone,Department, Job-Title and address) that needs to be updated.
	*/
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
        	stmt.executeUpdate(UpdateQuery);
//        	System.out.println(UpdateQuery);
            System.out.println("Successfully updated operator record");
		}catch (Exception e) {
				//System.out.println(e.getMessage());
				System.out.println("Update operator Record unsuccessful");
		}
	}
	
	/*  Delete Operator - takes the name and phone number of the operator that needs to be deleted(as they uniquely determine all other attributes. 
		Operator Id can also be used)and deletes the corresponding entry of Operator from Database */
	
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
			//System.out.println(e.getMessage());
			System.out.println("Delete Operator unsuccessful");
		}
	}

	// ViewAllOperators() is used to view information of All Operator Staff members that are currently stored in Database
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
			//System.out.println(e.getMessage());
			System.out.println("Error in viewing operators");
		}
	}
	
	// viewOperator() is used to fetch details of individual operators by entering their Operator Id as input.
	// (Can be fetched by getting name and phone number as input too)
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
			//System.out.println(e.getMessage());
			System.out.println("Error in viewing operators");
		}
	}
}
