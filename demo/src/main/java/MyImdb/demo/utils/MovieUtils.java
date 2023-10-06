package MyImdb.demo.utils;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class MovieUtils {

    public static List<String[]> readImdbRatingsCsvFile(File file){
        List<String[]> lstImdbIdRatingDate = new ArrayList<>();
        try {
            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] lineFields;
            //read first line (to ignore)
            lineFields = csvReader.readNext();
            // we are going to read data line by line
            while ((lineFields = csvReader.readNext()) != null) {
                System.out.println(lineFields[0] + "\t" + lineFields[1] + "\t" + lineFields[2]);
                lstImdbIdRatingDate.add(new String[] {lineFields[0] ,lineFields[1],lineFields[2]});
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lstImdbIdRatingDate;
    }
}
