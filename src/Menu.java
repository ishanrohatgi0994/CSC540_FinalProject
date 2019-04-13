import java.sql.Connection;
import java.util.Scanner;

public class Menu {
	Scanner sc = new Scanner(System.in);
	public void displayOptions(Connection conn) {
		boolean flag = true;
		while(flag) {
			System.out.println("\n Login as: 1) Operator \n 2) Nurse \n 3) Doctor \n 4) Admin \n 5) Exit \n Enter your choice : (1/2/3/4/5) \n");
		
			try {
				int choice  = sc.nextInt();
				switch(choice) {
				case 1:
					Operator o = new Operator();
					o.displayOperatorOptions(conn);
					break;
				case 2:
					Nurse n = new Nurse();
					n.displayNurseOptions(conn);
					break;
				case 3:
					Doctor d = new Doctor();
					d.displayDoctorOptions(conn);
					break;
				case 4:
					Admin ad = new Admin();
					ad.displayAdminOptions(conn);
					break;
				case 5:
					flag = false;
					break;
				default:
					System.out.println("Invalid Input");
				}
			}	
			catch (Exception e) {
				System.out.println("Invalid Input");
			}
		}
	}
}
