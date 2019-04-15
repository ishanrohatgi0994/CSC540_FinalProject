
import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Patient {
    /*
    Patient attributes
    Required attributes:
    name, phone, current_status
     */
    int patientID, current_status;
    String name, address, gender;
    BigDecimal ssn, phone;
    Integer age;

    /*
    current_status values:
    STATUS_NOT_IN_HOSPITAL if patient registered for some treatment and was also released.
    STATUS_OUTPATIENT: If patient is in the hospital for normal check up.
    STATUS_ADMITTED: If patient is admitted to a ward in the hospital
    STATUS_SOFT_DELETED: If the patient is deleted. view medical records for this will not work.
        The soft deleted patients are only used for generating reports.
     */
    public static int STATUS_NOT_IN_HOSPITAL = 0;
    public static int STATUS_OUTPATIENT = 1;
    public static int STATUS_ADMITTED = 2;
    public static int STATUS_SOFT_DELETED = 3;

    /*
    Billing payment statuses
     */
    public static int STATUS_UNPAID = 0;
    public static int STATUS_PAID = 1;

    /*
    DEFAULT_REGISTRATION_CHARGES is 100 dollars
     */
    public static float DEFAULT_REGISTRATION_CHARGES = 100;

    /*
    Types of payment available:
     */
    public static String PAYMENT_TYPE_CREDIT_CARD = "Credit Card";
    public static String PAYMENT_TYPE_DEBIT_CARD = "Debit Card";
    public static String PAYMENT_TYPE_CASH = "Cash";
    public static String PAYMENT_TYPE_CHECK = "Check";

    // get Patient objects through a list of IDs this excludes all the patients which are deleted
    public static ArrayList<Patient> getPatientByIDs(Connection conn, int[] ids) throws Exception {
        /*
        Input:
            connection to a database (conn)
            list of ids to be queried (ids)
        Output:
            ArrayList of patients
         */
        ArrayList<Patient> pl = new ArrayList<>();
        if(ids == null || ids.length == 0) {
            return pl;
        }
        String result = Arrays.stream(ids)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        // Query used for getting all the list of all patients
        String query = "SELECT patient_id,ssn, name, phone, age, gender, address, current_status "+
                "FROM patient WHERE patient_id IN  ("+result+") AND current_status != "+Patient.STATUS_SOFT_DELETED;

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        // Create patient objects for each patient and append to the returned array list.
        while(rs.next()){
            Patient p = new Patient();
            p.patientID = rs.getInt("patient_id");
            p.name = rs.getString("name");
            p.address = rs.getString("address");
            p.age = rs.getInt("age");
            if(rs.wasNull()){
                p.age = null;
            }
            // p.phone = new BigInteger(String.valueOf(rs.getLong("phone")));
            p.phone = rs.getBigDecimal("phone");
            p.ssn = rs.getBigDecimal("ssn");
            if(rs.wasNull()){
                p.ssn = null;
            }
            p.gender = rs.getString("gender");
            pl.add(p);
        }
        return pl;
    }

    // TODO include in list menu options to include <admin, doctor, nurse>
    // view patients by ids
    public static void viewPatientsByIDs(Connection conn) {
        /*
        Input:
            Connection to the database.
        Asks the user first for the number of patients being queried.
        Interactively asks for the IDs of each patient.
        Displays the information of all the patients.
         */
        System.out.println("Enter the number of patients you want to get details of ");
        Scanner sc = new Scanner(System.in);
        int numPatients = sc.nextInt();
        int[] ids = new int[numPatients];
        System.out.println("Enter the ids of the patients");
        for(int i = 0; i<numPatients ; i++){
            ids[i] = sc.nextInt();
        }
        try {
            ArrayList<Patient> plist = Patient.getPatientByIDs(conn, ids);
            if(plist.size() == 0) {
                System.out.println("Patients doesn't exist or deleted");
            }
            for(Patient p: plist) {
                System.out.println("DETAILS OF PATIENT WITH ID "+p.patientID);
                System.out.println("\tName: "+p.name);
                System.out.println("\tGender: "+p.gender);
                System.out.println("\tAge: "+p.age);
                System.out.println("\tPhone: "+p.phone);
                System.out.println("\tAddress: "+p.address);
                switch(p.current_status) {
                    case 0:
                        System.out.println("Current Status: OUT OF HOSPITAL");
                        break;
                    case 1:
                        System.out.println("Current Status: ADMITTED");
                        break;
                    case 2:
                        System.out.println("Current Status: OUTPATIENT");
                    case 3:
                        //do nothing
                        break;
                    default:
                        System.out.println("Current Status: INVALID");
                }
                System.out.println("SSN: "+p.ssn);
                System.out.println();
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            System.out.println("Error fetching details of patients "+ids);
        }
    }


    // Read details of a patient interactively and insert to the database.
    static int addPatientIfNotExists(Connection conn) throws Exception    {
        /*
        Input:
            Connection to the database.
        Interactively asks for the details of the patient and creates a patient record.

        If the patient was soft-deleted, just change the current status of the patient to the one the user inputs.
        else create a new patient entry
        returns the ID of the previously existing patient or new the ID of the new patient record.
         */

        // initialize required attributes
        String name, address;
        BigInteger ssn, phone;
        // Integer current_status, age;
        Integer age;

        // Interactively read each attributes
        // read name of the patient
        name = Utils.readAttribute("name", "Patient", false);
        // read phone number of the patient
        phone = new BigInteger(Utils.readAttribute("phone number", "Patient", false));
        String s = "SELECT patient_id, current_status FROM patient WHERE name = '"+name+"' AND phone ="+phone;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(s);
        if(rs.next()) {
            if (rs.getInt("current_status") == Patient.STATUS_SOFT_DELETED || rs.getInt("current_status") == Patient.STATUS_NOT_IN_HOSPITAL) {
                // Update the status to STATUS_OUTPATIENT
                Statement stmt2 = conn.createStatement();
                String updatePatient = "UPDATE patient SET current_status="+STATUS_OUTPATIENT+" WHERE patient_id = "+rs.getInt("patient_id");
                stmt2.executeUpdate(updatePatient);
            } else if (rs.getInt("current_status") == Patient.STATUS_ADMITTED || rs.getInt("current_status")== Patient.STATUS_OUTPATIENT){
                // Patient is already in the hospital. Hence avoid checking in once again
                return -1;
            }
            // Return the present patient ID
        	return rs.getInt("patient_id");
        }
        // read other attributes
        address = Utils.readAttribute("address", "Patient", true);
        if(address.equals("")) {
            address = null;
        }
        // read ssn of the patient
        String ssnString = Utils.readAttribute("ssn", "Patient", true);
        if(ssnString.equals("")){
            ssn = null;
        }else {
            ssn = new BigInteger(ssnString);
        }
        // read age of the patient
        String ageString = Utils.readAttribute("age", "Patient", true);
        if(ageString.equals("")){
            age = null;
        }else {
            age = Integer.parseInt(ageString);
        }
        
        // gender of the patient
        String gender = Utils.readAttribute("Gender", "Patient", true);
        if(gender.equals("")){
            gender = null;
        }

        // execute the statement
        try {
            // Insert the patient
            PreparedStatement ps = conn.prepareStatement("INSERT INTO patient (name, address, ssn, phone, current_status, age, gender)"+
                    " VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, address);
            if(ssn != null) {
                ps.setBigDecimal(3, new BigDecimal(ssn));
            } else {
                ps.setBigDecimal(3, null);
            }
            ps.setBigDecimal(4, new BigDecimal(phone));
            ps.setInt(5, STATUS_OUTPATIENT);
            if(age != null) {
                ps.setInt(6, age);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, gender);
            ps.executeUpdate();

            // Get the inserted id
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
            if(rs1.next()) {
                System.out.println("Successfully inserted patient record with ID " + rs1.getInt(1));
                return rs1.getInt(1);
            } else {
                System.out.println("Successfully inserted patient record. Bud ID could not be retrieved.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to insert patient");
            //e.printStackTrace();
        }
        return -1;
    }

    // update a patient by interactively getting the ID of the patient.
    public static void updatePatient(Connection conn) throws Exception{
        /*
        Input:
            Connection to the database
        Interactively asks the user if a particular record needs to be updated, prepares an update query and executes it.
         */
        int ID;
        ID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));

        // check if the ID is is not soft deleted.
        int[] ids = new int[1];
        ids[0] = ID;
        ArrayList al = getPatientByIDs(conn, ids);
        if(al.size() == 0) {
            System.out.println("There are no patients by that ID.");
            return;
        }

        // build the update query
        String UpdateQuery = "UPDATE patient SET ";
        String[] attributes = {"name", "address", "ssn", "phone", "age"};
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Build the update query interactively
        for (String attribute: attributes) {
            System.out.println("Should "+ attribute+" be updated? (y/n)");
            if (br.readLine().equals("y")) {
                String val = Utils.readAttribute(attribute, "Patient", false);
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
        UpdateQuery = UpdateQuery + " WHERE patient_id="+ID;
        //System.out.println(UpdateQuery);
        //Execute the query
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(UpdateQuery);
            System.out.println("Successfully updated patient record");
        } catch(SQLException e) {
            System.out.println("Error while updating patient record");
        }
    }



    // soft delete of the patient. Upate the current status field of the patient to SOFT_DELETED
    public static void deletePatient(Connection conn) throws Exception {
        /*
        Input:
            Connection to the database
        Soft deletes the patient only by updating the patient record to have the status; 3 i.e SOFT_DELETED.
         */
        int ID;
        ID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));
        System.out.println("Are you sure you want to delete the patient with id "+ ID+"? (y/n)");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if(br.readLine().equals("y")) {
            Statement stmt = conn.createStatement();
            String UpdateQuery = "UPDATE patient SET current_status = "+Patient.STATUS_SOFT_DELETED+" where patient_id = "+ID;
            try {
                stmt.executeUpdate(UpdateQuery);
                System.out.println("Successfully deleted the patient");
            }catch (SQLException e) {
                System.out.println("Failed to delete the patient "+ID);
            }
        }
    }

    // assign/update ward to a patient interactively by asking for thier preferred ward type
    public static void assignOrUpdateWardToPatient(Connection conn) throws Exception {
        /*
        Input:
            Connection to the database.

        Assigns ward to a patient if
        1. Ward type queried has at least 1 bed available
        1. if patient record exists
        2. if patient has not been deleted
        3. Patient is currently not checked out.
        4. There are beds available in the ward.

        Prompts the user if a ward has already been assigned to the patient.

        Transaction part:
        1. Update medical record of the patient
        2. Update the patient status and set it to be currently Admitted.
        3. Update the current availability of the new ward.
        4. Release bed if updating the patient's ward.
        NOTE: updation of ward is only to accomodate for human mistakes while assigning wards. Generation of bill will
        assume the paitnet is present in the newly assigned ward from the start date till end date.
         */
        int wardID, patientID;
        // Get the patient id from the user
        patientID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));

        // Get the ward type preference of the customer
        System.out.println("Enter ward type preference: 1) 1-Bed \n 2) 2-Bed \n 3) 3-Bed \n 4) 4-Bed \n");
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();
        wardID = Ward.getWardAvailaibilityByWardType(choice, conn);
        if(wardID == -1) {
            System.out.println("Ward Not Available. Cannot Check-In");
            return;
        }
        // get current medical record ID for the patient
        int medicalRecordID = getCurrentMedicalRecordID(conn, patientID);
        if(medicalRecordID == -1) {
            System.out.println("Error fetching medical record for the patient");
            return;
        }

        // check if the patient is not deleted.
        int[] ids = {patientID};
        ArrayList<Patient> patientList = Patient.getPatientByIDs(conn, ids);
        if(patientList.size() <= 0) {
            System.out.println("Patient not found. Hence ward cannot be assigned.");
            return;
        }

        conn.setAutoCommit(false);
        try {
            // check if the patient is already assigned a ward
            String getWardFromMedicalRecord = "SELECT ward_id FROM medical_records WHERE mr_id="+medicalRecordID;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getWardFromMedicalRecord);
            if(rs.next()) {
                rs.getInt(1);
                if(!rs.wasNull()) {
                    System.out.println("Patient already assigned a ward "+rs.getInt(1));
                    System.out.println("Do you want to update the ward? Cost still will be taken from the date of check in (y/n)");
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String ip = br.readLine();
                    if(!ip.equals("y")){

                        System.out.println("Did not update ward info for the patient"+ ip);
                        return;
                    }
                    Patient.releaseBed(conn, medicalRecordID);
                }
            } else {
                System.out.println("Medical record not found or patient deleted!");
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

            // update the status of the patient to currently admitted.
            String updatePatient = "UPDATE patient SET current_status="+STATUS_ADMITTED+" WHERE patient_id="+patientID;
            stmt = conn.createStatement();
            stmt.executeUpdate(updatePatient);

            //Commit the transaction
            conn.commit();
            System.out.println("Successfully updated ward id to "+wardID+" for patient "+patientID);
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Error assigning ward to the patient "+patientID);
            //e.printStackTrace();
        }
        conn.setAutoCommit(true);
    }

    // get current medical record ID fupdor the patient who is currently enrolled
    public static int getCurrentMedicalRecordID(Connection conn, int patientID) throws Exception {
        /*
        Input:
            Connection to the database
            PatientID to get the medical record details from
        Output:
            Medical record ID if a medical record exists with no checkout date (Which means the patient is still in the
            hospital)

         */
        Statement stmt = conn.createStatement();
        String query = "SELECT mr_id FROM medical_records WHERE patient_id = " + patientID +
                " AND checkout_date IS NULL";
        try {
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error while getting recent medical record for the patient "+ patientID);
            //System.out.println(e.getMessage());
        }
        return -1;
    }

    // view medical history for a patient
    public static void viewMedicalHistory(Connection conn) throws Exception {
        /*
        Input: Connection to the database.

        View Medical history of a patient by getting the patient id.
        Medical history refers to the medical record details which starts after the start-date entered and ends before
        the end-date entered by the user.
         */
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
                System.out.println("#####");
                System.out.println("MEDICAL RECORD ID: "+rs.getInt("mr_id"));
                System.out.println("TREATED DOCTOR ID: "+rs.getInt("doc_id"));
                System.out.println("RESPONSIBLE DOCTOR DETAILS: ");

                int[] docIDs = {rs.getInt(2)};
                Doctor d = new Doctor();
                d.viewDoctorsByIds(conn, docIDs);

                System.out.println("PRESCRIPTION GIVEN: "+rs.getString("prescription"));
                System.out.println("DIAGNOSIS: "+rs.getString("diagnosis"));
                System.out.println("CHECKIN DATE: "+rs.getDate("checkin_date"));
                System.out.println("CHECKOUT DATE: "+rs.getDate("checkout_date"));
                String getTreatments = "SELECT treatment_type, doc_id FROM treatment WHERE mr_id="+rs.getInt("mr_id");
                Statement treatmentStatement = conn.createStatement();
                ResultSet treatments = treatmentStatement.executeQuery(getTreatments);
                while(treatments.next()){
                    System.out.println("\tTREATMENT: "+treatments.getString(1));
                    System.out.println("\tSPECIALITY DOCTOR: "+treatments.getInt(2));

                    int[] specialityDoctor = {treatments.getInt(2)};
                    d.viewDoctorsByIds(conn, specialityDoctor);

                    System.out.println();
                }
                mrCount ++;
                System.out.println("#####");
            }
            System.out.println("-----");
        } catch(SQLException e) {
            System.out.println("Error getting medical record summary history ");
            //e.printStackTrace();
        }
    }

    // checkout the patient from the hospital.
    public static void checkoutPatient(Connection conn) throws Exception {
        /*
        Checkout Patient steps:
        1. check if a medical record which has not checked out till now is present
        2. check if the entered patientID is present in the data and not deleted.
         */
        int patientID = Integer.parseInt(Utils.readAttribute("ID", "Patient", false));
        int medicalRecordID = Patient.getCurrentMedicalRecordID(conn, patientID);

        // check if the patient is already checked out. If yes, then no need to checkout again
        if(medicalRecordID == -1) {
            System.out.println("Patient already checked out");
            return;
        }

        // check if the patient is not soft deleted.
        int[] ids = {patientID};
        ArrayList patients = Patient.getPatientByIDs(conn, ids);
        if(patients.size() == 0) {
            System.out.println("Patient not found or Patient has been deleted");
            return;
        }

        String updateMedicalRecord = "UPDATE medical_records SET checkout_date=curdate() WHERE mr_id="+medicalRecordID;
        String updatePatientStatus = "UPDATE patient SET current_status="+STATUS_NOT_IN_HOSPITAL+" WHERE patient_id="+patientID;

        conn.setAutoCommit(false);
        try {
            releaseBed(conn, medicalRecordID);
            generateBillingRecord(conn, medicalRecordID);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(updateMedicalRecord);
            stmt.executeUpdate(updatePatientStatus);
            conn.commit();
            System.out.println("Successfully checked out the patient");
        } catch (SQLException e) {
            System.out.println("Error checking out the patient");
            //System.out.println(e.getMessage());
            conn.rollback();
        }
        conn.setAutoCommit(true);
    }

    public static void releaseBed(Connection conn, int medicalRecordID) throws Exception{
        /*
        Input:
            Connection to the database
            Medical record ID

        Releases a bed in the ward and increases the bed count in the ward.
         */
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
            } else {
                System.out.println("No beds assigned to the patient");
            }
        }
    }

    public static void generateBillingRecord(Connection conn, int mr_id) throws Exception{
        /*
        Input:
            Connection to the database
            Medical record id
        Assumptions:
            Called after checking the patient is already deleted
        Sum up all the entries in the treatment table by adding all the treatement costs.
        Add DEFAULT_REGISTRATION_CHARGES
        Add ward charges by calculating the number of days for which the patient was in a ward.
        Create a new billing record in the billing table with the total cost.
         */
        Statement stmt = conn.createStatement();
        String treatmentQuery = "SELECT sum(cost) FROM treatment, treatment_cost"+
                " WHERE treatment_cost.treatment_type = treatment.treatment_type AND treatment.mr_id="+mr_id;
        ResultSet rs = stmt.executeQuery(treatmentQuery);
        float treatmentCost = 0;
        if(rs.next()) {
            treatmentCost = rs.getInt(1);
        } else {
            throw new Exception("cannot find treatment records");
        }

        // Query the number of days spent in the ward
        String numberOfDaysQuery = "SELECT datediff(curdate(), checkin_date) AS days FROM medical_records WHERE mr_id="+mr_id;
        rs = stmt.executeQuery(numberOfDaysQuery);
        float numdays = 0;
        if(rs.next()) {
            numdays = rs.getFloat("days");
        }

        // Get the medical record to find the ward to wich the patient was admitted
        String getWardCharges = "SELECT ward_charges.charges FROM medical_records, ward, ward_charges WHERE "+
                "medical_records.mr_id="+mr_id+" AND medical_records.ward_id=ward.ward_id AND ward.ward_type = ward_charges.ward_type"+
                " AND medical_records.checkout_date IS NULL";
        System.out.println(getWardCharges);
        rs = stmt.executeQuery(getWardCharges);
        double wardChargesPerDay = 0;
        if(rs.next()){
            wardChargesPerDay = rs.getDouble(1);
            System.out.println("inside if: "+ wardChargesPerDay);
        }

        // Total cost is the cost of the treatments, ward costs and registration charges.
        double totalCost = treatmentCost + numdays*wardChargesPerDay + DEFAULT_REGISTRATION_CHARGES;
        System.out.println("treatment cost: "+ treatmentCost);
        System.out.println("Number of days:"+numdays);
        System.out.println("ward_charges per day = "+wardChargesPerDay);
        System.out.println("total ward charges: "+  numdays*wardChargesPerDay);
        System.out.println("total cost: "+totalCost);
        System.out.println("Medical record ID: "+mr_id);

        String insertQuery = "INSERT INTO billing (mr_id, total_cost, payment_status) VALUES ("+mr_id+","+totalCost+", "+STATUS_UNPAID+")";
        stmt.executeUpdate(insertQuery);
    }


    // Interactively gets the pending bills for the patient and processes payment.
    public static void viewAndPayBill(Connection conn) throws Exception{
        /*
        Input:
            Connection to the database

        1. Obtains the patient ID from the console.
        2. Displays all the pending bills for the patient.
        3. For the bill which is to be paid, obtain the payment information.
        4. If the payment information is card, obtain the card number
        5. If card number inputted is invalid, roll back the transaction.
        6. Update the payment details
        7. Update the bill and set the status to paid
        Transaction part:
            1. Get the payment information and the card number if requried
            2. update payment details
            3. Update bill and set status to paid.
         */

        // Get the ID of the patient from the user
        int patientID = Integer.parseInt(Utils.readAttribute("patientID", "Patient", false));
        // Begin transaction
        conn.setAutoCommit(false);
        try {
            // Query to get all the pending bills to be paid
            String queryPendingBills = "SELECT DISTINCT bill_id, total_cost FROM billing, medical_records, patient WHERE " +
                    "billing.mr_id=medical_records.mr_id AND billing.payment_status=" + STATUS_UNPAID + " AND medical_records.patient_id=" + patientID;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryPendingBills);
            // check if there are any pending bills to be paid by the user
            boolean printed = false;
            while (rs.next()) {
                // display details of all the pending bills
                System.out.println("---");
                System.out.println("BILL ID: "+rs.getInt("bill_id"));
                System.out.println("TOTAL COST: "+rs.getDouble("total_cost"));
                System.out.println("---");
                printed = true;
            }
            // if no pending bills, return
            if(!printed) {
                System.out.println("No pending bills available");
                return;
            }
            //Get the bill which is to be paid now
            int billingID = Integer.parseInt(Utils.readAttribute("billing ID", "Bill to be paid", false));

            // Get payment type information for the patient
            System.out.println("Enter your payment type:\n 1) Credit Card\n 2) Debit Card\n 3) Cash\n 4) Check\n");
            Scanner sc = new Scanner(System.in);
            String payment_type;
            switch(sc.nextInt()) {
                case 1:
                    payment_type = PAYMENT_TYPE_CREDIT_CARD;
                    break;
                case 2:
                    payment_type = PAYMENT_TYPE_DEBIT_CARD;
                    break;
                case 3:
                    payment_type = PAYMENT_TYPE_CASH;
                    break;
                case 4:
                    payment_type = PAYMENT_TYPE_CHECK;
                    break;
                default:
                    System.out.println("Invalid payment type");
                    return;
            }
            // update query to update the billing information
            String updateBilling = "UPDATE billing SET payment_status = "+STATUS_PAID+", payment_type = '"+payment_type
                    +"'";
            if(payment_type == PAYMENT_TYPE_CREDIT_CARD || payment_type == PAYMENT_TYPE_DEBIT_CARD) {
                // Get the credit card number for the bill
                String cardInfo = Utils.readAttribute("credit card", "Bill", false);
                // Regular expression for credit card number
                Pattern p = Pattern.compile("^(\\d{4}\\-){3}\\d{4}$");
                Matcher m = p.matcher(cardInfo);
                if(m.find()) {
                    updateBilling = updateBilling + ", card='"+cardInfo+"'";
                } else {
                    System.out.println("Invalid card information. Payment failed");
                    conn.rollback();
                    return;
                }
            }
            updateBilling = updateBilling + " WHERE bill_id="+billingID;
            System.out.println(updateBilling);
            Statement s = conn.createStatement();
            s.executeUpdate(updateBilling);
            conn.commit();
        } catch (SQLException e) {
            // Error during execution of the payment
            System.out.println("Error processing payment");
            //System.out.println(e.getMessage());
            // Roll back the transaction.
            conn.rollback();
        }
        conn.setAutoCommit(true);
    }
}
