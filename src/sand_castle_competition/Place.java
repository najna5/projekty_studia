package sand_castle_competition;

public class Place {
    private int id;
    private double radius;

    public Place (int id, double radius) {
        this.id = id;
        this.radius = radius;
    }
    public int getId() {
        return id;
    }
    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return id + ": promien=" + radius;
    }
}
