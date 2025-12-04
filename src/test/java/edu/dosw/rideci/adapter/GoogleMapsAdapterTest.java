package edu.dosw.rideci.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.exceptions.ExternalServiceException;
import edu.dosw.rideci.infrastructure.config.GoogleMapsConfig;
import edu.dosw.rideci.infrastructure.persistance.repository.GoogleMapsAdapter;

@ExtendWith(MockitoExtension.class)
class GoogleMapsAdapterTest {
    @Mock
    private GoogleMapsConfig googleMapsConfig;
    @Mock
    private GeoApiContext geoApiContext;
    private GoogleMapsAdapter googleMapsAdapter;
    private Location origin;
    private Location destination;
    @BeforeEach
    void setUp() {
        googleMapsAdapter = new GoogleMapsAdapter(googleMapsConfig, geoApiContext);
        origin = Location.builder()
                .latitude(4.6097)
                .longitude(-74.0817)
                .direction("Bogotá, Colombia")
                .build();
        destination = Location.builder()
                .latitude(6.2442)
                .longitude(-75.5812)
                .direction("Medellín, Colombia")
                .build();
    }
    @Test
    void calculateRoute_shouldReturnRouteWithValidData_whenApiReturnsSuccessfully() throws Exception {
        DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
        DirectionsResult mockResult = new DirectionsResult();
        DirectionsRoute mockRoute = new DirectionsRoute();
        DirectionsLeg mockLeg = new DirectionsLeg();
        Distance mockDistance = new Distance();
        mockDistance.inMeters = 415000;
        Duration mockDuration = new Duration();
        mockDuration.inSeconds = 22500L;
        mockLeg.distance = mockDistance;
        mockLeg.duration = mockDuration;
        EncodedPolyline mockPolyline = mock(EncodedPolyline.class);
        when(mockPolyline.getEncodedPath()).thenReturn("encodedPathString");
        mockRoute.overviewPolyline = mockPolyline;
        mockRoute.legs = new DirectionsLeg[]{mockLeg};
        mockResult.routes = new DirectionsRoute[]{mockRoute};
        try (MockedStatic<DirectionsApi> mockedStatic = mockStatic(DirectionsApi.class)) {
            mockedStatic.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);
            when(mockRequest.origin(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.destination(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.await()).thenReturn(mockResult);
            Route result = googleMapsAdapter.calculateRoute(origin, destination);
            assertThat(result).isNotNull();
            assertThat(result.getTotalDistance()).isEqualTo(415000);
            assertThat(result.getEstimatedTime()).isEqualTo(22500L);
            assertThat(result.getPolyline()).isNotNull();
        }
    }
    @Test
    void calculateRoute_shouldReturnNull_whenNoRoutesFound() throws Exception {
        DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
        DirectionsResult mockResult = new DirectionsResult();
        mockResult.routes = new DirectionsRoute[0];
        try (MockedStatic<DirectionsApi> mockedStatic = mockStatic(DirectionsApi.class)) {
            mockedStatic.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);
            when(mockRequest.origin(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.destination(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.await()).thenReturn(mockResult);
            Route result = googleMapsAdapter.calculateRoute(origin, destination);
            assertThat(result).isNull();
        }
    }
    @Test
    void calculateRoute_shouldThrowExternalServiceException_whenApiThrowsException() throws Exception {
        DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
        try (MockedStatic<DirectionsApi> mockedStatic = mockStatic(DirectionsApi.class)) {
            mockedStatic.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);
            when(mockRequest.origin(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.destination(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.await()).thenThrow(new RuntimeException("API Error"));
            assertThatThrownBy(() -> googleMapsAdapter.calculateRoute(origin, destination))
                    .isInstanceOf(ExternalServiceException.class)
                    .hasMessageContaining("Error connecting with Google Maps");
        }
    }
    @Test
    void calculateRoute_shouldThrowExternalServiceException_whenGeoApiContextIsNull() {
        GoogleMapsAdapter adapterWithNullContext = new GoogleMapsAdapter(googleMapsConfig, null);
        assertThatThrownBy(() -> adapterWithNullContext.calculateRoute(origin, destination))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Error connecting with Google Maps");
    }
    @Test
    void calculateRoute_shouldHandleNullOriginDirection() {
        Location originWithNullDirection = Location.builder()
                .latitude(4.6097)
                .longitude(-74.0817)
                .direction(null)
                .build();
        assertThatThrownBy(() -> googleMapsAdapter.calculateRoute(originWithNullDirection, destination))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Error connecting with Google Maps");
    }
    @Test
    void calculateRoute_shouldThrowIllegalArgumentException_whenDestinationDirectionIsNull() {
        Location destinationWithNullDirection = Location.builder()
                .latitude(6.2442)
                .longitude(-75.5812)
                .direction(null)
                .build();
        assertThatThrownBy(() -> googleMapsAdapter.calculateRoute(origin, destinationWithNullDirection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Direction cannot be null");
    }
    @Test
    void calculateRoute_shouldHandleBothNullDirections() {
        Location originWithNullDirection = Location.builder()
                .latitude(4.6097)
                .longitude(-74.0817)
                .direction(null)
                .build();
        Location destinationWithNullDirection = Location.builder()
                .latitude(6.2442)
                .longitude(-75.5812)
                .direction(null)
                .build();
        assertThatThrownBy(() -> googleMapsAdapter.calculateRoute(originWithNullDirection, destinationWithNullDirection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Direction cannot be null");
    }
    @Test
    void constructor_shouldCreateGoogleMapsAdapter() {
        GoogleMapsAdapter adapter = new GoogleMapsAdapter(googleMapsConfig, geoApiContext);
        assertThat(adapter).isNotNull();
    }
    @Test
    void calculateRouteWithWayPoints_shouldReturnRouteWithWaypoints_whenApiReturnsSuccessfully() throws Exception {
        List<PickUpPoint> pickUpPoints = new ArrayList<>();
        Location waypoint1 = Location.builder()
                .latitude(5.0)
                .longitude(-74.5)
                .direction("Waypoint 1")
                .build();
        Location waypoint2 = Location.builder()
                .latitude(5.5)
                .longitude(-74.8)
                .direction("Waypoint 2")
                .build();
        PickUpPoint point1 = PickUpPoint.builder()
                .passengerId(1L)
                .passengerLocation(waypoint1)
                .order(1)
                .build();
        PickUpPoint point2 = PickUpPoint.builder()
                .passengerId(2L)
                .passengerLocation(waypoint2)
                .order(2)
                .build();
        pickUpPoints.add(point1);
        pickUpPoints.add(point2);
        DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
        DirectionsResult mockResult = new DirectionsResult();
        DirectionsRoute mockRoute = new DirectionsRoute();
        DirectionsLeg mockLeg1 = new DirectionsLeg();
        Distance mockDistance1 = new Distance();
        mockDistance1.inMeters = 50000;
        Duration mockDuration1 = new Duration();
        mockDuration1.inSeconds = 3600L;
        mockLeg1.distance = mockDistance1;
        mockLeg1.duration = mockDuration1;
        DirectionsLeg mockLeg2 = new DirectionsLeg();
        Distance mockDistance2 = new Distance();
        mockDistance2.inMeters = 60000;
        Duration mockDuration2 = new Duration();
        mockDuration2.inSeconds = 4200L;
        mockLeg2.distance = mockDistance2;
        mockLeg2.duration = mockDuration2;
        DirectionsLeg mockLeg3 = new DirectionsLeg();
        Distance mockDistance3 = new Distance();
        mockDistance3.inMeters = 70000;
        Duration mockDuration3 = new Duration();
        mockDuration3.inSeconds = 5000L;
        mockLeg3.distance = mockDistance3;
        mockLeg3.duration = mockDuration3;
        mockRoute.legs = new DirectionsLeg[]{mockLeg1, mockLeg2, mockLeg3};
        mockRoute.waypointOrder = new int[]{0, 1};
        EncodedPolyline mockPolyline = mock(EncodedPolyline.class);
        when(mockPolyline.getEncodedPath()).thenReturn("encodedPathString");
        mockRoute.overviewPolyline = mockPolyline;
        mockResult.routes = new DirectionsRoute[]{mockRoute};
        when(googleMapsConfig.geoApiContext()).thenReturn(geoApiContext);
        try (MockedStatic<DirectionsApi> mockedStatic = mockStatic(DirectionsApi.class)) {
            mockedStatic.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);
            when(mockRequest.origin(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.destination(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.waypoints(any(String[].class))).thenReturn(mockRequest);
            when(mockRequest.optimizeWaypoints(true)).thenReturn(mockRequest);
            when(mockRequest.trafficModel(any())).thenReturn(mockRequest);
            when(mockRequest.await()).thenReturn(mockResult);
            Route result = googleMapsAdapter.calculateRouteWithWayPoints(origin, destination, pickUpPoints);
            assertThat(result).isNotNull();
            assertThat(result.getTotalDistance()).isEqualTo(180000L);
            assertThat(result.getEstimatedTime()).isEqualTo(12800L);
            assertThat(result.getPolyline()).isNotNull();
            assertThat(point1.getDistanceFromPreviousStop()).isEqualTo(50000);
            assertThat(point1.getEstimatedTimeToPick()).isEqualTo(3600L);
            assertThat(point2.getDistanceFromPreviousStop()).isEqualTo(60000);
            assertThat(point2.getEstimatedTimeToPick()).isEqualTo(4200L);
        }
    }
    @Test
    void calculateRouteWithWayPoints_shouldThrowException_whenWaypointLocationIsNull() {
        List<PickUpPoint> pickUpPoints = new ArrayList<>();
        PickUpPoint pointWithNullLocation = PickUpPoint.builder()
                .passengerId(1L)
                .passengerLocation(null)
                .order(1)
                .build();
        pickUpPoints.add(pointWithNullLocation);
        assertThatThrownBy(() -> googleMapsAdapter.calculateRouteWithWayPoints(origin, destination, pickUpPoints))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Error connecting with Google Maps");
    }
    @Test
    void calculateRouteWithWayPoints_shouldHandleEmptyPickUpPointsList() throws Exception {
        List<PickUpPoint> emptyPickUpPoints = new ArrayList<>();
        DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
        DirectionsResult mockResult = new DirectionsResult();
        DirectionsRoute mockRoute = new DirectionsRoute();
        DirectionsLeg mockLeg = new DirectionsLeg();
        Distance mockDistance = new Distance();
        mockDistance.inMeters = 100000;
        Duration mockDuration = new Duration();
        mockDuration.inSeconds = 7200L;
        mockLeg.distance = mockDistance;
        mockLeg.duration = mockDuration;
        mockRoute.legs = new DirectionsLeg[]{mockLeg};
        mockRoute.waypointOrder = new int[]{};
        EncodedPolyline mockPolyline = new EncodedPolyline();
        mockRoute.overviewPolyline = mockPolyline;
        mockResult.routes = new DirectionsRoute[]{mockRoute};
        when(googleMapsConfig.geoApiContext()).thenReturn(geoApiContext);
        try (MockedStatic<DirectionsApi> mockedStatic = mockStatic(DirectionsApi.class)) {
            mockedStatic.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);
            when(mockRequest.origin(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.destination(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.waypoints(any(String[].class))).thenReturn(mockRequest);
            when(mockRequest.optimizeWaypoints(true)).thenReturn(mockRequest);
            when(mockRequest.trafficModel(any())).thenReturn(mockRequest);
            when(mockRequest.await()).thenReturn(mockResult);
            Route result = googleMapsAdapter.calculateRouteWithWayPoints(origin, destination, emptyPickUpPoints);
            assertThat(result).isNotNull();
            assertThat(result.getTotalDistance()).isEqualTo(100000L);
            assertThat(result.getEstimatedTime()).isEqualTo(7200L);
        }
    }
    @Test
    void calculateRouteWithWayPoints_shouldReturnNull_whenNoRoutesFound() throws Exception {
        List<PickUpPoint> pickUpPoints = new ArrayList<>();
        Location waypoint1 = Location.builder()
                .latitude(5.0)
                .longitude(-74.5)
                .direction("Waypoint 1")
                .build();
        PickUpPoint point1 = PickUpPoint.builder()
                .passengerId(1L)
                .passengerLocation(waypoint1)
                .order(1)
                .build();
        pickUpPoints.add(point1);
        DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
        DirectionsResult mockResult = new DirectionsResult();
        mockResult.routes = new DirectionsRoute[0];
        when(googleMapsConfig.geoApiContext()).thenReturn(geoApiContext);
        try (MockedStatic<DirectionsApi> mockedStatic = mockStatic(DirectionsApi.class)) {
            mockedStatic.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);
            when(mockRequest.origin(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.destination(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.waypoints(any(String[].class))).thenReturn(mockRequest);
            when(mockRequest.optimizeWaypoints(true)).thenReturn(mockRequest);
            when(mockRequest.trafficModel(any())).thenReturn(mockRequest);
            when(mockRequest.await()).thenReturn(mockResult);
            Route result = googleMapsAdapter.calculateRouteWithWayPoints(origin, destination, pickUpPoints);
            assertThat(result).isNull();
        }
    }
    @Test
    void calculateRouteWithWayPoints_shouldThrowException_whenApiThrowsException() throws Exception {
        List<PickUpPoint> pickUpPoints = new ArrayList<>();
        Location waypoint1 = Location.builder()
                .latitude(5.0)
                .longitude(-74.5)
                .direction("Waypoint 1")
                .build();
        PickUpPoint point1 = PickUpPoint.builder()
                .passengerId(1L)
                .passengerLocation(waypoint1)
                .order(1)
                .build();
        pickUpPoints.add(point1);
        DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
        when(googleMapsConfig.geoApiContext()).thenReturn(geoApiContext);
        try (MockedStatic<DirectionsApi> mockedStatic = mockStatic(DirectionsApi.class)) {
            mockedStatic.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);
            when(mockRequest.origin(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.destination(any(String.class))).thenReturn(mockRequest);
            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.waypoints(any(String[].class))).thenReturn(mockRequest);
            when(mockRequest.optimizeWaypoints(true)).thenReturn(mockRequest);
            when(mockRequest.trafficModel(any())).thenReturn(mockRequest);
            when(mockRequest.await()).thenThrow(new RuntimeException("API Error"));
            assertThatThrownBy(() -> googleMapsAdapter.calculateRouteWithWayPoints(origin, destination, pickUpPoints))
                    .isInstanceOf(ExternalServiceException.class)
                    .hasMessageContaining("Error connecting with Google Maps");
        }
    }
    @Test
    void recalculateETA_shouldReturnNull() {
        Long result = googleMapsAdapter.recalculateETA(1000L);
        assertThat(result).isNull();
    }
}
