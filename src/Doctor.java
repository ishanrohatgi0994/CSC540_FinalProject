import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.Scanner;

public class Doctor {
	/*
    Required attributes:
    name, phone, status
     */

	public static int STATUS_NOT_WORKING = 0;
	public static int STATUS_WORKING = 1;

	Treatment treatment = new Treatment();
	
	public void displayDoctorOptions(Connection conn) {
		System.out.println("\n 1) View Patient's Medical History(for given data range) \n 2) View Ward Information "
				+ "\n 3) View Current Treatment Details \n 4) Add Treatment"
				+ "\n 5) Update Treatment"
				+ "\n 6) Update Medical Record");
		Scanner sc = new Scanner(System.in);

		int choice = sc.nextInt();
		switch(choice) {
			case 1:
				try{
					Patient.viewMedicalHistory(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 5: 
				treatment.updateTreatment(conn);
				break;
			default:
				System.out.println("Invalid choice");
			}
		}
	// Check which functionalities to implement in this class in Project Report 3
	// Custom Implementation: View Ward Info will be showing the ward information and nurse name and contact details



	//Ishan Implementation
	// Add Doctor
	public static void addDoctor(Connection conn) throws IOException {
		// initialize required attributes
		String name, address, professional_title, dept, gender;
		BigInteger phone;
		Integer age;

		// Interactively read each attributes
		name = Utils.readAttribute("name", "Doctor", false);

		String ageString = Utils.readAttribute("age", "Doctor", true);
		if(ageString.equals("")){
			age = null;
		}else {
			age = Integer.parseInt(ageString);
		}

		gender = Utils.readAttribute("gender", "Doctor", true);
		if(gender.equals("")) {
			gender = null;
		}

		phone = new BigInteger(Utils.readAttribute("phone number", "Doctor", false));

		dept = Utils.readAttribute("department", "Doctor", true);
		if(dept.equals("")) {
			dept = null;
		}

		professional_title = Utils.readAttribute("professional title", "Doctor", true);
		if(professional_title.equals("")){
			professional_title = null;
		}

		address = Utils.readAttribute("address", "Doctor", true);
		if(address.equals("")) {
			address = null;
		}

		int current_status = Integer.parseInt(Utils.readAttribute("current status ", "Doctor", false));
		while(current_status != STATUS_NOT_WORKING && current_status != STATUS_WORKING) {
			System.out.println("Invalid status "+current_status+" entered. Try again.");
			current_status = Integer.parseInt(Utils.readAttribute("current status ", "Patient", false));
		}

		// execute the statement
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO doctor (name, age, gender, phone, dept, professional_title, address, status)"+
					" VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, name);

			if(age != null) {
				ps.setInt(2, age);
			} else {
				ps.setNull(2, Types.INTEGER);
			}

			ps.setString(3, gender);

			ps.setBigDecimal(4, new BigDecimal(phone));

			ps.setString(5, dept);
			ps.setString(6, professional_title);
			ps.setString(7, address);
			ps.setInt(8, current_status);

			ps.executeUpdate();

			// Get the inserted id
			ResultSet rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
			if(rs.next()) {
				System.out.println("Successfully inserted doctor record with ID " + rs.getInt(1));
			} else {
				System.out.println("Successfully inserted doctor record. Bud ID could not be retrieved.");
			}
		} catch (SQLException e) {
			System.out.println("Failed to insert doctor");
			e.printStackTrace();
		}

	}

	// update a doctor by interactively getting the ID of the doctor.
	public static void updateDoctor(Connection conn) throws Exception{
		int ID;
		ID = Integer.parseInt(Utils.readAttribute("ID", "Doctor", false));
		String UpdateQuery = "UPDATE doctor SET ";
		String[] attributes = {"name", "age", "gender", "phone", "department", "professional_title", "address", "status"};
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// Build the update query interactively
		for (String attribute: attributes) {
			System.out.println("Should "+ attribute+" be updated? (y/n)");
			if (br.readLine().equals("y")) {
				String val = Utils.readAttribute(attribute, "Doctor", false);
				if(attribute.equals("phone")) {
					UpdateQuery = UpdateQuery + attribute + "="+ new BigInteger(val) + ", ";
				}
				else if (attribute.equals("age")) {
					UpdateQuery = UpdateQuery + attribute + "=" + val + ", ";
				}
				else if (attribute.equals("status")) {
					UpdateQuery = UpdateQuery + attribute + "=" + val + " ";
				}
				else {
					UpdateQuery = UpdateQuery + attribute + "='" + val+"', ";
				}
			}
		}
		UpdateQuery = UpdateQuery + "WHERE doc_id ="+ID;
		System.out.println(UpdateQuery);
		//Execute the query
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(UpdateQuery);
			System.out.println("Successfully updated doctor record");
		} catch(SQLException e) {
			System.out.println("Error while updating doctor record");
			System.out.println(e);
		}
	}

	// soft delete of the doctor
	public static void deleteDoctor(Connection conn) throws Exception {
		int ID;
		ID = Integer.parseInt(Utils.readAttribute("ID", "Doctor", false));
		System.out.println("Are you sure you want to delete the doctor with id "+ ID+"? (y/n)");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if(br.readLine().equals("y")) {
			Statement stmt = conn.createStatement();
			String UpdateQuery = "UPDATE doctor SET status = "+STATUS_NOT_WORKING+" where doc_id = "+ID;
			try {
				stmt.executeUpdate(UpdateQuery);
				System.out.println("Successfully deleted the doctor");
			}catch (SQLException e) {
				System.out.println("Failed to delete the doctor "+ID);
			}
		}
	}

	// View all Doctors
	public void viewAllDoctors(Connection conn) {
		try {
			PreparedStatement stmt=conn.prepareStatement("Select * from doctor");
			ResultSet rs = stmt.executeQuery();
			System.out.print("Name \t\t Age \t Gender \t Phone \t\t Departmet \t\t Professional Title \t\t Address \t\t Status\n");
			while(rs.next()) {
				System.out.println(rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t"+rs.getString(4) + "\t"+rs.getBigDecimal(5) + "\t\t"+rs.getString(6) +
						"\t\t"+rs.getString(7) + "\t\t"+rs.getString(8) + "\t\t"+rs.getInt(9));
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error in viewing Doctors");
		}
	}

	//Get Doctor By List of ID's
	public void viewDoctorsByIds(Connection conn, int[] doctorIDs) {

		try {
			String stringDoctorIDs = "(";
			for(int i=0; i < doctorIDs.length; i++){
				stringDoctorIDs = stringDoctorIDs + String.valueOf(doctorIDs[i]) + ",";
			}
			stringDoctorIDs = stringDoctorIDs.substring(0, stringDoctorIDs.length() - 1);
			stringDoctorIDs += ")";

			PreparedStatement stmt=conn.prepareStatement("Select * from doctor WHERE doc_id IN" + stringDoctorIDs);
			ResultSet rs = stmt.executeQuery();
			System.out.print("Name \t\t Age \t Gender \t Phone \t\t Departmet \t\t Professional Title \t\t Address \t\t Status\n");
			while(rs.next()) {
				System.out.println(rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t"+rs.getString(4) + "\t"+rs.getBigDecimal(5) + "\t\t"+rs.getString(6) +
						"\t\t"+rs.getString(7) + "\t\t"+rs.getString(8) + "\t\t"+rs.getInt(9));
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error in viewing Doctors");
		}
	}
}
