package redwire;

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
        } else {
            System.out.println("Invalid CSV, must have header and/or have each data point filled in not blank");
        }
    }
}
