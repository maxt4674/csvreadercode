package redwire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CsvReader {
    private ArrayList<String> headers = new ArrayList<>();
    private ArrayList<ArrayList<String>> data = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> analysedData = new HashMap<>();

    private final File CSV_FILE;

    CsvReader(String filename){
        this.CSV_FILE = new File(filename);
    }

    public ArrayList<String> getHeaders(){
        return this.headers;
    }

    public ArrayList<ArrayList<String>> getData(){
        return this.data;
    }

    public HashMap<String, HashMap<String, String>> getAnalysedData(){
        return this.analysedData;
    }

    public boolean analyseData(String analysisParams){
        String[] params = analysisParams.split(",");
        for(int i = 0; i < params.length; i++){
            String[] paramDiv = params[i].split("\\|");
            if(paramDiv[0].equals("SUM")) {
                HashMap<String, String> sums = new HashMap<>();
                for(int c = 1; c < paramDiv.length; c++){
                    sums.put(paramDiv[c], String.valueOf(getSum(paramDiv[c])));
                }
                analysedData.put("SUM", sums);
                
            } else if(paramDiv[0].equals("COUNT")) {
                for(int c = 1; c < paramDiv.length; c++){
                    analysedData.put("COUNT-"+paramDiv[c], getCount(paramDiv[c]));
                }
            } else if(paramDiv[0].equals("AVG")) {
                HashMap<String, String> avg = new HashMap<>();
                for(int c = 1; c < paramDiv.length; c++){
                    avg.put(paramDiv[c], String.valueOf(getAverage(paramDiv[c])));
                }
                analysedData.put("AVG", avg);

            } else if(paramDiv[0].equals("CORREL")) {
                HashMap<String, String> cor = new HashMap<>();
                for(int c = 1; c < paramDiv.length; c++){
                    cor.put(paramDiv[c], String.valueOf(getCorrelation(paramDiv[c])));
                }
                analysedData.put("CORRELATION", cor);
            }
        }
        return true;
    }

    public boolean isValidCSV(){
        if(!hasHeader()){
            return false;
        }

        if(!hasData()){
            return false;
        }

        return true;
    }

    private boolean hasHeader(){
        ArrayList<String> headerList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE));
            String row = "";
            row = reader.readLine();
            reader.close();
            if(row.length() < 1){
                return false;
            } else {
                String[] data = row.split(",");
                for(int i = 0; i < data.length; i++){
                    headerList.add(data[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for(int i = 0; i < headerList.size(); i++){
            Set<String> seen = new HashSet<>();

            for (String header : headerList) {
                if (header == null || header.trim().isEmpty()) {
                    return false;
                }

                if (!seen.add(header.trim())) {
                    return false;
                }

                if (isNumeric(header) || isValidDate(header)) {
                    return false;
                }
            }
        }

        for(int i = 0; i < headerList.size(); i++){
            headers.add(headerList.get(i).trim());
        }

        return true;
    }

    private static boolean isValidDate(String str) {
        String[] formats = {"yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy"};

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                sdf.parse(str);
                return true;
            } catch (ParseException ignored) {
            }
        }

        return false;
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean hasData(){
        ArrayList<ArrayList<String>> decodeddata = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE));
            String row = "";
            row = reader.readLine();
            
            while((row = reader.readLine()) != null){
                ArrayList<String> datarow = new ArrayList<>();
                String[] data = row.split(",");
                for(int i = 0; i < data.length; i++){
                    datarow.add(data[i]);
                } 
                decodeddata.add(datarow);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(decodeddata.size() < 1){
            return false;
        }

        for(int i = 0; i < decodeddata.size(); i++){
            ArrayList<String> row = new ArrayList<>();
            for(int c = 0; c < decodeddata.get(i).size(); c++){
                if(decodeddata.get(i).get(c).length() < 1) {
                    row.add(null);
                } else {
                    row.add(decodeddata.get(i).get(c).trim());
                }
            }

            if(row.size() != headers.size()){
                return false;
            }
            data.add(row);
        }

        return true;
    }

    private float getSum(String param){
        float sum = -1;

        if(getParamIndex(param) != -1){
            int indexAt = getParamIndex(param);
            if(isNumeric(data.get(0).get(indexAt))){
                sum = 0;
                for(int i = 0; i < data.size(); i++){
                    sum += Float.valueOf(data.get(i).get(indexAt));
                }
            }
        }

        return sum;
    }

    private HashMap<String, String> getCount(String param){
        HashMap<String, String> count = new HashMap<>();
        ArrayList<String> seenVars = new ArrayList<>();

        int indexAt = getParamIndex(param);
        for(int i = 0; i < data.size(); i++){
            String val = data.get(i).get(indexAt);
            boolean isSeen = false;
            for(int c = 0; c < seenVars.size(); c++){
                if(val.equals(seenVars.get(c))){
                    isSeen = true;
                }
            }

            if(!isSeen){
                seenVars.add(val);
            }
        }

        int total = 0;
        for(int i = 0; i < seenVars.size(); i++){
            total = 0;
            for(int c = 0; c < data.size(); c++){
                if(seenVars.get(i).equals(data.get(c).get(indexAt))){
                    total += 1;
                }
            }
            count.put(seenVars.get(i), String.valueOf(total));
        }

        return count;
    }

    private float getAverage(String param){
        float average = -1;

        if(getSum(param) != -1){
            average = 0;
            average = getSum(param) / data.size();
        }

        return average;
    }

    private float getCorrelation(String param){
        float correl = -1;

        return correl;
    }

    private int getParamIndex(String param){
        int index = -1;

        for(int i = 0; i < headers.size(); i++){
            if(param.equals(headers.get(i))){
                index = i;
            }
        }

        return index;
    }

}
