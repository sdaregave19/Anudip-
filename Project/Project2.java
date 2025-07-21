import java.sql.*;
import java.util.Scanner;

public class Project2 {
    private static final String URL = "jdbc:mysql://localhost:3306/Project";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); 
            System.out.println("Connected to database!");

            createTable(conn);
            Scanner sc = new Scanner(System.in);
            int ch;
            do {
                System.out.println("\n---------------------------- MENU ----------------------------");
                System.out.println("1. ADD STUDENT");
                System.out.println("2. VIEW TABLE");
                System.out.println("3. UPDATE STUDENT AGE");
                System.out.println("4. DELETE STUDENT BY ID");
                System.out.println("5. SELECT STUDENT BY ID");
                System.out.println("0. EXIT");
                System.out.print("Enter your choice: ");
                ch = sc.nextInt();

                switch (ch) {
                    case 1 -> {
                        System.out.print("Enter the number of students to add: ");
                        int n = sc.nextInt();
                        for (int i = 0; i < n; i++) {
                            System.out.print("Enter student_id: ");
                            int student_id = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Enter name: ");
                            String name = sc.nextLine();
                            System.out.print("Enter email: ");
                            String email = sc.nextLine();
                            System.out.print("Enter age: ");
                            int age = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Enter department: ");
                            String department = sc.nextLine();
                            System.out.print("Enter city: ");
                            String city = sc.nextLine();

                            insertData(conn, student_id, name, email, age, department, city);
                        }
                    }
                    case 2 -> retrieveData(conn);
                    case 3 -> {
                        System.out.print("Enter student_id to update: ");
                        int id = sc.nextInt();
                        System.out.print("Enter new age: ");
                        int new_age = sc.nextInt();
                        updateData(conn, id, new_age);
                    }
                    case 4 -> {
                        System.out.print("Enter student_id to delete: ");
                        int id = sc.nextInt();
                        deleteData(conn, id);
                    }
                    case 5 -> {
                        System.out.print("Enter student_id to view: ");
                        int id = sc.nextInt();
                        selectViaId(conn, id);
                    }
                    case 0 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice.");
                }

            } while (ch != 0);

            conn.close();
            System.out.println("Database connection closed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                     "student_id INT PRIMARY KEY, " +
                     "name VARCHAR(50), " +
                     "email VARCHAR(100) UNIQUE, " +
                     "age INT, " +
                     "department VARCHAR(50), " +
                     "city VARCHAR(50)" +
                     ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'students' created or already exists.");
        }
    }

    private static void insertData(Connection conn, int student_id, String name, String email, int age, String department, String city) throws SQLException {
        String sql = "INSERT INTO students (student_id, name, email, age, department, city) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student_id);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setInt(4, age);
            pstmt.setString(5, department);
            pstmt.setString(6, city);
            pstmt.executeUpdate();
            System.out.println("Student inserted: " + name);
        }
    }

    private static void retrieveData(Connection conn) throws SQLException {
        String sql = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.printf("\n%-10s %-15s %-25s %-5s %-15s %-15s\n", "ID", "Name", "Email", "Age", "Department", "City");
            System.out.println("----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-15s %-25s %-5d %-15s %-15s\n",
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getString("department"),
                        rs.getString("city"));
            }
        }
    }

    private static void updateData(Connection conn, int id, int new_age) throws SQLException {
        String sql = "UPDATE students SET age = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, new_age);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Updated age for student ID: " + id);
            } else {
                System.out.println("No student found with ID: " + id);
            }
        }
    }

    private static void deleteData(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Deleted student with ID: " + id);
            } else {
                System.out.println("No student found with ID: " + id);
            }
        }
    }

    private static void selectViaId(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nStudent Details:");
                    System.out.println("ID: " + rs.getInt("student_id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Age: " + rs.getInt("age"));
                    System.out.println("Department: " + rs.getString("department"));
                    System.out.println("City: " + rs.getString("city"));
                } else {
                    System.out.println("No student found with ID: " + id);
                }
            }
        }
    }
}