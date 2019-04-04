import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;

public class Patient {
    /*
    Required attributes:
    name, phone, current_status
     */
    public static int STATUS_NOT_IN_HOSPITAL = 0;
    public static int STATUS_OUTPATIENT = 1;
    public static int STATUS_ADMITTED = 2;


    //TODO Check error condition:
    // if phone is entered with special characters
    // if ssn is entered with special characters

    //TODO indicate what 0, 1 and 2 means when asking for the current status.

    //TODO viewPatients to get the id of the patient?

    // Read details of a patient interactively and insert to the database.
    public static void addPatient(Connection conn) throws IOException    {
        // initialize required attributes
        String name, address;
        BigInteger ssn, phone;
        Integer current_status, age;

        // Interactively read each attributes
        name = Utils.readAttribute("name", "Patient", false);
        address = Utils.readAttribute("address", "Patient", true);
        if(address.equals("")) {
            address = null;
        }
        String ssnString = Utils.readAttribute("ssn", "Patient", true);
        if(ssnString.equals("")){
            ssn = null;
        }else {
            ssn = new BigInteger(ssnString);
        }
        phone = new BigInteger(Utils.readAttribute("phone number", "Patient", false));
        String ageString = Utils.readAttribute("age", "Patient", true);
        if(ageString.equals("")){
            age = null;
        }else {
            age = Integer.parseInt(ageString);
        }
        current_status = Integer.parseInt(Utils.readAttribute("current status ", "Patient", false));
        while(current_status != STATUS_ADMITTED && current_status != STATUS_NOT_IN_HOSPITAL && current_status != STATUS_OUTPATIENT) {
            System.out.println("Invalid status "+current_status+" entered. Try again.");
            current_status = Integer.parseInt(Utils.readAttribute("current status ", "Patient", false));
        }

        // execute the statement
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO patient (name, address, ssn, phone, current_status, age)"+
                    " VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, address);
            if(ssn != null) {
                ps.setBigDecimal(3, new BigDecimal(ssn));
            } else {
                ps.setBigDecimal(3, null);
            }
            ps.setBigDecimal(4, new BigDecimal(phone));
            ps.setInt(5, current_status);
            if(age != null) {
                ps.setInt(6, age);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();

            // Get the inserted id
            ResultSet rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
            if(rs.next()) {
                System.out.println("Successfully inserted patient record with ID " + rs.getInt(1));
            } else {
                System.out.println("Successfully inserted patient record. Bud ID could not be retrieved.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to insert patient");
            e.printStackTrace();
        }
    }

    // update a patient by interactively getting the ID of the patient.
    public static void updatePatient(Connection conn) throws Exception{
        int ID;
        ID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));
        String UpdateQuery = "UPDATE patient SET ";
        String[] attributes = {"name", "address", "ssn", "phone", "age"};
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Build the update query interactively
        for (String attribute: attributes) {
            System.out.println("Should "+ attribute+" be updated? (y/n)");
            if (br.readLine().equals("y")) {
                String val = Utils.readAttribute(attribute, "Patient", false);
                if(attribute.equals("ssn") || attribute.equals("phone")) {
                    UpdateQuery = UpdateQuery + attribute + "="+ new BigInteger(val) + ", ";
                }
                else if (attribute.equals("age")) {
                    UpdateQuery = UpdateQuery + attribute + "=" + val + " ";
                }
                else {
                    UpdateQuery = UpdateQuery + attribute + "='" + val+"', ";
                }
            }
        }
        UpdateQuery = UpdateQuery + "WHERE patient_id="+ID;
        System.out.println(UpdateQuery);
        //Execute the query
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(UpdateQuery);
            System.out.println("Successfully updated patient record");
        } catch(SQLException e) {
            System.out.println("Error while updating patient record");
        }
    }


    //TODO  "Successfully deleted the patient" message appropriate?

    // soft delete of the patient
    public static void deletePatient(Connection conn) throws Exception {
        int ID;
        ID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));
        System.out.println("Are you sure you want to delete the patient with id "+ ID+"? (y/n)");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if(br.readLine().equals("y")) {
            Statement stmt = conn.createStatement();
            String UpdateQuery = "UPDATE patient SET current_status = "+STATUS_NOT_IN_HOSPITAL+" where patient_id = "+ID;
            try {
                stmt.executeUpdate(UpdateQuery);
                System.out.println("Successfully deleted the patient");
            }catch (SQLException e) {
                System.out.println("Failed to delete the patient "+ID);
            }
        }
    }

    //TODO make the function a transaction

    // assign ward to a patient interactively
    public static void assignWardToPatient(Connection conn) throws Exception {
        int wardID, patientID;
        wardID = Integer.parseInt(Utils.readAttribute("ID", "Ward", false));
        patientID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));

        // get current medical record ID for the patient
        int medicalRecordID = getCurrentMedicalRecordID(conn, patientID);
        if(medicalRecordID == -1) {
            System.out.println("Error fetching medical record for the patient");
            return;
        }
        try {
            // check if the patient is already assigned a ward
            String getWardFromMedicalRecord = "SELECT ward_id FROM medical_records WHERE mr_id="+medicalRecordID;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getWardFromMedicalRecord);
            if(rs.next()) {
                rs.getInt(1);
                if(!rs.wasNull()) {
                    System.out.println("Patient already assigned a ward "+rs.getInt(1));
                    return;
                }
            } else {
                System.out.println("Medical record not found!");
                return;
            }

            // check if at least 1 bed is available in the ward
            String getCurrentAvailability = "SELECT current_availability FROM ward WHERE ward_id="+wardID;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getCurrentAvailability);
            int currentAvailability = 0;
            if(rs.next()) {
                currentAvailability = rs.getInt(1);
            }
            if(currentAvailability <= 0) {
                System.out.println("No beds available in the ward "+wardID);
                return;
            }

            // update the ward information in the medical record
            String updateQuery = "UPDATE medical_records SET ward_id = " + wardID +" WHERE mr_id = "+ medicalRecordID;
            // System.out.println(updateQuery);
            stmt = conn.createStatement();
            stmt.executeUpdate(updateQuery);

            // update current availability in wards
            String updateWard = "UPDATE ward SET current_availability = "+(currentAvailability-1)+" WHERE ward_id="+wardID;
            stmt = conn.createStatement();
            stmt.executeUpdate(updateWard);
            System.out.println("Successfully updated ward id to "+wardID+" for patient "+patientID);
        } catch (SQLException e) {
            System.out.println("Error assigning ward to the patient "+patientID);
            e.printStackTrace();
        }
    }

    // get current medical record ID for the patient who is currently enrolled
    public static int getCurrentMedicalRecordID(Connection conn, int patientID) throws Exception {
        Statement stmt = conn.createStatement();
        String query = "SELECT mr_id FROM medical_records WHERE patient_id = " + patientID + " AND checkout_date IS NULL";
        try {
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error while getting recent medical record for the patient "+ patientID);
        }
        return -1;
    }

    // view medical history for a patient
    public static void viewMedicalHistory(Connection conn) throws Exception {
        int patientID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));
        String startDate = Utils.readAttribute("Begin Date", "Medical History (yyyy-mm-dd)", false);
        String endDate = Utils.readAttribute("End Date", "Medical History (yyyy-mm-dd)", false);

        // Display summary by printing medical records which contains prescription, diagnosis and doctor responsible
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT mr_id, doc_id, prescription, diagnosis, ward_id, checkin_date, checkout_date FROM medical_records WHERE "+
                    "checkin_date >= '"+startDate+"' AND checkout_date <= '"+endDate+"' AND patient_id = "+patientID;
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Summary history");
            System.out.println("-----");
            int mrCount = 0;
            while(rs.next()) {
                System.out.println("MEDICAL RECORD ID: "+rs.getInt(1));
                System.out.println("TREATED DOCTOR ID: "+rs.getInt(2));
                System.out.println("PRESCRIPTION GIVEN: "+rs.getString(3));
                System.out.println("DIAGNOSIS: "+rs.getString(4));
                System.out.println("CHECKIN DATE: "+rs.getDate(5));
                System.out.println("CHECKOUT DATE: "+rs.getDate(6));
                String getTreatments = "SELECT treatment_type, doc_id FROM treatment WHERE mr_id="+rs.getInt(1);
                Statement treatmentStatement = conn.createStatement();
                ResultSet treatments = treatmentStatement.executeQuery(getTreatments);
                while(treatments.next()){
                    System.out.println("\tTREATMENT: "+treatments.getString(1));
                    System.out.println("\tSPECIALITY DOCTOR: "+treatments.getInt(2));
                    System.out.println();
                }
                mrCount ++;
            }
            System.out.println("-----");
        } catch(SQLException e) {
            System.out.println("Error getting medical record summary history ");
            e.printStackTrace();
        }
    }

    //TODO make this a transaction

    // checkout the patient from the hospital.
    public static void checkoutPatient(Connection conn) throws Exception {
        int patientID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));
        int medicalRecordID = getCurrentMedicalRecordID(conn, patientID);

        // check if the patient is already checked out. If yes, then no need to checkout again
        if(medicalRecordID == -1) {
            System.out.println("Patient already checked out");
            return;
        }

        String updateMedicalRecord = "UPDATE medical_records SET checkout_date=curdate() WHERE mr_id="+medicalRecordID;
        String updatePatientStatus = "UPDATE patient SET current_status="+STATUS_NOT_IN_HOSPITAL+" WHERE patient_id="+patientID;

        try {
            releaseBed(conn, medicalRecordID);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(updateMedicalRecord);
            stmt.executeUpdate(updatePatientStatus);
            //TODO call create billing entry here
        } catch (SQLException e) {
            System.out.println("Error checking out the patient");

        }
    }

    public static void releaseBed(Connection conn, int medicalRecordID) throws Exception{
        String getMedicalRecord = "SELECT ward_id  FROM medical_records WHERE mr_id=" + medicalRecordID;
        Statement mrStatement = conn.createStatement();
        ResultSet mrRs = mrStatement.executeQuery(getMedicalRecord);
        if (mrRs.next()) {
            int ward_id = mrRs.getInt(1);
            if (!mrRs.wasNull()) {
                // get current availability of the ward
                String getCurrentAvailability = "SELECT current_availability FROM ward WHERE ward_id="+ward_id;
                Statement wardStatement = conn.createStatement();
                ResultSet wardResult = wardStatement.executeQuery(getCurrentAvailability);

                // increment the current availability for the ward
                if(wardResult.next()) {
                    int currentAvailability = wardResult.getInt(1);
                    String updateWardAvailability = "UPDATE ward SET current_availability="+(currentAvailability+1)+
                            " WHERE ward_id="+ward_id;
                    wardStatement.executeUpdate(updateWardAvailability);
                }

                // Remove ward ID from medical record
                String updateMedicalRecord = "UPDATE medical_records SET ward_id = NULL WHERE mr_id="+medicalRecordID;
                mrStatement.executeUpdate(updateMedicalRecord);
            } else {
                System.out.println("No beds assigned to the patient");
            }
        }
    }
}
