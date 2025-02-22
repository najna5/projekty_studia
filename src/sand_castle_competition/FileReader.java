package sand_castle_competition;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {

    public static void readFile(String fileName, ArrayList<Place> places, ArrayList<Bucket> buckets) {
        File file = new File(fileName);
        try{
            Scanner scanner = new Scanner(file);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",\\s*");
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                if (parts.length == 3) {
                    int c = Integer.parseInt(parts[2].trim());
                    buckets.add(new Bucket(a, b, c));
                }else {
                    places.add(new Place(a, b));
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            throw new RuntimeException(e);
        }
    }

}
