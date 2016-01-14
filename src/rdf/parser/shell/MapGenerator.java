package rdf.parser.shell;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Женя on 10.01.2016.
 */
public class MapGenerator {

    private Database db;


    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public void getShema(){
         db.openConnection();

        try {
            ResultSet rs = db.query("SHOW TABLE");


            while (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Total number of books in the table : " + count);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

    }



    public void setDb(Database db) {
        this.db = db;
    }
}
