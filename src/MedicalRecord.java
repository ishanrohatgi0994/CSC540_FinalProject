import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MedicalRecord {

	Scanner sc = new Scanner(System.in);
	public void checkInPatient(Connection conn) {
		System.out.println("Enter the Patient ID who needs to be checked_In");
		int p_id = sc.nextInt();
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		//System.out.println(date);
		try {
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO medical_records (checkin_date,patient_id)"+
									"VALUES (?,?)");
			stmt.setString(1, date);
			stmt.setInt(2, p_id);
			stmt.executeUpdate(); // Insertion done
			System.out.println("Patient "+ p_id + " successfully checked into wolfware hospital");
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Patient Check-In failed. Please ensure correct patient id is inserted or Try after sometime");
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

	public void assignWardToPatient(Connection conn) {
		// Aniruddha
		
	}

	public void checkOutPatient(Connection conn) {
		// Aniruddha - Billing flow
		
	}
}
