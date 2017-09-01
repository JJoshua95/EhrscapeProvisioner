package ehrscapeProvisioner.ticketDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MySqlTicketDao implements MultiPatientProvisionerTicketDao {

	private Connection conn = MySqlConnection.Connector();
	private BasicDataSource ds = MySqlConnection.getDataSource();
	private JsonParser parser = new JsonParser();

	public boolean isDbConnected() {
		try {
			return !conn.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public MultiPatientProvisionerTicket getTicketRecord(String ticketId) {
		MultiPatientProvisionerTicket ticket = null;
		try {
			ticket = getTicketFromDb(ticketId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ticket;
	}

	@Override
	// only for creating new ticket records with no response or completion times
	public void createTicketRecord(MultiPatientProvisionerTicket ticket) {
		try {
			insertTicketToDb(ticket.getTicketId(), ticket.getStartTime(), ticket.getProvisioningStatus(), null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateTicketRecord(MultiPatientProvisionerTicket ticket) {
		try {
			updateTicketInDb(ticket.getTicketId(), ticket.getProvisioningStatus(), ticket.getResponseBody().toString(),
					ticket.getCompletionTime());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getAllTicketRecords() throws SQLException {
		Statement statement = null;
		Connection conn = null;
		ResultSet results = null;
		try {
			conn = ds.getConnection();
			statement = conn.createStatement();
			results = statement.executeQuery("SELECT * from tickets;");
			while (results.next()) {
				String outputString = String.format("Data row = (%s, %s %s, %s, %s)", results.getString(2),
						results.getString(3), results.getString(4), results.getString(5), results.getString(6));
				System.out.println(outputString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// close everything
			if (conn != null) {
				conn.close();
			}
			if (results != null) {
				results.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
	}

	public MultiPatientProvisionerTicket getTicketFromDb(String ticketId) throws SQLException {
		MultiPatientProvisionerTicket ticket = null;
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		ResultSet result = null;
		try {
			conn = ds.getConnection();
			String query = "SELECT * FROM tickets WHERE ticketId = ?;";
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, ticketId);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				//System.out.println(ticketId);
				String startTime = result.getString("startTime");
				// System.out.println(startTime);
				String provisioningStatus = result.getString("provisioningStatus");
				// System.out.println(provisioningStatus);
				String provisioningResponseBody = result.getString("provisioningResponseBody");
				// System.out.println(provisioningResponseBody);
				JsonElement responseElement;
				if (provisioningResponseBody != null) {
					responseElement = parser.parse(provisioningResponseBody);
					// System.out.println(responseElement.toString());
				} else {
					responseElement = null;
				}
				String completionTime = result.getString("completionTime");
				// System.out.println(completionTime);
				ticket = new MultiPatientProvisionerTicket(ticketId, provisioningStatus, startTime, responseElement,
						completionTime);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// close everything
			if (conn != null) {
				conn.close();
			}
			if (result != null) {
				result.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
		return ticket;
	}

	public void insertTicketToDb(String ticketId, String startTime, String provisioningStatus,
			String provisioningResponseBody, String completionTime) throws SQLException {
		String query;
		PreparedStatement preparedStatement = null;
		Connection conn = null;

		query = "INSERT INTO tickets "
				+ "(ticketId, startTime, provisioningStatus, provisioningResponseBody, completionTime) "
				+ "VALUES (?, ?, ?, ?, ?);";

		try {
			conn = ds.getConnection();
			preparedStatement = conn.prepareStatement(query);

			preparedStatement.setString(1, ticketId);
			preparedStatement.setString(2, startTime);
			preparedStatement.setString(3, provisioningStatus);
			preparedStatement.setString(4, provisioningResponseBody);
			preparedStatement.setString(5, completionTime);

			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// close
			if (conn != null) {
				conn.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	public void updateTicketInDb(String ticketId, String provisioningStatus, String provisioningResponseBody,
			String completionTime) throws SQLException {
		PreparedStatement preparedStatement = null;
		String query = "UPDATE tickets "
				+ "SET provisioningStatus = ?, provisioningResponseBody = ?, completionTime = ?"
				+ "WHERE ticketId = ?;";
		Connection conn = null;

		try {
			conn = ds.getConnection();
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, provisioningStatus);
			preparedStatement.setString(2, provisioningResponseBody);
			preparedStatement.setString(3, completionTime);
			preparedStatement.setString(4, ticketId);

			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// close
			if (conn != null) {
				conn.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

}
