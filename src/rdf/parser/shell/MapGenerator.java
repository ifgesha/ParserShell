package rdf.parser.shell;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.*;


public class MapGenerator {

    private Database db;
    private static Log log = new Log();


    public void getShema(){

        String out = "";
        int t = 0;

        db.openConnection();

        try{
            ResultSet rsTable = db.query("SHOW TABLES"); // Получить список таблиц
            while (rsTable.next()) {
                String tName = rsTable.getString(1);

                log.info("Process table " + tName);


                // Получить строение таблици
                ResultSet rsTableCrata = db.query(" SHOW CREATE TABLE `"+tName+"`");
                while (rsTableCrata.next()) {
                    t++;
                    String tCreate = rsTableCrata.getString(2);
                    //log.info(" SHOW CREATE TABLE " + tCreate);

                    out += getTriplet(t, tName, tCreate);

                }


            }


        }catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

        log.info(out);


    }




    private static String getTriplet(int t, String tName, String tCreate){
        String resultTriplet = "";
        String resTriplet = "";
        String pkey = "";

        Pattern pItem = Pattern.compile("  `(.+)` .+");
        Pattern pK = Pattern.compile("PRIMARY KEY \\(`(.+)`\\),");

        String[] lines = tCreate.split("\\r?\\n");
        for (String str: lines) {
            Matcher m = pItem.matcher(str);
            if( m.matches()){
                resTriplet +="\n";
                resTriplet +="	rr:predicateObjectMap [\n";
                resTriplet +="		rr:predicate ex:\""+m.group(1)+"\";\n";
                resTriplet +="		rr:objectMap [ rr:column \""+m.group(1)+"\" ];\n";
                resTriplet +="	];\n";
            }

            Matcher k = pK.matcher(str);
            if( k.matches()) {
                pkey = k.group(1);
            }

        }

        resultTriplet +="\n";
        resultTriplet +="<#TriplesMap"+t+">\n";
        resultTriplet +="	a rr:TriplesMap;\n";
        resultTriplet +="	rr:logicalTable [ rr:tableName  \""+tName+"\" ];\n";
        resultTriplet +="	rr:subjectMap [\n";
        resultTriplet +="		rr:template \"http://data.example.com/"+tName+"/{\""+pkey+"\"}\";\n";
        resultTriplet +="		rr:class <http://example.com/ontology/"+tName+">;\n";
        resultTriplet +="		rr:graph <http://example.com/graph/"+tName+"> ;\n";
        resultTriplet +="	];\n";
        resultTriplet += resTriplet;
        resultTriplet +="	.\n\n";



        return resultTriplet;
    }








    public void setDb(Database db) {
        this.db = db;
    }
}
