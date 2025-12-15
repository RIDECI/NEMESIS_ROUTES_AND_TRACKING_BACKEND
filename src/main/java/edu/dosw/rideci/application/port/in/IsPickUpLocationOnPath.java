package edu.dosw.rideci.application.port.in;

public interface IsPickUpLocationOnPath {
    
    boolean isPickUpLocationOnPath(double pickUpPointLat, double pickUpPointLon, String encodedPolyline, double toleranceMeters);

}
