package controller;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:db_juego.db";

    static {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS resultados (" +
                    "id_resultado INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre_partida VARCHAR(30) NOT NULL," +
                    "nombre_jugador1 VARCHAR(40) NOT NULL," +
                    "nombre_jugador2 VARCHAR(40) NOT NULL," +
                    "ganador VARCHAR(40) NOT NULL," +
                    "punto INTEGER NOT NULL," +
                    "estado VARCHAR(10) NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertarResultado(String nombrePartida, String nombreJugador1, String nombreJugador2, String ganador, int punto, String estado) {
        String sql = "INSERT INTO resultados (nombre_partida, nombre_jugador1, nombre_jugador2, ganador, punto, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombrePartida);
            pstmt.setString(2, nombreJugador1);
            pstmt.setString(3, nombreJugador2);
            pstmt.setString(4, ganador);
            pstmt.setInt(5, punto);
            pstmt.setString(6, estado);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void actualizarResultado(String nombrePartida, String ganador, int punto, String estado) {
        String sql = "UPDATE resultados SET ganador = ?, punto = ?, estado = ? WHERE nombre_partida = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ganador);
            pstmt.setInt(2, punto);
            pstmt.setString(3, estado);
            pstmt.setString(4, nombrePartida);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static {
        createNewDatabase();
        createNewTable();
    }

    public static void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS resultados (\n"
                + "	id_resultado INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	nombre_partida TEXT NOT NULL,\n"
                + "	nombre_jugador1 TEXT NOT NULL,\n"
                + "	nombre_jugador2 TEXT NOT NULL,\n"
                + "	ganador TEXT NOT NULL,\n"
                + "	punto INTEGER NOT NULL,\n"
                + "	estado TEXT NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}


