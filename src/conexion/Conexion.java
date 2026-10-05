
package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // DATOS DE MYSQL. Se pueden configurar por variables de entorno para
    // ejecutar la aplicación dentro de Docker o contra una base externa.
    private static final String HOST = variable("DB_HOST", "localhost");
    private static final String PORT = variable("DB_PORT", "3306");
    private static final String DATABASE = variable("DB_NAME", "economia_db");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = variable("DB_USER", "root");
    private static final String PASSWORD = variable("DB_PASSWORD", "root");

    private static String variable(String nombre, String valorPredeterminado) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? valorPredeterminado : valor;
    }

    // MÉTODO DE CONEXIÓN
    public static Connection conectar() {

        Connection conn = null;

        try {

            // Cargar driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Conectar
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Conexión exitosa a MySQL");

        } catch (ClassNotFoundException e) {

            System.out.println("Driver no encontrado");
            System.out.println(e.getMessage());

        } catch (SQLException e) {

            System.out.println("Error de conexión");
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
