import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;
import java.sql.*;


public class Ward {

	/*View ward information that is assigned to a particular nurse. If the nurse ID is not found the
	* system iteratively asks for nurse id till it finds a valid match.*/
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

	/*This function is used to siplay ward information to a doctor. A doctor may need to know the current ward
	* availability, the corresponding nurse id who manages the ward. Hence we do a join on the ward table field nurse_id
	* and nurse table field nurse_id to display ward information and nurse information for each ward in the database. */
	public static void viewWardInformationForDoctor(Connection conn){

		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT ward_id, ward.nurse_id, nurse.name, nurse.phone FROM ward join nurse on ward.nurse_id=nurse.nurse_id ;");
			ResultSet rs = stmt.executeQuery();

			System.out.print("Ward ID \t\t Nurse ID \t\t Nurse Name \t\t Nurse Phone\n");
			while(rs.next()){
				System.out.println(rs.getInt(1)+"\t\t"+rs.getInt(2)+"\t\t"+rs.getString(3)
						+"\t\t"+rs.getBigDecimal(4));
			}

		} catch (SQLException e) {
			System.out.println("Ward Information could not be recieved");
		}

	}

	/* Add Ward information by iteratively asking for each field. If the ward type entered is invalid then system
	* iteratively asks till valid bed type is not entered. Initially on adding a ward, the current availability of
	* ward is set to the same number as total capacity as it;s a new ward being added and currently has no patients
	* in it.*/

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

		//Check if Nurse ID exists. If blank set nurseID to -1 which we will check for later and assign NULL in DB.
		do{
			String nurseIDString = Utils.readAttribute("nurse ID", "Ward", true);
			if(nurseIDString.length() != 0){
				nurseID = Integer.parseInt(nurseIDString);

				try{
					PreparedStatement stmt=conn.prepareStatement("Select * from nurse WHERE nurse_id = " + nurseID +" and status = 1;");
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
			}
			else{
				nurseID = -1;
			}
		}while (nurseID == 0);



		// execute the statement
		try {
			if (nurseID != -1){
				PreparedStatement ps = conn.prepareStatement("INSERT INTO ward (total_capacity, current_availability, ward_type, nurse_id)"+
						" VALUES (?, ?, ?, ?)");

				ps.setInt(1, totalCapacity);
				ps.setInt(2, totalCapacity);
				ps.setInt(3, wardType);
				ps.setInt(4, nurseID);

				ps.executeUpdate();
			}
			else{
				PreparedStatement ps = conn.prepareStatement("INSERT INTO ward (total_capacity, current_availability, ward_type)"+
						" VALUES (?, ?, ?)");

				ps.setInt(1, totalCapacity);
				ps.setInt(2, totalCapacity);
				ps.setInt(3, wardType);

				ps.executeUpdate();
			}

			// Get the inserted id
			ResultSet rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
			if(rs.next()) {
				System.out.println("Successfully inserted ward record with ID " + rs.getInt(1));
			} else {
				System.out.println("Successfully inserted ward record. Bud ID could not be retrieved.");
			}
		} catch (SQLException e) {
			System.out.println("Failed to insert ward");
		}

	}

	/* Update a ward by interactively getting the ID of the ward. While updating a ward the total availability cannot
	* be set to a number which is less than the current availability of the ward. */
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

				//Ensure new total availability is not less than current availability
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

						UpdateQuery = UpdateQuery + attribute + "=" + val + ",";

					}
					catch (SQLException e){
						System.out.println("Ward current availability does not exist\n");
					}
				}

				//Ensure ward bed-type added is correct
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
					UpdateQuery = UpdateQuery + attribute + "=" + val + ",";
				}

				// Ensure nurseID being updated is present in the database
				else if (attribute.equals("nurse_id")) {
					int nurseID = 0;
					String val = "";
					do{
						val = Utils.readAttribute("nurse ID", "Ward", false);
						nurseID = Integer.parseInt(val);

						try{
							PreparedStatement stmt=conn.prepareStatement("Select * from nurse WHERE nurse_id = " + nurseID + " and status = 1");
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
		if (UpdateQuery != null && UpdateQuery.length() > 0 && UpdateQuery.charAt(UpdateQuery.length() - 1) == ',') {
        	UpdateQuery = UpdateQuery.substring(0, UpdateQuery.length() - 1);
        }
		UpdateQuery = UpdateQuery + " WHERE ward_id ="+ID;
//		System.out.println(UpdateQuery);

		
		//Execute the query
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(UpdateQuery);
			System.out.println("Successfully updated ward record");
		} catch(SQLException e) {
			System.out.println("Error while updating ward record");
			//System.out.println(e);
		}
	}

	/*It is used to permanently delete the ward from the database. */
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
	
	/*
	 * getCurrentWardUsageStatus()is a report used to view current availability and total capacity and percentage usage for all wards of all types.
	 * It is useful for Hospital Staff to check number of patients in hospital and also total available beds.
	 */
	public static void getCurrentWardUsageStatus(Connection conn) {
		try {
			PreparedStatement stmt=conn.prepareStatement("SELECT ward_id, total_capacity, current_availability FROM Ward");
			ResultSet rs = stmt.executeQuery(); 
			System.out.println("---------------------------------------------------");
			System.out.println("Current Ward usage status:");
			System.out.println("---------------------------------------------------");
			System.out.println("Ward_Id \t Total Capacity \t Current Avilability \t Percent Usage");
			while(rs.next()) {
				System.out.println(rs.getInt(1) + "\t \t " + rs.getInt(2) + "\t \t \t" + rs.getInt(3) + "\t \t \t" + (((rs.getInt(2)-rs.getInt(3))*100)/rs.getInt(2)) + "%");
			}
		}catch(Exception e) {
			System.out.println("Error occured while fetching ward usage");
		}
	}
	
	/*
	 * getWardAvailaibilityByWardType() is used to display all wards available for the given ward type and return the first available ward. 
	 * This information is useful for check-in(operator) staff to check ward availability based on patient preference.
	 */
	public static int getWardAvailaibilityByWardType(int ward_type, Connection conn) {
		try {
			PreparedStatement stmt=conn.prepareStatement("SELECT ward_id, current_availability FROM ward where ward_type = ? and current_availability > 0");
			stmt.setInt(1, ward_type);
			ResultSet rs = stmt.executeQuery();
			if (!rs.isBeforeFirst() ) {    
			    System.out.println("No data");
			    return -1; // Returns -1 if no such ward found
			} 
			int ward_return =0;
			if(rs.next()) {
				//System.out.println(rs.getInt(1));
				ward_return =  rs.getInt(1); // Returns the ward Id of the first ward found
			}
			rs.beforeFirst();
			System.out.println("Ward ID \t Availability");
			while(rs.next()) {
				System.out.println(rs.getInt(1) +"\t \t" + rs.getInt(2));
			}
			System.out.println("Ward Assigned : " + ward_return );
			return ward_return;
		}
		catch(Exception e) {
			//System.out.println(e.getMessage());
			System.out.println("Error Occured while fetching wards. Try Later");
		}
		return -1;
	}
	
	/*
	 * getWardUsageHistory() is a report used to view ward usage(total patients admitted in that ward) report for the given Ward Id and given date range
	 */
	public static void getWardUsageHistory(Connection conn) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter ward id");
		int w_id = sc.nextInt();
		System.out.println("Enter the start date(yyyy-mm-dd)");
		String sd = sc.next();
		System.out.println("Enter end date(yyyy-mm-dd)");
		String ed = sc.next();
		try {
			PreparedStatement stmt=conn.prepareStatement("SELECT ward_id, COUNT(*) FROM medical_records where ward_id = ? "+
					 "and checkin_date >= ? and checkout_date <= ?;");
			stmt.setInt(1, w_id);
			stmt.setString(2, sd);
			stmt.setString(3, ed);
			ResultSet rs = stmt.executeQuery(); 
			System.out.println("---------------------------------------------------");
			System.out.println("Ward usage for the given date range is:");
			System.out.println("---------------------------------------------------");
			System.out.println("Ward_Id \t Patient_Count");
			while(rs.next()) {
				System.out.println(w_id + "\t \t " + rs.getInt(2));
			}
			if(rs.getInt(2)==0)
				System.out.println("Change Date Range (Enter in Format Given) and Try again");
		}
		catch(Exception e) {
			//System.out.println(e.getMessage());
			System.out.println("Error occured while fetching Ward Usage Report for this date range");
		}
	}

	/*
	 * decrementWardCapacity() is used to decrement ward capacity once it is assigned to a patient during check-in
	 */
	public static void decrementWardCapacity(int ward_id, Connection conn){
		try {
			PreparedStatement stmt=conn.prepareStatement("Select ward_id, current_availability from ward where ward_id = ?",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, ward_id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				int capacity = rs.getInt(2);
				capacity = capacity - 1;
				rs.updateInt("current_availability", capacity);
				rs.updateRow();
				//conn.commit();
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error Occured while updating ward capacity");
		}
	}	

}
