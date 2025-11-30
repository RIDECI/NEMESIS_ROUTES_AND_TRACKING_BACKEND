package edu.dosw.rideci.infrastructure.persistance.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;

public interface RouteRepository extends MongoRepository<RouteDocument, String> {

}
