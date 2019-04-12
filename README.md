# CSC540_FinalProject
Report 1 - https://docs.google.com/document/d/1cH1awcHuf49GOszxKEC0APxEkQnTASiuJZ-Td3M0mlg/edit?ts=5c57848d
Report 2 - https://docs.google.com/document/d/1xFFqmwsZ_ZNl-Thc1VKYRfVdYs77tCFFYw-S7CoVwwY/edit?ts=5c7b1dc5
Report 3 - https://docs.google.com/document/d/1SoEyAZ9Wm1m5FXWDKL2VgWWZg5o7IGgy4w5BDVFyhNg/edit?ts=5ca13398

Tasks & Operations

Operator
---
1. Add Patient (Aniruddha)
2. Add Medical Record(Check -In) for a Patient" (Harsh)
3. Update Nurse \n (Chinmai)
4) Update Doctor \n (Ishan)
5) Update Patient " (Aniruddha)
6. Update Ward \n  (Ishan)
7) Update Medical Record" (Harsh)
8. Delete Nurse \n (Chinmai)
9) Delete Doctor \n (Ishan)
10) Delete Patient" (Aniruddha)
11. Delete Ward \n (Ishan)
12) Assign Patient to Ward \n (Aniruddha)
13) Checkout Patient \n (Aniruddha)
14) View Reports (NOT ASSIGNED)

Nurse
---
1) Update Medical Record (Harsh)
2) Enter Treatment (Test) Details " (Chinmai)
3) View Managed Ward Information (Ishan)
4) View Treatment Details " (Chinmai)
5) View Medical Record for Patient"); (Harsh)

Doctor
---
1) View Patient's Medical History(for given data range) (Aniruddha)
2) View Ward Information " (Ishan)
3) View Current Treatment Details (Chinmai)
4) Add Treatment" (Chinmai)
5) Update Treatment" (Chinmai)
6) Update Medical Record" (Harsh)

Admin
---
1) Add Doctor (Ishan)
2) Add Nurse (Chinmai)
3) Add Operator (Harsh)
4) Add Ward" (Ishan)
5) Assign Nurse to ward \n\ (Chinmai)
6) Update Operator (Harsh)
7) Delete Operator (Harsh)
8) View Reports" (NOT ASSIGNED)

32 operations excluding view reports

Reporting tasks
---
1. Generate Patient Medical History Report (Aniruddha)
2. Generate Ward usage History for particular ward(Harsh)
3. Generate current ward usage percentage (Chinmai)
4. All information of patients currently being treated by a given doctor (Ishan)
5. Get all staff by role (Aniruddha)
6. Get current ward availability status for all wards(Harsh)

Additional APIs
---
1. Get doctor information by IDs (LIST OF IDS) (Ishan)
2. Get nurse by IDs (Chinmai)
3. Get patient by IDs (Aniruddha)
4. Get operator by IDs (Harsh)
5. Get ward by IDs (Chinmai)
6. Pay bill API (just add valid credit card number information to the billing record, set payment status to paid and rollback if any error. (NOT ASSIGNED)
7. Delete treatment
8. view billing information
9. Get Ward Availability by Ward Type (Harsh)
10. Reduce Ward capacity once assigned to a patient (Harsh)
11. Show all wards information (Harsh)

Pending Tasks
---
### Encapsulate program in do-while logic for continuous execution until user wants to exit. test entire program.

### Document execution of all the functions in the code

### High level design desicion in document.

### Display appropriate tasks for appropriate type of users.

## Aniruddh
1. Change get ward logic
2. Update 2nd transaction in document based on 1
3. Add 3rd transaction in the document. (cardAuthorization)
4. Make use of view doctor by IDs in view medical history.
5. Patient and treatment data dump
6. Check if patient is already present before adding
7. billing table schema change.
8. change paybill api to accoomodate new billing schema

## Harsh
1. Ward charges data dump
2. Code comments.
3. Current Ward usage percentage report.

## Chinmai
1. Get all nurses by role
2. Handle null logic in operators.
4. medical record data dump
5. check if nurse is present before inserting

## Ishan
1. Get all staff by role
2. Get all doctors by role
3. write documentation in code
4. Billing data dump
5. check if doctor is present before inserting
6. All information of patients currently being treated by a given doctor (Ishan)
7. Number patients per month.
