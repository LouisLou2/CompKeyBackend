package com.example.comp.service.inter;

import org.neo4j.driver.Session;

public interface Neo4jConnector {
  Session getSession();
}
