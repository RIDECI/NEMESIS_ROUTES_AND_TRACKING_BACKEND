package edu.dosw.rideci.infrastructure.persistance.repository;


import org.springframework.stereotype.Component;



@Component
public class GeolocationUtils {

    private static final int EARTH_RADIUS = 6371000;

    public double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2){

        Double dLat = Math.toRadians(lat2-lat1);
        
        Double dLon = Math.toRadians(lon2-lon1);
        
        Double a = Math.pow(Math.sin(dLat/2), 2) + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.pow(Math.sin(dLon/2),2);
        
        Double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return EARTH_RADIUS * c;
    }

    public double distanceToSegment(double pLat, double pLon, double startLat, double startLon, double endLat, double endLon){
        double x = pLon;
        double y = pLat;
        double x1 = startLon;
        double y1 = startLat;
        double x2 = endLon;
        double y2 = endLat;

        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;

        if (len_sq != 0) 
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        return calculateDistanceInMeters(pLat, pLon, yy, xx);
    }
}
