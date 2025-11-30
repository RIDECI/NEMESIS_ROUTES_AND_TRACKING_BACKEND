package edu.dosw.rideci.infrastructure.persistance.repository;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.exceptions.ExternalServiceException;
import edu.dosw.rideci.infrastructure.config.GoogleMapsConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GoogleMapsAdapter implements MapsServicePort {

    private final GoogleMapsConfig googleMapsConfig;

    public Route calculateRoute(Location origin, Location destination) {
        try {

            DirectionsResult result = DirectionsApi.newRequest(googleMapsConfig.geoApiContext())
                    .origin(origin.getAddress())
                    .destination(origin.getAddress())
                    .mode(TravelMode.DRIVING)
                    .await();

            if (result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];
                DirectionsLeg leg = route.legs[0];

                return Route.builder()
                        .totalDistance(leg.distance.inMeters)
                        .estimatedTime(leg.duration.inSeconds)
                        .polyline(route.overviewPolyline.getEncodedPath())
                        .build();
            }

        } catch (Exception e) {
            throw new ExternalServiceException("Error connecting with Google Maps");
        }

        return null;
    }

}
