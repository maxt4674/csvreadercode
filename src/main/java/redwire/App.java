package redwire;

import java.util.HashMap;
import java.util.Map;

public class App 
{
    public static void main( String[] args )
    {
        CsvReader csvReader = new CsvReader("default.csv");

        if(csvReader.isValidCSV()){
            System.out.println("Valid CSV!");
            for(int i = 0; i < csvReader.getHeaders().size(); i++){
                System.out.println("\t"+(i+1)+"-"+csvReader.getHeaders().get(i));
            }
            //SUM, COUNT DISTINCT, Average, Correlation
            csvReader.analyseData("COUNT|Age|Salary|Name,AVG|Age|Salary,SUM|Age|Salary|Email");
            HashMap<String, HashMap<String, String>> data = csvReader.getAnalysedData();
            for(Map.Entry<String, HashMap<String, String>> entry : data.entrySet()){
                String outerKey = entry.getKey();
                HashMap<String, String> innerMap = entry.getValue();

                System.out.println("Argument: " + outerKey);
                
                for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                    String innerKey = innerEntry.getKey();
                    String innerValue = innerEntry.getValue();
                    System.out.println("\t" + innerKey + ": " + innerValue);
                }
            }
        } else {
            System.out.println("Invalid CSV, must have header and/or have each data point filled in not blank");
        }
    }
}
