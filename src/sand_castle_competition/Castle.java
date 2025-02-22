package sand_castle_competition;

import java.util.ArrayList;

public class Castle extends Place {
    private double height;
    private double current_radius; //radius of avaiable place for new layer
    private ArrayList<double[]> data = new ArrayList<>(); //id of buckets and height of each segment


    public Castle(int id, double height, double radius) {
        super(id,radius);
        this.height = height;
        this.current_radius = radius;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCurrentRadius() {
        return current_radius;
    }
    public void setCurrentRadius(double current_radius) {
        this.current_radius = current_radius;
    }

    //bucket from which segment is built and layer height
    public void addSegment(double[] segment){
        data.add(segment);
    }

    @Override
    public String toString() {
        StringBuilder a = new StringBuilder();
        a.append("zamek: ").append(getId())
                .append(": wysokosc: ").append(height)
                .append("\nBudowa:\n");

        for (double[] segment : data) {
            a.append("id wiaderka: ").append((int) segment[0])
                    .append(", wysokosc segmentu: ").append(segment[1])
                    .append("\n");
        }

        return a.toString();
    }
}
