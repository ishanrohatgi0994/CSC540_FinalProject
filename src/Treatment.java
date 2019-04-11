import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Scanner;
import java.sql.Connection;


public class Treatment {
	Scanner sc = new Scanner (System.in);
	MedicalRecord mr = new MedicalRecord();
	
	//Add treatment details for a patient
	public void addTreatment(Connection conn) {
		System.out.println("Enter Patient ID");
		int patientId = sc.nextInt();
		System.out.println("Enter Doctor ID");
		int doctorId = sc.nextInt();
		System.out.println("Enter the treatment type number");
		int medicalRecordId=-1, treatmentNumber;
		String treatmentType="";
		
		try {
			PreparedStatement stmt1 = conn.prepareStatement("select treatment_type from treatment_cost;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs1 = stmt1.executeQuery();
			HashMap<Integer, String> treatmentTypeByNumber = new HashMap<Integer, String>();
			int counter = 1;
			while(rs1.next()){
				treatmentTypeByNumber.put(counter, rs1.getString("treatment_type"));
				counter += 1;
			}
			for(int i=1; i<=counter; i++){
				System.out.println(i + " " + treatmentTypeByNumber.get(i));
			}
			treatmentNumber = sc.nextInt();
			treatmentType = treatmentTypeByNumber.get(treatmentNumber);
		}
		catch(Exception e) {
			System.out.println("Some Error occured while fetching the treatment types");
			System.exit(0);
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement("select mr_id from medical_records where patient_id = ? "
					+ " and checkout_date is null", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
			stmt.setInt(1, patientId);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			medicalRecordId = rs.getInt("mr_id");
		}
		catch (Exception e){
			System.out.println("The given Patient ID is not undergoing any treatment");
			System.exit(0);
		}
		
		try {
			PreparedStatement stmt2 = conn.prepareStatement("insert into treatment(mr_id, doc_id,treatment_type) "+
			"values(?,?,?)");
			stmt2.setInt(1, medicalRecordId);
			stmt2.setInt(2, doctorId);
			stmt2.setString(3, treatmentType);
			stmt2.executeUpdate(); // Insertion done
			System.out.println("Treatment Details Insertion Successful");
		}
		catch(Exception e){
			System.out.println("Unable to add treatment details");
		}
	}
	
	//View Current Treatment Details for a Patient
	public void viewCurrentTreatmentDetails(Connection conn) {
			System.out.println("Enter Patient ID");
			int patientId = sc.nextInt();
			int medicalRecordId = -1;
			try {
				PreparedStatement stmt = conn.prepareStatement("select * from medical_records where patient_id = ? "
						+ " and checkout_date is null", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				stmt.setInt(1, patientId);
				ResultSet rs = stmt.executeQuery();
				rs.next();
				medicalRecordId = rs.getInt(1);
			}
			catch(Exception e) {
				System.out.println("Given Patient is not undergoing any treatment");
				System.exit(0);
			}
			
			try {				
				PreparedStatement stmt2 = conn.prepareStatement("select * from treatment where mr_id = ? ");
				stmt2.setInt(1, medicalRecordId);
				ResultSet rs1 = stmt2.executeQuery();
				
				if (!rs1.next()) {
					System.out.println("No Treatment details found for given patient id");
				}
				else {
					System.out.println("Medical Record Id \t Doctor Id \t Treatment Type");
					do {
						System.out.println(rs1.getString(2) + "\t\t\t " + rs1.getInt(3) + " \t\t "+ rs1.getString(4));
					} while (rs1.next());
				}
			}
			catch (Exception e){
				System.out.println("Unable to fetch treatment details for given patient id");
			}
	}
	
	//Update Treatment for a given patient
	public void updateTreatment(Connection conn){
		System.out.println("Enter Patient ID who's treatment details has to be deleted");
		int patientId = sc.nextInt();
		int medicalRecordId = -1;
		try {
			PreparedStatement stmt = conn.prepareStatement("select * from medical_records where patient_id = ? "
					+ " and checkout_date is null", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, patientId);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			medicalRecordId = rs.getInt(1);
		}
		catch(Exception e) {
			System.out.println("Given Patient is not undergoing any treatment");
			System.exit(0);
		}
		
		try {				
			PreparedStatement stmt2 = conn.prepareStatement("select * from treatment where mr_id = ? ");
			stmt2.setInt(1, medicalRecordId);
			ResultSet rs1 = stmt2.executeQuery();
			
			if (!rs1.next()) {
				System.out.println("No Treatment details found for given patient id");
			}
			else {
				System.out.println("Treatment ID \t Medical Record Id \t Doctor Id \t Treatment Type");
				do {
					System.out.println(rs1.getInt(1) + "\t\t " + rs1.getString(2) + "\t\t\t " + rs1.getInt(3) + " \t\t "+ rs1.getString(4));
				} while (rs1.next());
			}
		}
		catch (Exception e){
			System.out.println("Unable to fetch treatment details for given patient id");
		}
		
		System.out.println("Please Enter one of the Treatment IDs from above list");
		int treatmentId = sc.nextInt();
		try {
			PreparedStatement stmt = conn.prepareStatement("Select * from treatment where tr_id =?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, treatmentId);
			ResultSet rs = stmt.executeQuery(); 
			String ch;
			
			if (!rs.next()) {
				System.out.println("No such treatment ID found");
			}
			else {
				
				do {
					System.out.println("Do you want to update Doctor ID(Y/N) ?");
					ch = sc.next();
					if(ch.equals("Y") || ch.equals("y")) {
						sc.nextLine();
						System.out.println("Enter Doctor ID");
						int newDrID = sc.nextInt();
						rs.updateInt("doc_id", newDrID);
					}
					
					System.out.println("Do you want to update the treatment (Y/N) ?");
					ch = sc.next();
						
					if(ch.equals("Y") || ch.equals("y")) {
						sc.nextLine();
						System.out.println("Enter Treatment number");
						PreparedStatement stmt1 = conn.prepareStatement("select treatment_type from treatment_cost;", 
								ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						ResultSet rs1 = stmt1.executeQuery();
						HashMap<Integer, String> treatmentTypeByNumber = new HashMap<Integer, String>();
						
						int counter = 1;
						while(rs1.next()){
							treatmentTypeByNumber.put(counter, rs1.getString("treatment_type"));
							counter++;
						}
						for(int i=1; i<=counter; i++){
							System.out.println(i + " " + treatmentTypeByNumber.get(i));
						}
						int treatmentNumber = sc.nextInt();
						String treatmentType = treatmentTypeByNumber.get(treatmentNumber);
						rs.updateString("treatment_type", treatmentType);
					}
					rs.updateRow();
				} while (rs.next());
				System.out.println("Treatment Details updated successfully");
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Treatment Details could not be updated");
		}
	}

	//Delete Treatment
	public void deleteTreatment(Connection conn) {
		System.out.println("Enter Patient ID who's treatment details has to be deleted");
		int patientId = sc.nextInt();
		int medicalRecordId = -1;
		try {
			PreparedStatement stmt = conn.prepareStatement("select * from medical_records where patient_id = ? "
					+ " and checkout_date is null", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, patientId);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			medicalRecordId = rs.getInt(1);
		}
		catch(Exception e) {
			System.out.println("Given Patient is not undergoing any treatment");
			System.exit(0);
		}
		
		try {				
			PreparedStatement stmt2 = conn.prepareStatement("select * from treatment where mr_id = ? ");
			stmt2.setInt(1, medicalRecordId);
			ResultSet rs1 = stmt2.executeQuery();
			
			if (!rs1.next()) {
				System.out.println("No Treatment details found for given patient id");
			}
			else {
				System.out.println("Treatment ID \t Medical Record Id \t Doctor Id \t Treatment Type");
				do {
					System.out.println(rs1.getInt(1) + "\t\t " + rs1.getString(2) + "\t\t\t " + rs1.getInt(3) + " \t\t "+ rs1.getString(4));
				} while (rs1.next());
			}
		}
		catch (Exception e){
			System.out.println("Unable to fetch treatment details for given patient id");
		}
		System.out.println("Enter the Treatment ID which has to be deleted from the above list");
		int treatmentId = sc.nextInt();
		try {
			PreparedStatement stmt3=conn.prepareStatement("delete from treatment where tr_id = ?");
			stmt3.setInt(1, treatmentId);
			stmt3.executeUpdate();
			System.out.println("Treatment Details deleted");
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error in deletion of treatment details");
		}
	}
}