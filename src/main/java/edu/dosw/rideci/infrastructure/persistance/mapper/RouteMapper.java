package edu.dosw.rideci.infrastructure.persistance.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.persistance.Entity.LocationDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.PickupPointDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    Route toDomain(RouteDocument document);

    RouteDocument toDocument(Route route);

    LocationDocument toLocationDocumentEmbeddable(Location location);

    Location toLocationDomain(LocationDocument location);

    List<PickupPointDocument> toPickUpPointEmbeddableList(List<PickUpPoint> pickUpPoint);

    List<PickUpPoint> toPickUpPointDomainList(List<PickupPointDocument> pickUpPoint);

}
