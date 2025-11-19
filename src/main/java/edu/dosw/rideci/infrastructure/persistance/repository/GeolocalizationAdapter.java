package edu.dosw.rideci.infrastructure.persistance.repository;

import edu.dosw.rideci.application.dto.TravelCreatedEvent;
import edu.dosw.rideci.application.port.out.GeolocalizationRepositoryPort;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;
import edu.dosw.rideci.infrastructure.persistance.mapper.RouteMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GeolocalizationAdapter implements GeolocalizationRepositoryPort {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    @Override
    public Route createRoute(TravelCreatedEvent event) {

        Route route = Route.builder()
                .travelId(event.getTravelId())
                .origin(event.getOrigin())
                .destiny(event.getDestiny())
                .departureDateAndTime(event.getDepartureDateAndTime())
                .totalDistance(0.0) // toca calcularla, dos formas de hacerlo
                .build();

        RouteDocument createdRoute = routeMapper.toDocument(route);

        RouteDocument savedRoute = routeRepository.save(createdRoute);

        return routeMapper.toDomain(savedRoute);

    }

}
