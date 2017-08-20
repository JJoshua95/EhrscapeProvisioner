package ehrscapeProvisioner.ticketDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

/*
    database: ehrscapeprovisionerdb
   
    Creating table

	CREATE TABLE tickets (
		id INT NOT NULL AUTO_INCREMENT,
		ticketId VARCHAR(400) NOT NULL,
		startTime VARCHAR(400) NOT NULL,
		provisioningStatus VARCHAR(400) NOT NULL,
		provisioningResponseBody MEDIUMTEXT ,
		completionTime VARCHAR(400),
		PRIMARY KEY(ID)
 	);
 	
 */

public class MySqlConnection {
	
	// JDBC driver name 
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	
	// LOCALHOST DATABASE
	
	// Database URL
	static final String LOCAL_DB_URL = "jdbc:mysql://localhost/ehrscapeprovisionerdb";

	// Database credentials
	static final String LOCAL_USER = "root";
	static final String LOCAL_PASS = "NTWCY,Awy8qk";
	
	// AZURE DATABASE
	// Database URL
	static final String AZURE_DB_URL = "jdbc:mysql://ehrscapeprovisionerazuredb.mysql.database.azure.com:3306/ehrscapeprovisionerdb?"
			+ "verifyServerCertificate=true&useSSL=true&requireSSL=false";

	// Database credentials
	static final String AZURE_USER = "myadmin@ehrscapeprovisionerazuredb";
	static final String AZURE_PASS = "Password01";
	
	// for single connection testing purposes
	public static Connection Connector() {
		try {

			Class.forName(JDBC_DRIVER);
			// localhost connection
			// get connection
			//Connection conn = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_USER, LOCAL_PASS);
			
			// Azure connection
            // Set connection properties.
            Properties properties = new Properties();
            properties.setProperty("user", AZURE_USER);
            properties.setProperty("password", AZURE_PASS);

            // get connection
            Connection conn = DriverManager.getConnection(AZURE_DB_URL, properties);
			return conn;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	private static BasicDataSource dataSource;
	
	public static BasicDataSource getDataSource() {
		if (dataSource == null) {
			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(JDBC_DRIVER);
			
			// localhost
			/*
			ds.setUrl(LOCAL_DB_URL);
			ds.setUsername(LOCAL_USER);
			ds.setPassword(LOCAL_PASS);
			*/
			
			//Azure
			ds.setUrl(AZURE_DB_URL);
			ds.setUsername(AZURE_USER);
			ds.setPassword(AZURE_PASS);
			
			ds.setMinIdle(5);
			ds.setMaxIdle(10);
			ds.setMaxOpenPreparedStatements(100);

			dataSource = ds;
		}
		return dataSource;
	}

}
