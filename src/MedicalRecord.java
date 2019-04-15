import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MedicalRecord {

	Scanner sc = new Scanner(System.in);
	/*
	 * 
	  CheckInPatient() is used to create the medical/check-in(in our case) record of patient when he enters the hospital for treatment.Details like Patient ID,
	  Check-In date and Ward_id(Not always) are filled in.When a patient check’s in, if a ward is required for them to be admitted into, their preference of
	  ward-type (1-bed,2-bed,3-bed,4-bed wards)is determined, and accordingly availability of ward is checked. If a ward with the patient’s preference is found, 
	  Patient is assigned to that ward and the ward_id is also fed in during check-in.The corresponding ward capacity is also decreased.
	  
	  A transaction is used in this case so that both operations of assigning ward 
	  and decrementing ward capacity are performed one after the other to maintain database consistency.

	  If a ward is required, and no preferred wards are available, Patient is not checked-in into the hospital and no records are created.
	  If a ward is not required during check-in. Patient can be checked in directly by creating medical record with patient id and check-in date

	 */
	
	public void checkInPatient(Connection conn) throws Exception {

		int p_id;
		try {
		conn.setAutoCommit(false); // Transaction start
		p_id = Patient.addPatientIfNotExists(conn);
		if(p_id < 0) {
			conn.rollback();
			System.out.println("Error creating a record for the patient or the patient has already checked-in");
			return;
		}
		int ward_id = -1;
		System.out.println("Ward Required ? (Y/N)");
		String ch = sc.next();
		boolean flag = true;
		if(ch.equals("Y") || ch.equals("y")) {
			System.out.println("Enter ward type preference: 1) 1-Bed \n 2) 2-Bed \n 3) 3-Bed \n 4) 4-Bed \n");
			int choice = sc.nextInt();
			ward_id = Ward.getWardAvailaibilityByWardType(choice, conn);
			if(ward_id == -1) {
				System.out.println("Ward Not Available. Cannot Check-In");
				conn.rollback();
				flag = false;
				return;
			}
		}
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		//conn.setAutoCommit(false); // Transaction
			if(ward_id != -1) {
				PreparedStatement stmt=conn.prepareStatement("INSERT INTO medical_records (checkin_date,patient_id,ward_id)"+
									"VALUES (?,?,?)");
				stmt.setString(1, date);
				stmt.setInt(2, p_id);
				stmt.setInt(3, ward_id);
				stmt.executeUpdate(); // Insertion done
			}
			else {
				PreparedStatement stmt=conn.prepareStatement("INSERT INTO medical_records (checkin_date,patient_id)"+
						"VALUES (?,?)");
				stmt.setString(1, date);
				stmt.setInt(2, p_id);
				stmt.executeUpdate(); // Insertion done
			}
			System.out.println("Patient "+ p_id + " successfully checked into wolfware hospital");
			if(ward_id != -1) {
				Ward.decrementWardCapacity(ward_id, conn);
				//System.out.println("Decremented corresponding ward capacity");
			}
			conn.commit(); // Committing the transaction
		}catch(SQLException e) {
			conn.rollback();
			System.out.println("Failed to add patient and check-in");
		}
		conn.setAutoCommit(true);
	}

	/* getLatestMedicalRecordFromPatientId() is a Utility API function, which returns the current medical record for patient given his/her patient ID. 
	 This is used by many other Reporting, Tasks and Operations API's.
	*/
	public int getLatestMedicalRecordFromPatientId(int p_id,Connection conn) {
		try {
				PreparedStatement stmt=conn.prepareStatement("Select mr_id from medical_records where patient_id = ? and checkout_date is null");
				stmt.setInt(1, p_id);
				ResultSet rs = stmt.executeQuery();
				int mr_id = -1;
				while(rs.next()) {
					mr_id = rs.getInt(1);
				}
				return mr_id;
			} catch (Exception e) {
				System.out.println("Error occured while finding medical record for given Patient ID");
			}
			
		return -1;
		
	}
	
	/*
	 * UpdateMedicalRecord(), helps in updating the medical records with prescription, diagnosis and responsible doctor information. 
	 * Checkout is handled in Patient Class checkoutPatient method
	 */
	public void updateMedicalRecord(Connection conn) {
		// TODO Auto-generated method stub
		System.out.println("Enter Patient Id whose record needs to be updated");
		int p_id = sc.nextInt();
		int mr_id = getLatestMedicalRecordFromPatientId(p_id,conn);
		if(mr_id == -1) {
			System.out.println("Patient not checked-In yet");
		}
		else {
			System.out.println(mr_id);
			try {
				PreparedStatement stmt=conn.prepareStatement("Select * from medical_records where mr_id = ?",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				stmt.setInt(1, mr_id);
				ResultSet rs = stmt.executeQuery(); 
				String ch;
				while(rs.next()) {
					//System.out.println(rs.getString("name"));
					System.out.println("Do you want to update prescription (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						String old_prescription = rs.getString(3);
						if(old_prescription==null)
							old_prescription="";
						System.out.println("Enter new prescription details");
						sc.nextLine();
						String new_prescription = sc.nextLine();
						rs.updateString("prescription", old_prescription+ "\n" + new_prescription);
						System.out.println("Update Prescription successful");
					}
					
					System.out.println("Do you want to update diagnosis (Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						String old_diagnosis = rs.getString(2);
						if(old_diagnosis==null)
							old_diagnosis = "";
						System.out.println("Enter new diagnosis details");
						sc.nextLine();
						String new_diagnosis = sc.nextLine();
						rs.updateString("diagnosis", old_diagnosis+ "\n" + new_diagnosis);
						System.out.println("Update Diagnosis successful");
					}
					System.out.println("Do you want to update Responsible Doctor for this Patient(Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						System.out.println("Enter Responsible  Doctor Id");
						int new_doc_id = sc.nextInt();
						rs.updateInt("doc_id",new_doc_id);
						System.out.println("Update Responsible Doctor successful");
					}
					rs.updateRow();
					System.out.println("Medical Record Updated Successfully !!");
				}
				System.out.println("Thankyou !!");
			}
			catch(Exception e) {
				System.out.println("Update Failed. Please try again with correct Inputs");
			}
		}		
	}

	/*
	 * viewMedicalRecordForPatient is used to view details of current medical record (mr_id, doc_id, prescription, diagnosis, ward_id, checkin_date) 
	 * for given patient Id if he is in hospital. View Medical History is covered in Patient class
	 */
	
	public void viewMedicalRecordForPatient(Connection conn) {
		System.out.println("Enter Patient Id whose current medical record you want to see");
		int p_id = sc.nextInt();
		int mr_id = getLatestMedicalRecordFromPatientId(p_id, conn);
		if(mr_id == -1)
			System.out.println("Patient Record Not found");
		else {
			// Fetch details for this mr_id and print
			String query = "SELECT mr_id, doc_id, prescription, diagnosis, ward_id, checkin_date FROM medical_records WHERE "+
					"mr_id="+mr_id;
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				System.out.println("MEDICAL RECORD ID: " + rs.getInt(1));
				System.out.println("RESPONSIBLE DOCTOR: " + rs.getInt(2));
				System.out.println("PRESCRIPTION GIVEN: "+rs.getString(3));
				System.out.println("DIAGNOSIS: "+rs.getString(4));
				System.out.println("WARD: "+rs.getInt(5));
				System.out.println("CHECK IN DATE: "+rs.getDate(6));
			}catch (Exception e) {
				System.out.println("Failed to get latest medical record");
			}
		}
	}


	/* Get all patients for a given date range returns a count of the number of times a patient entry was created for that date range.
	* Also it returns a count of the distinct number of patients that entered for the given date range.*/
	public static void getPatientPerMonth(Connection conn){
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the start date(yyyy-mm-dd)");
		String sd = sc.next();
		System.out.println("Enter end date(yyyy-mm-dd)");
		String ed = sc.next();

		try {
			PreparedStatement stmt=conn.prepareStatement("SELECT COUNT(patient_id), COUNT(DISTINCT(patient_id)) FROM medical_records where "+
					"checkin_date >= ? and checkin_date <= ?;");
			stmt.setString(1, sd);
			stmt.setString(2, ed);
			ResultSet rs = stmt.executeQuery();
			System.out.println("---------------------------------------------------");
			System.out.println("Patient checkins for the given date range is:");
			System.out.println("---------------------------------------------------");
			System.out.println("Total Count \t\t Distinct Count");

			while(rs.next()) {
				System.out.println(rs.getInt(1) + "\t \t " + rs.getInt(2));
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			System.out.println("Error occured while fetching number of patients");
		}
	}
}
