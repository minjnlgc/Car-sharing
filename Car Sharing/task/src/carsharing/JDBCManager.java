package carsharing;

import java.sql.*;

public class JDBCManager {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static String DB_URL = "jdbc:h2:./src/carsharing/db/";

    public static void process(String[] args) throws ClassNotFoundException, SQLException {
        String dataBaseName = "carsharing";
        String setDBName = "-databaseFileName";

        Class.forName(JDBC_DRIVER);

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(setDBName)) {
                dataBaseName = args[i+1];
            }
        }

        DB_URL = DB_URL + dataBaseName;

        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        Statement st = conn.createStatement();

        String sql_company = "CREATE TABLE IF NOT EXISTS COMPANY " +
                "(ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "NAME VARCHAR(255) UNIQUE NOT NULL)";

        String sql_car = "CREATE TABLE IF NOT EXISTS CAR " +
                "(ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "NAME VARCHAR(255) UNIQUE NOT NULL," +
                "COMPANY_ID INT NOT NULL," +
                "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))";

        String sql_customer = "CREATE TABLE IF NOT EXISTS CUSTOMER " +
                "(ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "NAME VARCHAR(255) UNIQUE NOT NULL," +
                "RENTED_CAR_ID INT DEFAULT NULL," +
                "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))";

        st.executeUpdate(sql_company);
        st.executeUpdate(sql_car);
        st.executeUpdate(sql_customer);

        st.close();
        conn.close();
    }

    public static boolean printCompanyList() throws SQLException {

        boolean isEmpty = false;

        String sql = "SELECT NAME FROM COMPANY ORDER BY ID";

        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        if (!rs.isBeforeFirst()) {
            System.out.println("The company list is empty!");
            isEmpty = true;
        } else {
            System.out.println("Choose the company:");
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("NAME");
                System.out.println(i + ". " + name);
                i++;
            }
            System.out.println("0. Back");
        }

        rs.close();
        stmt.close();
        conn.close();

        return isEmpty;

    }

    public static void printCarList(int COMPANY_ID, String companyName, boolean customer_ver) throws SQLException {
        String sql = String.format("SELECT NAME FROM CAR WHERE COMPANY_ID=%d ORDER BY ID", COMPANY_ID);

        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        if (!rs.isBeforeFirst()) {
            if (customer_ver) {
                System.out.printf("No available cars in the '%s' company\n", companyName);
            } else {
                System.out.println("The car list is empty!");
            }

        } else {
            if (customer_ver) {
                System.out.println("Choose a car:");
            } else {
                System.out.printf("%s cars:\n", companyName);
            }
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("NAME");
                System.out.println(i + ". " + name);
                i++;
            }
        }

        rs.close();
        stmt.close();
        conn.close();
    }

    public static boolean printCustomerList() throws SQLException {
        boolean isEmpty = false;
        String sql = "SELECT NAME FROM CUSTOMER ORDER BY ID";

        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        if (!rs.isBeforeFirst()) {
            System.out.println("The customer list is empty!");
            isEmpty = true;
        } else {
            System.out.println("Choose a customer:");
            int i = 1;
            while (rs.next()) {
                String name = rs.getString("NAME");
                System.out.println(i + ". " + name);
                i++;
            }
            System.out.println("0. Back");
        }

        rs.close();
        stmt.close();
        conn.close();

        return isEmpty;
    }

    public static String getCompanyName(int user_option) throws SQLException {
        String companyName = "";
        String sql = String.format("SELECT NAME FROM COMPANY WHERE ID=%d", user_option);
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            companyName = rs.getString("NAME");
        }
        return companyName;
    }

    public static void createCompany(String name) throws SQLException {
        String sql = "INSERT INTO COMPANY (NAME) VALUES (?)";
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public static void createCar(String name, int id) throws SQLException {
        String sql = "INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?)";
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public static void createCustomer(String name) throws SQLException {
        String sql = "INSERT INTO CUSTOMER (NAME) VALUES (?)";
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public static int getCompanyId(String name) throws SQLException {
        int id = -1;
        String sql = "SELECT ID FROM COMPANY WHERE NAME=?";
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);


        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            id = rs.getInt("ID");
        }

        rs.close();
        stmt.close();
        conn.close();

        return id;
    }

    public static void getCustomerRentedCar(int customer_id) throws SQLException {
        String sql = "SELECT CAR.NAME AS CAR_NAME, COMPANY.NAME AS COMPANY_NAME " +
                "FROM CUSTOMER " +
                "JOIN CAR ON CUSTOMER.RENTED_CAR_ID = CAR.ID " +
                "JOIN COMPANY ON CAR.COMPANY_ID = COMPANY.ID " +
                "WHERE CUSTOMER.ID = ?";

        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customer_id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String car_name = rs.getString("CAR_NAME");
            String company_name = rs.getString("COMPANY_NAME");
            System.out.printf("Your rented car:\n" +
                    "%s\n" +
                    "Company:\n" +
                    "%s\n", car_name, company_name);
        } else {
            System.out.println("You didn't rent a car!");
        }

        rs.close();
        stmt.close();
        conn.close();
    }

    public static void customerReturnCar(int customer_id) throws SQLException {
        boolean isRentedCar = JDBCManager.isCustomerRentCar(customer_id);

        String sql = "UPDATE CUSTOMER SET RENTED_CAR_ID=NULL WHERE ID=?";

        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customer_id);

        stmt.executeUpdate();

        if (!isRentedCar) {
            System.out.println("You didn't rent a car!");
        } else {
            System.out.println("You've returned a rented car!");
        }
    }

    public static boolean isCustomerRentCar(int customer_id) throws SQLException {
        String sql = "SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID=?";
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, customer_id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int rented_car_id = rs.getInt("RENTED_CAR_ID");
            return (rented_car_id != 0);
        } else {
            return false;
        }
    }

    public static String getRentCarNameByCompany(int company_id, int sequence) throws SQLException {
        String car_name = "";

        String sql = "SELECT NAME FROM CAR WHERE COMPANY_ID=?";
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, company_id);

        ResultSet rs = stmt.executeQuery();

        for (int i = 1; i <= sequence && rs.next(); i++) {
            if (i == sequence) {
                car_name = rs.getString("NAME");
            }
        }

        return car_name;
    }

    public static void customerRentCar(int company_id, int customer_id, String car_name) throws SQLException {
        String sql = "UPDATE CUSTOMER SET RENTED_CAR_ID=? WHERE ID=?";

        Connection conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);



        String sql_car = "SELECT ID FROM CAR WHERE NAME=? AND COMPANY_ID=?";
        PreparedStatement carStmt = conn.prepareStatement(sql_car);
        carStmt.setString(1, car_name);
        carStmt.setInt(2, company_id);

        ResultSet carRs = carStmt.executeQuery();
        int car_id = -1;

        if (carRs.next()) {
            car_id = carRs.getInt("ID");
        } else {
            System.out.println("Car not found!");
            return;
        }

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, car_id);
        stmt.setInt(2, customer_id);

        int rowUpdated = stmt.executeUpdate();

        if (rowUpdated == 0) {
            System.out.println("You didn't rent a car!");
        } else {
            System.out.println("You rented '" + car_name + "'");
        }
    }
}
