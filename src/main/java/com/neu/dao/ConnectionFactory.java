package com.neu.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectionFactory {
//    private static final String URL = "jdbc:mysql://localhost/SkiResorts?"
//            + "user=root&password=*Mengnanmei28&serverTimezone=UTC";
//
//    private static final String URL2 = "jdbc:mysql://database.c0bkdfz3di78.us-east-1.rds.amazonaws.com/SkiResorts?"
//            + "user=root&password=12345678&serverTimezone=UTC";
//
//    private static final String URL3 = "jdbc:mysql://database.cp9pmeuloqsu.us-east-1.rds.amazonaws.com/SkiResorts?"
//            + "user=root&password=12345678&serverTimezone=UTC";
//
//    private static final String URL_EXP = "jdbc:mysql://database-1.cp9pmeuloqsu.us-east-1.rds.amazonaws.com/SkiResorts?"
//            + "user=root&password=12345678&serverTimezone=UTC";

    private static final String GCP = "jdbc:mysql://google/SkiResorts?cloudSqlInstance=zeta-resource-259322:us-east1:shi-instance-1&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&user=root&password=12345678";


    private static Connection connection = null;
    public static Connection getConnection() {
        if(connection!=null) return connection;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager
                    .getConnection(GCP);

            // Statements allow to issue SQL queries to the database
            Statement statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
