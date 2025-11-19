package edu.dosw.rideci.infrastructure.persistance.mapper;

import org.mapstruct.Mapper;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.persistance.Entity.LocationDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    Route toDomain(RouteDocument document);

    RouteDocument toDocument(Route route);

    LocationDocument toLocationDocumentEmbeddable(Location location);

    Location toLocationEmbeddable(LocationDocument location);

}
