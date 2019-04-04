import java.sql.Connection;
import java.sql.PreparedStatement;
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

	public int getMedicalRecordFromPatientId(int p_id) {
		
		return p_id;
		
	}
	public void updateMedicalRecord(Connection conn) {
		// TODO Auto-generated method stub
		
	}

}
