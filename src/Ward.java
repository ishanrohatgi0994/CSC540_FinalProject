import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;
import java.sql.*;


public class Ward {

	public static void viewWardInformationForNurse(Connection conn) throws IOException {
		// TODO Auto-generated method stub
		int nurseID;
		do{
			String nurseIDString = Utils.readAttribute("nurse ID", "Ward", false);
			nurseID = Integer.parseInt(nurseIDString);

			try{
				PreparedStatement stmt=conn.prepareStatement("Select * from nurse WHERE nurse_id = " + nurseID);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()){
					System.out.println("Nurse Exists\n");
				}
				else{
					System.out.println("Nurse ID Not Found\n");
					nurseID = 0;
				}

			}
			catch (SQLException e){
				System.out.println("Nurse Does not exist\n");
			}
		}while (nurseID == 0);

		try{
			PreparedStatement stmt=conn.prepareStatement("Select * from ward WHERE nurse_id = " + nurseID);
			ResultSet rs = stmt.executeQuery();
			System.out.print("Ward ID \t\t Total Capacity \t\t Current Availability \t\t Ward Bed Type\n");
			while(rs.next()){
				System.out.println(rs.getInt(1)+"\t\t"+rs.getInt(2)+"\t\t"+rs.getInt(3)
						+"\t\t"+rs.getInt(4));
			}
		}
		catch (SQLException e){
			System.out.println("Nurse Does not exist\n");
		}

	}

	//Ishan Implementation
	// Add Ward
	public static void addWard(Connection conn) throws IOException {
		// initialize required attributes
		Integer totalCapacity, wardType, nurseID;

		String totalCapacityString = Utils.readAttribute("total capacity", "Ward", false);
		totalCapacity = Integer.parseInt(totalCapacityString);

		// Check if ward bed type is valid
		do {
			String wardTypeString = Utils.readAttribute("bed type (1, 2, 3, 4)", "Ward", false);
			wardType = Integer.parseInt(wardTypeString);

			if ((wardType == 1) || (wardType == 2) || (wardType == 3) || (wardType == 4)){
				System.out.print("Ward Bed Type found\n");
			}
			else{
				System.out.println("Ward Bed type can be 1, 2, 3 or 4");
				wardType = 0;
			}
		}while (wardType == 0);

		//Check if Nurse ID exists
		do{
			String nurseIDString = Utils.readAttribute("nurse ID", "Ward", false);
			nurseID = Integer.parseInt(nurseIDString);

			try{
				PreparedStatement stmt=conn.prepareStatement("Select * from nurse WHERE nurse_id = " + nurseID);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()){
					System.out.println("Nurse Exists\n");
				}
				else{
					System.out.println("Nurse ID Not Found\n");
					nurseID = 0;
				}

			}
			catch (SQLException e){
				System.out.println("Nurse Does not exist\n");
			}
		}while (nurseID == 0);



		// execute the statement
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO ward (total_capacity, current_availability, ward_type, nurse_id)"+
					" VALUES (?, ?, ?, ?)");

			ps.setInt(1, totalCapacity);
			ps.setInt(2, totalCapacity);
			ps.setInt(3, wardType);
			ps.setInt(4, nurseID);

			ps.executeUpdate();

			// Get the inserted id
			ResultSet rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
			if(rs.next()) {
				System.out.println("Successfully inserted ward record with ID " + rs.getInt(1));
			} else {
				System.out.println("Successfully inserted ward record. Bud ID could not be retrieved.");
			}
		} catch (SQLException e) {
			System.out.println("Failed to insert ward");
			e.printStackTrace();
		}

	}

	// update a ward by interactively getting the ID of the ward.
	public static void updateWard(Connection conn) throws Exception{
		int ID;
		ID = Integer.parseInt(Utils.readAttribute("ID", "Ward", false));
		String UpdateQuery = "UPDATE ward SET ";
		String[] attributes = {"total_capacity", "ward_type", "nurse_id"};
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// Build the update query interactively
		for (String attribute: attributes) {
			System.out.println("Should "+ attribute+" be updated? (y/n)");
			if (br.readLine().equals("y")) {

				if (attribute.equals("total_capacity")) {

					try{
						PreparedStatement stmt=conn.prepareStatement("Select current_availability from ward WHERE ward_id = " + ID);
						ResultSet rs = stmt.executeQuery();
						rs.next();
						int currentAvailability = rs.getInt(1);

						String val = "";
						int newTotal = 0;
						do{
							val = Utils.readAttribute(attribute, "Ward", false);
							newTotal = Integer.parseInt(val);
							if (newTotal < currentAvailability){
								System.out.println("New Total Availability cannot be less than current availability: " + currentAvailability +"\n");
							}
						}while(newTotal < currentAvailability);

						UpdateQuery = UpdateQuery + attribute + "=" + val + ", ";

					}
					catch (SQLException e){
						System.out.println("Ward current availability does not exist\n");
					}
				}

				else if (attribute.equals("ward_type")) {
					int wardType = 0;
					String val = "";
					do {
						val = Utils.readAttribute("ward bed type (1,2,3,4)", "Ward", false);
						wardType = Integer.parseInt(val);

						if ((wardType == 1) || (wardType == 2) || (wardType == 3) || (wardType == 4)){
							System.out.print("Ward Bed Type found\n");
						}
						else{
							System.out.println("Ward Bed type can be 1, 2, 3 or 4");
							wardType = 0;
						}
					}while (wardType == 0);
					UpdateQuery = UpdateQuery + attribute + "=" + val + ", ";
				}

				else if (attribute.equals("nurse_id")) {
					int nurseID = 0;
					String val = "";
					do{
						val = Utils.readAttribute("nurse ID", "Ward", false);
						nurseID = Integer.parseInt(val);

						try{
							PreparedStatement stmt=conn.prepareStatement("Select * from nurse WHERE nurse_id = " + nurseID);
							ResultSet rs = stmt.executeQuery();
							if (rs.next()){
								System.out.println("Nurse Exists\n");
							}
							else{
								System.out.println("Nurse ID Not Found\n");
								nurseID = 0;
							}

						}
						catch (SQLException e){
							System.out.println("Nurse Does not exist\n");
						}
					}while (nurseID == 0);

					UpdateQuery = UpdateQuery + attribute + "=" + val + " ";
				}
			}
		}
		UpdateQuery = UpdateQuery + "WHERE ward_id ="+ID;
		System.out.println(UpdateQuery);

		//Execute the query
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(UpdateQuery);
			System.out.println("Successfully updated ward record");
		} catch(SQLException e) {
			System.out.println("Error while updating ward record");
			System.out.println(e);
		}
	}

	public static void deleteWard(Connection conn)throws Exception{
		int ID;
		ID = Integer.parseInt(Utils.readAttribute("ID", "Ward", false));
		System.out.println("Are you sure you want to delete the ward with id "+ ID+"? (y/n)");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if(br.readLine().equals("y")) {
			Statement stmt = conn.createStatement();
			String UpdateQuery = "DELETE FROM ward where ward_id = "+ID;
			try {
				stmt.executeUpdate(UpdateQuery);
				System.out.println("Successfully deleted the ward");
			}catch (SQLException e) {
				System.out.println("Failed to delete the ward with ID: "+ID);
			}
		}
	}

}
