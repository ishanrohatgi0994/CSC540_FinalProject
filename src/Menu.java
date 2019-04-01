import java.util.Scanner;

public class Menu {
	Scanner sc = new Scanner(System.in);
	public void displayOptions() {
		System.out.println("1) Operator \n 2) Nurse \n 3) Doctor \n 4) Admin \n Enter your choice : (1/2/3/4)");
		try {
			int choice  = sc.nextInt();
			switch(choice) {
			case 1:
				Operator o = new Operator();
				o.displayOperatorOptions();
				break;
			case 2:
				Nurse n = new Nurse();
				n.displayNurseOptions();
				break;
			case 3:
				Doctor d = new Doctor();
				d.displayDoctorOptions();
				break;
			case 4:
				Admin ad = new Admin();
				ad.displayAdminOptions();
				break;
			default:
				System.out.println("Invalid Input. Bye Bye");
			}
			
		}
		catch (Exception e) {
			System.out.println("Invalid Input");
		}
	}
}
