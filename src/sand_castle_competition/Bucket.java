package sand_castle_competition;

public class Bucket {
    private int id;
    private double angle;
    private double volume;

    public Bucket(int id, double angle, double volume) {
        this.id = id;
        this.angle = angle;
        this.volume = volume;
    }

    public int getId() {
        return id;
    }

    public double getAngle() {
        return angle;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return  id + ": kat zsypu=" + angle + ", objetosc=" + volume;
    }

}
