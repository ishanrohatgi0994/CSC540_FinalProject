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

public class Nurse {
	Scanner sc = new Scanner (System.in);
	MedicalRecord mr = new MedicalRecord();
	Treatment treatment = new Treatment();
	Ward ward = new Ward();
	
	public void displayNurseOptions(Connection conn) {
		//Function used to display the actions that can be performed by Nurse
		System.out.println("\n " +
				"1) Update Medical Record for Patient \n " +
				"2) Add Treatment (Test) Details for Patient \n " +
				"3) View Managed Ward Information for Nurse\n " +
				"4) View Current Treatment Details for Patient\n " +
				"5) View Medical Record for Patient \n" +
				"6) Delete Treatment Details of a Patient \n" +
				"7) Update treatment details for a patient \n" +
				"8) View Medical Record History for Patient\n");
		int choice = sc.nextInt();
		switch(choice) {
		case 1: mr.updateMedicalRecord(conn);
				break;
		case 2: treatment.addTreatment(conn);
				break;
		case 3: try {
			Ward.viewWardInformationForNurse(conn);
		} catch (Exception e) {
			System.out.println("Error while fetching ward information for nurse");
			//e.printStackTrace();
		}
		break;
		case 4: treatment.viewCurrentTreatmentDetails(conn);
				break;
		case 5: mr.viewMedicalRecordForPatient(conn);
				break;
		case 6: treatment.deleteTreatment(conn);
				break;
		case 7:
			treatment.updateTreatment(conn);
			break;
		case 8:
			try {
				Patient.viewMedicalHistory(conn);
			} catch (Exception e) {
				System.out.println("Error while fetching medical history of patient");
			}
		default: 
			System.out.println("Enter valid Input");
		}
	}
	
	//Add Nurse
	public void addNurse(Connection conn) throws Exception{
		// Getting details associated with the Nurse and creating a new Nurse record in DB.
		
		String name, address, gender, dept;
        BigInteger phone;
        Integer age;
        
        //Get Name of the Nurse.
        name = Utils.readAttribute("name", "Nurse", false);
        
        //Get age of the Nurse.
        String ageString = Utils.readAttribute("age", "Nurse", true);
        if(ageString.equals("")){
            age = null;
        }else {
            age = Integer.parseInt(ageString);
        }
        
        //Get Gender of Nurse.
        gender = Utils.readAttribute("Gender", "Nurse", true);
        if(gender.equals("")){
            gender = null;
        }
        //Get Phone Number of Nurse
        phone = new BigInteger(Utils.readAttribute("phone number", "Nurse", false));
        
        //Get Department of Nurse
        dept = Utils.readAttribute("department", "Nurse", true);
        
        //Get Address of Nurse
        address = Utils.readAttribute("address", "Nurse", true);
        
        if(address.equals("")) {
            address = null;
        }        
        //Get Professional Title of Nurse
        String title = Utils.readAttribute("professional title", "Nurse", true);
        if(title.equals("")){
            title = null;
        }
		int status = 1; //Setting the Status of Nurse as 1 indicating that the nurse is a staff of hospital.
		//Setting the Status of Nurse as 0 indicates that the nurse is no longer staff of hospital.
		
		try {

			// check if the nurse with the same name and phone number exists
			String selectNurseBeforeInsert = "SELECT * from nurse where name='"+name+"' AND phone="+phone;
			Statement s = conn.createStatement();
			ResultSet r = s.executeQuery(selectNurseBeforeInsert);
			if (r.next()) {
				if (r.getInt("status") == 0) {
					// if there is an entry with the same name and phone number, update the current status to set to the currently inputted status.
					String updateNurseStatus = "UPDATE nurse SET status=" + 1 + " WHERE name='" + name + "' AND phone=" + phone;
					Statement updateNurse = conn.createStatement();
					updateNurse.executeUpdate(updateNurseStatus);
					System.out.println("Returning Nurse. Updated the status of the nurse");
					return;
				} else {
					System.out.println("Nurse already exists. Cannot insert nurse");
					return;
				}
			}
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO nurse (name,age,gender,phone,dept,professional_title,address,status)"+
														"VALUES (?,?,?,?,?,?,?,?)");
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
			stmt.setInt(8, status);
			stmt.executeUpdate(); // Insertion done
			System.out.println();

			// select last insert ID
			// Get the inserted id
			ResultSet rs1 = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
			if(rs1.next()) {
				System.out.println("Successfully inserted nurse record with ID " + rs1.getInt(1));
			} else {
				System.out.println("Successfully inserted nurse record. Bud ID could not be retrieved.");
			}
		}
		catch(Exception e) {
//			System.out.println(e.getMessage());
			System.out.println("Nurse Creation Failed");
		}
	}
	
	//Update Nurse
	public void updateNurse(Connection conn) throws Exception {
		//Function used to update the details of the Nurse.
		//Get Phone Number and Name of the Nurse whose details has to be updated as the combination of Nurse's name
		//and Nurse's phone number are unique.
		//Nurse's ID also can be used to identify unique Nurse.
		String name = Utils.readAttribute("name", "Nurse", false);
		System.out.println("Enter the phone number");
		BigInteger phone = new BigInteger(Utils.readAttribute("phone number", "Nurse", false));
		
        String UpdateQuery = "UPDATE nurse SET ";
        String[] attributes = {"name", "age", "gender", "phone", "dept", "professional_title", "address"};
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Build the update query interactively
        for (String attribute: attributes) {
            System.out.println("Should "+ attribute+" be updated? (y/n)");
            if (br.readLine().equals("y")) {
                String val = Utils.readAttribute(attribute, "Nurse", false);
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
            System.out.println("Successfully updated nurse record");
		}catch (Exception e) {
				// System.out.println(e.getMessage());
				System.out.println("Update Nurse Record unsuccessful");
		}
	}
	
	//Delete Nurse
	public void deleteNurse(Connection conn) {
		//Function used to delete the details of the Nurse.
		//Get Phone Number and Name of the Nurse whose details has to be updated as the combination of Nurse's name
		//and Nurse's phone number are unique.
		//Nurse's ID also can be used to identify unique Nurse.
		System.out.println("Enter Nurse name who needs to be deleted");
		String name = sc.nextLine();
		System.out.println("Enter the phone number");
		BigInteger phone = sc.nextBigInteger();
		
		try {
			PreparedStatement stmt=conn.prepareStatement("update nurse set status = ? where name =? and phone =?");
			stmt.setInt(1, 0);//Setting the Status of the Nurse as 0 which indicates that the nurse is out of hospital.
			stmt.setString(2, name);
			stmt.setBigDecimal(3, new BigDecimal(phone));
			stmt.executeUpdate(); // Will delete if nurse exists else no side effects
			System.out.println("Deleted Nurse successful");
		}
		catch(Exception e) {
			//System.out.println(e.getMessage());
			System.out.println("Deletion of Nurse unsuccessful");
		}
	}

	//View all nurses
	public void viewAllNurses(Connection conn) {
		try {
			PreparedStatement stmt=conn.prepareStatement("Select * from nurse where status = 1");
			ResultSet rs = stmt.executeQuery();
			
			if (!rs.next()) {
				System.out.println("Nurses Information not found");
			}
			else {
				System.out.print("Name \t\t Age \t Gender \t Phone \t\t Departmet \t\t JobTitle \t\t Address \t\t Role-Type \n");
				do {
					System.out.println(rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t"+rs.getString(4) + "\t"+rs.getBigDecimal(5) + "\t\t"+rs.getString(6) + 
							"\t\t"+rs.getString(7) + "\t\t"+rs.getString(8) + " \t\t Nurse" );
				} while (rs.next());
			}
		}catch (Exception e) {
			System.out.println("Error in viewing Nurses");
		}
	}
}
