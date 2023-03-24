package carsharing;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        JDBCManager.process(args);
        Menu.start(sc);

    }
}