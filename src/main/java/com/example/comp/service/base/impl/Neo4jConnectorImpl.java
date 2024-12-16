package com.example.comp.service.base.impl;

import com.example.comp.service.base.inter.Neo4jConnector;
import jakarta.annotation.PostConstruct;
import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class Neo4jConnectorImpl implements Neo4jConnector {

  @Value("${data.neo4j.uri}")
  private String uri;

  @Value("${data.neo4j.username}")
  private String username;

  @Value("${data.neo4j.password}")
  private String password;

  @Value("${data.neo4j.db-name}")
  private String dbName;

  private Driver driver;

  @PostConstruct
  void init(){
    driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
  }

  public Session getSession() {
    return driver.session(SessionConfig.forDatabase(dbName));
  }
}