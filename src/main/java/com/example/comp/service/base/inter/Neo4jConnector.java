package com.example.comp.service.base.inter;

import org.neo4j.driver.Session;

public interface Neo4jConnector {
  Session getSession();
}
