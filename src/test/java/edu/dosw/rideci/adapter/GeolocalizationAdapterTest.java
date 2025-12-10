package edu.dosw.rideci.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.application.port.in.CalculateRouteWithWayPointsUseCase;
import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.exceptions.RouteNotFoundException;
import edu.dosw.rideci.exceptions.TimeOutException;
import edu.dosw.rideci.infrastructure.persistance.Entity.LocationDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.PickupPointDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.TrackingConfigurationDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.TravelTrackingDocument;
import edu.dosw.rideci.infrastructure.persistance.mapper.RouteMapper;
import edu.dosw.rideci.infrastructure.persistance.repository.GeolocalizationAdapter;
import edu.dosw.rideci.infrastructure.persistance.repository.RouteRepository;

@ExtendWith(MockitoExtension.class)
class GeolocalizationAdapterTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private MapsServicePort mapsServicePort;

    @Mock
    private CalculateRouteWithWayPointsUseCase calculateRouteWithWayPointsUseCase;

    @InjectMocks
    private GeolocalizationAdapter geolocalizationAdapter;

    private Location origin;
    private Location destiny;

    @BeforeEach
    void setUp() {
        origin = sampleLocation(1.1, 2.2, "Origin");
        destiny = sampleLocation(3.3, 4.4, "Destiny");
    }

    @Test
    void createRoute_shouldPersistGoogleDataAndReturnMappedRoute() {
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        CreateRouteCommand command = CreateRouteCommand.builder()
                .travelId("travel-1")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(departure)
                .build();

        Route googleData = Route.builder()
                .totalDistance(1234.5)
                .estimatedTime(1800L)
                .polyline("encoded-polyline")
                .build();

        RouteDocument persistedDocument = RouteDocument.builder().id("route-id").build();
        Route mappedRoute = Route.builder()
                .id("route-id")
                .origin(origin)
                .destiny(destiny)
                .totalDistance(googleData.getTotalDistance())
                .estimatedTime(googleData.getEstimatedTime())
                .polyline(googleData.getPolyline())
                .build();

        when(mapsServicePort.calculateRoute(origin, destiny)).thenReturn(googleData);
        ArgumentCaptor<Route> routeCaptor = ArgumentCaptor.forClass(Route.class);
        when(routeMapper.toDocument(routeCaptor.capture())).thenReturn(persistedDocument);
        when(routeRepository.save(persistedDocument)).thenReturn(persistedDocument);
        when(routeMapper.toDomain(persistedDocument)).thenReturn(mappedRoute);

        Route result = geolocalizationAdapter.createRoute(command);

        Route capturedRoute = routeCaptor.getValue();
        assertThat(capturedRoute.getTravelId()).isEqualTo("travel-1");
        assertThat(capturedRoute.getTotalDistance()).isEqualTo(googleData.getTotalDistance());
        assertThat(capturedRoute.getEstimatedTime()).isEqualTo(googleData.getEstimatedTime());
        assertThat(capturedRoute.getPolyline()).isEqualTo("encoded-polyline");
        assertThat(result.getId()).isEqualTo("route-id");

        verify(routeRepository).save(persistedDocument);
        verify(mapsServicePort).calculateRoute(origin, destiny);
    }

    @Test
    void updateRoute_whenLocationsChange_shouldRecalculateAndSave() {
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        RouteDocument storedDocument = RouteDocument.builder()
                .id("route-1")
                .departureDateAndTime(departure)
                .build();

        Route storedRoute = Route.builder()
                .id("route-1")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(departure)
                .pickUpPoints(new ArrayList<>())
                .build();

        Location newOrigin = sampleLocation(5.5, 6.6, "NewOrigin");
        Location newDestiny = sampleLocation(7.7, 8.8, "NewDestiny");

        Route googleData = Route.builder()
                .totalDistance(555)
                .estimatedTime(3200L)
                .polyline("new-poly")
                .build();

        Route newRoute = Route.builder()
                .id(storedRoute.getId())
                .travelId(storedRoute.getTravelId())
                .origin(newOrigin)
                .destiny(newDestiny)
                .totalDistance(storedRoute.getTotalDistance())
                .estimatedTime(storedRoute.getEstimatedTime())
                .polyline(storedRoute.getPolyline())
                .departureDateAndTime(LocalDateTime.now().plusHours(3))
                .pickUpPoints(storedRoute.getPickUpPoints())
                .locationShare(storedRoute.getLocationShare())
                .travelTracking(storedRoute.getTravelTracking())
                .build();

        when(routeRepository.findById("route-1")).thenReturn(Optional.of(storedDocument));
        when(routeMapper.toDomain(storedDocument)).thenReturn(storedRoute);
        when(mapsServicePort.calculateRoute(newOrigin, newDestiny)).thenReturn(googleData);
        when(routeMapper.toDocument(storedRoute)).thenReturn(storedDocument);

        Route result = geolocalizationAdapter.updateRoute("route-1", newRoute);

        assertThat(result.getOrigin()).isSameAs(newOrigin);
        assertThat(result.getDestiny()).isSameAs(newDestiny);
        assertThat(result.getEstimatedTime()).isEqualTo(3200L);
        assertThat(result.getPolyline()).isEqualTo("new-poly");
        assertThat(result.getTotalDistance()).isEqualTo(555);

        verify(routeRepository).save(storedDocument);
    }

    @Test
    void updateRoute_whenRouteNotFound_shouldThrow() {
        when(routeRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> geolocalizationAdapter.updateRoute("missing", Route.builder().build()))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void updateRoute_whenEditingTooLate_shouldThrowTimeOut() {
        LocalDateTime departure = LocalDateTime.now().plusMinutes(10);
        RouteDocument storedDocument = RouteDocument.builder().id("late").build();
        Route storedRoute = Route.builder()
                .id("late")
                .departureDateAndTime(departure)
                .build();

        when(routeRepository.findById("late")).thenReturn(Optional.of(storedDocument));
        when(routeMapper.toDomain(storedDocument)).thenReturn(storedRoute);

        assertThrows(TimeOutException.class, () -> geolocalizationAdapter.updateRoute("late", storedRoute));
        verify(routeRepository, never()).save(any(RouteDocument.class));
    }

    @Test
    void updateLocation_whenAccuracyTooHigh_shouldReturnLastLocation() {
        LocationDocument lastLocationDocument = sampleLocationDocument(10, 20);
        TravelTrackingDocument tracking = TravelTrackingDocument.builder()
                .lastLocation(lastLocationDocument)
                .locationHistory(new ArrayList<>())
                .remainingDistance(1000)
                .trackingConfiguration(TrackingConfigurationDocument.builder().updateIntervalSeconds(30).build())
                .build();
        RouteDocument routeDocument = RouteDocument.builder()
                .id("route-location")
                .travelTracking(tracking)
                .build();

        Location lastLocation = sampleLocation(10, 20, "Last");
        when(routeRepository.findById("route-location")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toLocationDomain(lastLocationDocument)).thenReturn(lastLocation);

        Location noisyLocation = sampleLocation(11, 21, "New");
        noisyLocation.setAccuracy(80.0);

        Location result = geolocalizationAdapter.updateLocation("route-location", noisyLocation);

        assertThat(result.getLatitude()).isEqualTo(10);
        assertThat(result.getLongitude()).isEqualTo(20);
        verify(routeRepository, never()).save(routeDocument);
    }

    @Test
    void updateLocation_whenAccuracyValid_shouldUpdateTracking() {
        LocationDocument lastLocationDocument = sampleLocationDocument(10, 20);
        TravelTrackingDocument tracking = TravelTrackingDocument.builder()
                .lastLocation(lastLocationDocument)
                .locationHistory(new ArrayList<>())
                .remainingDistance(1000)
                .trackingConfiguration(TrackingConfigurationDocument.builder().updateIntervalSeconds(30).build())
                .build();
        RouteDocument routeDocument = RouteDocument.builder()
                .id("route-track")
                .travelTracking(tracking)
                .build();

        when(routeRepository.findById("route-track")).thenReturn(Optional.of(routeDocument));

        Location newLocation = sampleLocation(12, 22, "Updated");
        newLocation.setAccuracy(10.0);
        LocationDocument newLocationDocument = sampleLocationDocument(12, 22);
        when(routeMapper.toLocationDocumentEmbeddable(any(Location.class))).thenReturn(newLocationDocument);

        Location updated = geolocalizationAdapter.updateLocation("route-track", newLocation);

        assertThat(updated.getLatitude()).isEqualTo(12);
        assertThat(updated.getLongitude()).isEqualTo(22);
        assertThat(routeDocument.getTravelTracking().getLastLocation()).isEqualTo(newLocationDocument);
        assertThat(routeDocument.getTravelTracking().getLocationHistory()).containsExactly(lastLocationDocument);
        verify(routeRepository).save(routeDocument);
    }

    @Test
    void addPickUpPoint_whenValid_shouldAppendPointAndRecalculateRoute() {
        LocalDateTime departure = LocalDateTime.now().plusHours(1);
        RouteDocument routeDocument = RouteDocument.builder().id("route-pick").build();
        List<PickUpPoint> currentPoints = new ArrayList<>();
        Route domainRoute = Route.builder()
                .id("route-pick")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(departure)
                .pickUpPoints(currentPoints)
                .build();

        when(routeRepository.findById("route-pick")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toDomain(routeDocument)).thenReturn(domainRoute);

        Route recalculated = Route.builder()
                .totalDistance(999)
                .estimatedTime(2000L)
                .polyline("poly")
                .build();
        when(calculateRouteWithWayPointsUseCase.calculateRouteWithWayPoints(origin, destiny, currentPoints))
                .thenReturn(recalculated);

        RouteDocument updatedDocument = RouteDocument.builder().id("route-pick").build();
        when(routeMapper.toDocument(domainRoute)).thenReturn(updatedDocument);
        when(routeRepository.save(updatedDocument)).thenReturn(updatedDocument);

        PickUpPoint newPoint = PickUpPoint.builder()
                .passengerId(10L)
                .distanceFromPreviousStop(100)
                .passengerLocation(sampleLocation(13, 23, "Pickup"))
                .estimatedTimeToPick(600L)
                .order(1)
                .build();

        PickUpPoint created = geolocalizationAdapter.addPickUpPoint("route-pick", newPoint);

        assertThat(domainRoute.getPickUpPoints()).hasSize(1);
        assertThat(created.getPassengerId()).isEqualTo(10L);
        assertThat(domainRoute.getTotalDistance()).isEqualTo(999);
        assertThat(domainRoute.getPolyline()).isEqualTo("poly");
        verify(routeRepository).save(updatedDocument);
    }

    @Test
    void addPickUpPoint_whenTooLate_shouldThrowTimeOut() {
        LocalDateTime departure = LocalDateTime.now().plusMinutes(15);
        RouteDocument routeDocument = RouteDocument.builder().id("late-pick").build();
        Route domainRoute = Route.builder()
                .id("late-pick")
                .departureDateAndTime(departure)
                .pickUpPoints(new ArrayList<>())
                .build();

        when(routeRepository.findById("late-pick")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toDomain(routeDocument)).thenReturn(domainRoute);

        assertThrows(TimeOutException.class,
                () -> geolocalizationAdapter.addPickUpPoint("late-pick", PickUpPoint.builder().build()));
        verify(routeRepository, never()).save(any(RouteDocument.class));
        verifyNoInteractions(calculateRouteWithWayPointsUseCase);
    }

    @Test
    void getRouteInformation_shouldReturnMappedRoute() {
        RouteDocument routeDocument = RouteDocument.builder().id("info").build();
        Route mappedRoute = Route.builder().id("info").build();
        when(routeRepository.findById("info")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toDomain(routeDocument)).thenReturn(mappedRoute);

        Route result = geolocalizationAdapter.getRouteInformation("info");
        assertThat(result).isSameAs(mappedRoute);
    }

    @Test
    void getRouteInformation_whenMissing_shouldThrow() {
        when(routeRepository.findById("missing-info")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> geolocalizationAdapter.getRouteInformation("missing-info"))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void getPickUpPoints_shouldReturnMappedList() {
        RouteDocument routeDocument = RouteDocument.builder()
                .id("pick-list")
                .pickUpPoints(Collections.singletonList(PickupPointDocument.builder().build()))
                .build();
        List<PickUpPoint> pickUpPoints = List.of(PickUpPoint.builder().passengerId(1L).build());

        when(routeRepository.findById("pick-list")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toPickUpPointDomainList(routeDocument.getPickUpPoints())).thenReturn(pickUpPoints);

        List<PickUpPoint> result = geolocalizationAdapter.getPickUpPoints("pick-list");
        assertThat(result).isEqualTo(pickUpPoints);
    }

    @Test
    void getRealTimePosition_shouldReturnMappedLocation() {
        LocationDocument lastLocation = sampleLocationDocument(14, 24);
        TravelTrackingDocument tracking = TravelTrackingDocument.builder()
                .lastLocation(lastLocation)
                .locationHistory(new ArrayList<>())
                .trackingConfiguration(TrackingConfigurationDocument.builder().build())
                .build();
        RouteDocument routeDocument = RouteDocument.builder()
                .id("rtp")
                .travelTracking(tracking)
                .build();

        Location mapped = sampleLocation(14, 24, "Actual");

        when(routeRepository.findById("rtp")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toLocationDomain(lastLocation)).thenReturn(mapped);

        Location result = geolocalizationAdapter.getRealTimePosition("rtp");
        assertThat(result).isSameAs(mapped);
    }

    @Test
    void updateIntervalSeconds_shouldMutateTrackingConfiguration() {
        TrackingConfigurationDocument configuration = TrackingConfigurationDocument.builder()
                .updateIntervalSeconds(30)
                .build();
        TravelTrackingDocument tracking = TravelTrackingDocument.builder()
                .trackingConfiguration(configuration)
                .build();
        RouteDocument routeDocument = RouteDocument.builder()
                .id("interval")
                .travelTracking(tracking)
                .build();

        when(routeRepository.findById("interval")).thenReturn(Optional.of(routeDocument));

        geolocalizationAdapter.updateIntervalSeconds("interval", 45);

        assertThat(routeDocument.getTravelTracking().getTrackingConfiguration().getUpdateIntervalSeconds())
                .isEqualTo(45);
    }

    @Test
    void updateIntervalSeconds_whenRouteNotFound_shouldThrow() {
        when(routeRepository.findById("missing-interval")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> geolocalizationAdapter.updateIntervalSeconds("missing-interval", 60))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void updateRoute_whenOnlyDepartureTimeChanges_shouldNotRecalculateRoute() {
        LocalDateTime originalDeparture = LocalDateTime.now().plusHours(2);
        LocalDateTime newDeparture = LocalDateTime.now().plusHours(3);
        
        RouteDocument storedDocument = RouteDocument.builder()
                .id("route-time-only")
                .build();

        Route storedRoute = Route.builder()
                .id("route-time-only")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(originalDeparture)
                .pickUpPoints(new ArrayList<>())
                .totalDistance(1000)
                .estimatedTime(3600L)
                .polyline("original-poly")
                .build();

        Route updatedRoute = Route.builder()
                .id("route-time-only")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(newDeparture)
                .pickUpPoints(new ArrayList<>())
                .build();

        when(routeRepository.findById("route-time-only")).thenReturn(Optional.of(storedDocument));
        when(routeMapper.toDomain(storedDocument)).thenReturn(storedRoute);
        when(routeMapper.toDocument(storedRoute)).thenReturn(storedDocument);

        Route result = geolocalizationAdapter.updateRoute("route-time-only", updatedRoute);

        assertThat(result.getDepartureDateAndTime()).isEqualTo(newDeparture);
        assertThat(result.getTotalDistance()).isEqualTo(1000);
        assertThat(result.getPolyline()).isEqualTo("original-poly");
        verify(mapsServicePort, never()).calculateRoute(any(), any());
        verify(routeRepository).save(storedDocument);
    }

    @Test
    void updateLocation_whenRemainingDistanceLessThan50_shouldExecuteNotificationLogic() {
        LocationDocument lastLocationDocument = sampleLocationDocument(10, 20);
        TravelTrackingDocument tracking = TravelTrackingDocument.builder()
                .lastLocation(lastLocationDocument)
                .locationHistory(new ArrayList<>())
                .remainingDistance(30)
                .trackingConfiguration(TrackingConfigurationDocument.builder().updateIntervalSeconds(30).build())
                .build();
        RouteDocument routeDocument = RouteDocument.builder()
                .id("route-near-dest")
                .travelTracking(tracking)
                .build();

        when(routeRepository.findById("route-near-dest")).thenReturn(Optional.of(routeDocument));

        Location newLocation = sampleLocation(12, 22, "Near");
        newLocation.setAccuracy(10.0);
        LocationDocument newLocationDocument = sampleLocationDocument(12, 22);
        when(routeMapper.toLocationDocumentEmbeddable(any(Location.class))).thenReturn(newLocationDocument);

        Location result = geolocalizationAdapter.updateLocation("route-near-dest", newLocation);

        assertThat(result).isNotNull();
        verify(routeRepository).save(routeDocument);
    }

    @Test
    void updateLocation_whenRouteNotFound_shouldThrow() {
        when(routeRepository.findById("missing-route")).thenReturn(Optional.empty());
        
        Location location = sampleLocation(1, 1, "Test");
        
        assertThatThrownBy(() -> geolocalizationAdapter.updateLocation("missing-route", location))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void addPickUpPoint_whenRouteNotFound_shouldThrow() {
        when(routeRepository.findById("missing-pickup")).thenReturn(Optional.empty());
        
        PickUpPoint point = PickUpPoint.builder().passengerId(1L).build();
        
        assertThatThrownBy(() -> geolocalizationAdapter.addPickUpPoint("missing-pickup", point))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void getPickUpPoints_whenRouteNotFound_shouldThrow() {
        when(routeRepository.findById("missing-pickup-list")).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> geolocalizationAdapter.getPickUpPoints("missing-pickup-list"))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void getRealTimePosition_whenRouteNotFound_shouldThrow() {
        when(routeRepository.findById("missing-position")).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> geolocalizationAdapter.getRealTimePosition("missing-position"))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void updatePickUpPoint_shouldThrowTimeOutWhenTooLate() {
        LocalDateTime departure = LocalDateTime.now().plusMinutes(20);
        RouteDocument routeDocument = RouteDocument.builder().id("update-point").build();
        Route domainRoute = Route.builder()
                .id("update-point")
                .departureDateAndTime(departure)
                .pickUpPoints(new ArrayList<>())
                .build();

        when(routeRepository.findById("update-point")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toDomain(routeDocument)).thenReturn(domainRoute);

        PickUpPoint updatedPoint = PickUpPoint.builder().passengerId(5L).build();

        assertThrows(TimeOutException.class,
                () -> geolocalizationAdapter.updatePickUpPoint("update-point", updatedPoint));
    }

    @Test
    void updatePickUpPoint_whenRouteNotFound_shouldThrow() {
        when(routeRepository.findById("missing-update-point")).thenReturn(Optional.empty());
        
        PickUpPoint point = PickUpPoint.builder().passengerId(1L).build();
        
        assertThatThrownBy(() -> geolocalizationAdapter.updatePickUpPoint("missing-update-point", point))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void updatePickUpPoint_whenValid_shouldRecalculateAndReturnNull() {
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        RouteDocument routeDocument = RouteDocument.builder().id("update-valid").build();
        List<PickUpPoint> currentPoints = new ArrayList<>();
        Route domainRoute = Route.builder()
                .id("update-valid")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(departure)
                .pickUpPoints(currentPoints)
                .build();

        when(routeRepository.findById("update-valid")).thenReturn(Optional.of(routeDocument));
        when(routeMapper.toDomain(routeDocument)).thenReturn(domainRoute);

        Route recalculated = Route.builder()
                .totalDistance(888)
                .estimatedTime(1500L)
                .polyline("poly-updated")
                .build();
        when(calculateRouteWithWayPointsUseCase.calculateRouteWithWayPoints(origin, destiny, currentPoints))
                .thenReturn(recalculated);

        PickUpPoint updatedPoint = PickUpPoint.builder().passengerId(7L).build();

        PickUpPoint result = geolocalizationAdapter.updatePickUpPoint("update-valid", updatedPoint);

        assertThat(result).isNull();
        verify(calculateRouteWithWayPointsUseCase).calculateRouteWithWayPoints(origin, destiny, currentPoints);
    }

    @Test
    void createRoute_shouldHandleNullPickUpPoints() {
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        CreateRouteCommand command = CreateRouteCommand.builder()
                .travelId("travel-null-points")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(departure)
                .build();

        Route googleData = Route.builder()
                .totalDistance(2000.0)
                .estimatedTime(2400L)
                .polyline("poly-null-test")
                .build();

        RouteDocument persistedDocument = RouteDocument.builder().id("route-null-points").build();
        Route mappedRoute = Route.builder()
                .id("route-null-points")
                .pickUpPoints(null)
                .build();

        when(mapsServicePort.calculateRoute(origin, destiny)).thenReturn(googleData);
        when(routeMapper.toDocument(any(Route.class))).thenReturn(persistedDocument);
        when(routeRepository.save(persistedDocument)).thenReturn(persistedDocument);
        when(routeMapper.toDomain(persistedDocument)).thenReturn(mappedRoute);

        Route result = geolocalizationAdapter.createRoute(command);

        assertThat(result.getPickUpPoints()).isNull();
    }

    @Test
    void updateRoute_whenOnlyPickUpPointsChange_shouldNotRecalculateRoute() {
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        RouteDocument storedDocument = RouteDocument.builder()
                .id("route-points-change")
                .build();

        List<PickUpPoint> originalPoints = new ArrayList<>();
        List<PickUpPoint> newPoints = new ArrayList<>();
        newPoints.add(PickUpPoint.builder().passengerId(1L).build());

        Route storedRoute = Route.builder()
                .id("route-points-change")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(departure)
                .pickUpPoints(originalPoints)
                .totalDistance(1500)
                .estimatedTime(4000L)
                .polyline("unchanged-poly")
                .build();

        Route updatedRoute = Route.builder()
                .id("route-points-change")
                .origin(origin)
                .destiny(destiny)
                .departureDateAndTime(departure)
                .pickUpPoints(newPoints)
                .build();

        when(routeRepository.findById("route-points-change")).thenReturn(Optional.of(storedDocument));
        when(routeMapper.toDomain(storedDocument)).thenReturn(storedRoute);
        when(routeMapper.toDocument(storedRoute)).thenReturn(storedDocument);

        Route result = geolocalizationAdapter.updateRoute("route-points-change", updatedRoute);

        assertThat(result.getPickUpPoints()).hasSize(1);
        assertThat(result.getTotalDistance()).isEqualTo(1500);
        assertThat(result.getPolyline()).isEqualTo("unchanged-poly");
        verify(mapsServicePort, never()).calculateRoute(any(), any());
    }

    private static Location sampleLocation(double lat, double lon, String direction) {
        return Location.builder()
                .latitude(lat)
                .longitude(lon)
                .direction(direction)
                .accuracy(5.0)
                .speed(30.0)
                .build();
    }

    private static LocationDocument sampleLocationDocument(double lat, double lon) {
        return LocationDocument.builder()
                .latitude(lat)
                .longitude(lon)
                .build();
    }
}
