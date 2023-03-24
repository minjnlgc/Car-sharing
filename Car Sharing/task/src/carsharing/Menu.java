package carsharing;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    public static void start(Scanner sc) throws SQLException {
        boolean exit_program = false;

        while (!exit_program) {
            System.out.println("1. Log in as a manager\n" +
                    "2. Log in as a customer\n" +
                    "3. Create a customer\n" +
                    "0. Exit");
            int user_option = sc.nextInt();
            sc.nextLine();

            switch (user_option) {
                case 1:
                    Menu.managerMenu(sc);
                    break;
                case 2:
                    Menu.showCustomerList(sc);
                    break;
                case 3:
                    Menu.createCustomer(sc);
                    break;
                case 0:
                    exit_program = true;
                    break;
                default:
                    System.out.println("Invalid operation!");
                    break;
            }
        }
    }


    public static void managerMenu(Scanner sc) throws SQLException {
        boolean exit_program = false;
        while (!exit_program) {
            System.out.println("1. Company list\n" +
                    "2. Create a company\n" +
                    "0. Back");
            int user_option = sc.nextInt();
            sc.nextLine();

            switch (user_option) {
                case 1:
                    Menu.showCompanyList(sc, false);
                    break;
                case 2:
                    Menu.createCompany(sc);
                    break;
                case 0:
                    Menu.start(sc);
                    exit_program = true;
                    break;
                default:
                    System.out.println("Invalid operation!");
            }
        }
    }

    public static void showCustomerList(Scanner sc) throws SQLException {
        do {
            if (JDBCManager.printCustomerList()) {
                break;
            }

            // customer id
            int user_option = sc.nextInt();
            sc.nextLine();

            if (user_option == 0) {
                Menu.start(sc);
            } else {
                Menu.customerMenu(sc, user_option);
            }
        } while (true);
    }


    public static void showCompanyList(Scanner sc, boolean customer_ver) throws SQLException {
        do {
            if (JDBCManager.printCompanyList()) {
                break;
            }

            int user_option = sc.nextInt();
            sc.nextLine();

            if (user_option == 0) {
                if (customer_ver) {
                    Menu.start(sc);
                } else {
                    Menu.managerMenu(sc);
                }
            } else {
                String companyName = JDBCManager.getCompanyName(user_option);
                Menu.companyCarMenu(sc, companyName, user_option);
            }
        } while (true);

    }

    public static void companyCarMenu(Scanner sc, String companyName, int company_ID) throws SQLException {
        boolean exit_program = false;

        System.out.printf("'%s' company:\n", companyName);
        while (!exit_program) {
            System.out.println("1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    JDBCManager.printCarList(company_ID, companyName, false);
                    break;
                case 2:
                    Menu.createCar(sc, companyName);
                    break;
                case 0:
                    Menu.showCompanyList(sc, true);
                    exit_program = true;
                    break;
                default:
                    System.out.println("Invalid operation!");
                    break;
            }
        }
    }

    public static void customerMenu(Scanner sc, int user_option_of_customer) throws SQLException {
        boolean exit_program = false;
        while (!exit_program) {
            System.out.println("1. Rent a car\n" +
                    "2. Return a rented car\n" +
                    "3. My rented car\n" +
                    "0. Back");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    // rent a car
                    Menu.customerRentCarMenu(sc, user_option_of_customer);
                    break;
                case 2:
                    JDBCManager.customerReturnCar(user_option_of_customer);
                    break;
                case 3:
                    JDBCManager.getCustomerRentedCar(user_option_of_customer);
                    break;
                case 0:
                    Menu.showCustomerList(sc);
                    exit_program = true;
                    break;
                default:
                    System.out.println("Invalid operation!");
                    break;
            }
        }
    }

    public static void customerRentCarMenu(Scanner sc, int customer_id) throws SQLException {

        if (JDBCManager.isCustomerRentCar(customer_id)) {
            System.out.println("You've already rented a car!");
        } else {

            do {
                if (JDBCManager.printCompanyList()) {
                    break;
                }

                int user_option = sc.nextInt();
                sc.nextLine();

                if (user_option == 0) {
                    Menu.customerMenu(sc, customer_id);
                } else {
                    Menu.customerCarList(sc, user_option, customer_id);
                }
            } while (true);
        }
    }


    public static void customerCarList(Scanner sc, int user_option_company, int customer_id) throws SQLException {

        String companyName = JDBCManager.getCompanyName(user_option_company);
        int company_id = JDBCManager.getCompanyId(companyName);

        JDBCManager.printCarList(company_id, companyName, true);
        System.out.println("0. Back");

        int option_car = sc.nextInt();
        sc.nextLine();

        if (option_car == 0) {
            return;
        } else {
            String car_name = JDBCManager.getRentCarNameByCompany(company_id, option_car);
            JDBCManager.customerRentCar(company_id, customer_id, car_name);
        }

        Menu.customerMenu(sc, customer_id);
    }

    public static void createCompany(Scanner sc) throws SQLException {
        System.out.println("Enter the company name:");
        String name = sc.nextLine();
        JDBCManager.createCompany(name);
        System.out.println("The company was created!");
    }

    public static void createCar(Scanner sc, String companyName) throws SQLException {
        int companyID = JDBCManager.getCompanyId(companyName);
        System.out.println("Enter the car name:");
        String name = sc.nextLine();

        JDBCManager.createCar(name, companyID);
        System.out.println("The car was added!");

    }

    public static void createCustomer(Scanner sc) throws SQLException {
        System.out.println("Enter the customer name:");
        String name = sc.nextLine();
        JDBCManager.createCustomer(name);
        System.out.println("The customer was added!");
    }
}
