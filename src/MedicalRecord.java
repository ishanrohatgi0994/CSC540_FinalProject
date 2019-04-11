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
	public void checkInPatient(Connection conn) {
		System.out.println("Enter the Patient ID who needs to be checked_In");
		int p_id = sc.nextInt();
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
				flag = false;
			}
		}
		if(flag) {
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			//System.out.println(date);
			try {
				conn.setAutoCommit(false); // Transaction
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
				try {
					if(ward_id != -1) {
						Ward.decrementWardCapacity(ward_id, conn);
						System.out.println("Decremented corresponding ward capacity");
					}
					conn.commit(); // Committing the transaction
					
				}catch (Exception e) {
					conn.rollback();
					System.out.println("Ward Capacity DecrementOperation Failed");
				}
			}catch(Exception e) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				//System.out.println(e.getMessage());
				System.out.println("Patient Check-In failed. Please ensure correct patient id is inserted or Try after sometime");
			}
			try {
				conn.commit();// No exceptions. Committing the transaction
				conn.setAutoCommit(true);
			}catch(Exception e) {
				System.out.println("SQL Transaction commit Exception");
			}
		}
	}

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
				System.out.println(e.getMessage());
				System.out.println("Error occured while finding medical record for given Patient ID");
			}
			
		return -1;
		
	}
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
				System.out.println(e.getMessage());
				System.out.println("Update Failed. Please try again with correct Inputs");
			}
		}		
	}

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
				System.out.println(e.getMessage());
				System.out.println("Failed to get latest medical record");
			}
		}
	}
}
