package sand_castle_competition;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Maths {

    public double[] MAX (int wH, int wV,List<Bucket> buckets,List<Castle> castles){
        double height = 0;
        double volume = 0;
        for(Castle c: castles) {
            height += c.getHeight();
        }
        height = height / castles.size();
        for(Bucket b: buckets) {
            volume += b.getVolume();
        }
        double[] result = new double[3];
        result[0] = (((double) wH / (wH + wV))* height + ((double) wV / (wH + wV))*volume);
        result[1] = height;
        result[2] = volume;
        return result;
    }


    //"r" is radius of base place, and "a" is an angle from bucket data
    private double[] segmentHeight(double r, double a, double shovel){
        double[] result = new double[2];
        //change "a" to radians
        double aR = a*(Math.PI/180);
        //height of a cone
        double height = r * Math.tan(aR);
        //calculating what would be a volume of a cone with chosen sand
        double volume = (1.0/3) * Math.PI * Math.pow(r,2) * height;
        //if it's bigger than shovel capacity we'd cut the top


        if (volume > shovel){
            double newVol = volume - shovel;
            double R = Math.cbrt((3*newVol)/(Math.PI * Math.tan(aR)));
            result[0] = R;
            result[1] = (r-R)*Math.tan(aR);
        }else{ //assumption that we check if there's enough sand in the bucket
            result[0] = 0;
            result[1] = height;
        }
        return result;
    }
    //result is [radius of top circle, height to add to castle]

    /*
    not extremely efficient, but function to check if there isn't much sand in buckets.
    in buildCastles() it compares the steepest and the least steep castle
    if there is not much sand, the castle won't be finished and the least steep castles will be wrongly smaller
    */
    //n = 0 - lowest tower, n = 1 highest
    private double extremum(List<Bucket> buckets, List<Castle> castles, double shovel, int n) {
        ArrayList<Bucket> tempBuckets = new ArrayList<>();
        for (Bucket bucket : buckets) {
            tempBuckets.add(new Bucket(bucket.getId(), bucket.getAngle(), bucket.getVolume()));
        }
        ArrayList<Castle> tempCastles = new ArrayList<>();
        for (Castle castle : castles) {
            tempCastles.add(new Castle(castle.getId(),castle.getHeight(),castle.getRadius()));
        }

        while (true) {
            boolean anyCastleUpdated = false;

            for (Castle castle : tempCastles) {
                if (castle.getCurrentRadius() <= 0) {
                    continue;
                }

                if(n == 0) {
                    tempBuckets.sort(Comparator.comparingDouble(Bucket::getAngle));
                }else if (n == 1){
                    tempBuckets.sort(Comparator.comparingDouble(Bucket::getAngle).reversed());
                }

                for (Bucket bucket : tempBuckets) {
                    if (bucket.getVolume() < shovel) {
                        continue;
                    }

                    double[] segment = segmentHeight(castle.getCurrentRadius(), bucket.getAngle(), shovel);

                    castle.setCurrentRadius(segment[0]);
                    castle.setHeight(castle.getHeight() + segment[1]);
                    bucket.setVolume(bucket.getVolume() - shovel);
                    castle.addSegment(new double[]{bucket.getId(), segment[1]});
                    anyCastleUpdated = true;
                }
            }
            if (!anyCastleUpdated) {
                break;
            }
        }
        double height = 0;
        for(Castle c: tempCastles) {
            height += c.getHeight();
        }
        height = height / tempCastles.size();
        return height;
    }


    public void buildCastles(List<Bucket> buckets, List<Castle> castles, double shovel, int wH, int wV){
        int wh;
        int wv;
        if (extremum(buckets, castles, shovel,0) > extremum(buckets, castles, shovel,1)) {
            wh = 0;
            wv = 1;
        }else{
            wh = wH;
            wv = wV;
        }
        while(true) {
            boolean anyCastleUpdated = false;

            for (Castle castle : castles) {
                if (castle.getCurrentRadius() <= 0) {
                    continue;  // Skip if the castle is already complete
                }

                Bucket bestBucket = null;
                double bestScore = Double.NEGATIVE_INFINITY;
                double[] bestSegment = null;

                for (Bucket bucket : buckets) {
                    if (bucket.getVolume() < shovel) {
                        continue;  // Skip buckets with insufficient sand
                    }

                    //segment is [radius of top circle, height to add to castle]
                    double[] segment = segmentHeight(castle.getCurrentRadius(), bucket.getAngle(), shovel);

                    /*
                    if height is important then the angle of segment should be bigger so next layer has bigger bottom
                    but if we care more about usage of sand then segment should be more steep
                    */
                    double efficiency = (segment[0]/castle.getCurrentRadius());
                    double score = ((double)wh/(wh+wv)) * efficiency + ((double)wv/(wh+wv)) * (1-efficiency);
                    if (score > bestScore) {
                        bestScore = score;
                        bestBucket = bucket;
                        bestSegment = segment;
                    }
                }

                if (bestBucket != null) {
                    castle.setCurrentRadius(bestSegment[0]);  // New top radius after adding sand
                    castle.setHeight(castle.getHeight() + bestSegment[1]);

                    bestBucket.setVolume(bestBucket.getVolume() - shovel);

                    castle.addSegment(new double[]{bestBucket.getId(), bestSegment[1]});

                    anyCastleUpdated = true;
                }
            }
            if(!anyCastleUpdated){
                break;
            }
        }
    }


}
