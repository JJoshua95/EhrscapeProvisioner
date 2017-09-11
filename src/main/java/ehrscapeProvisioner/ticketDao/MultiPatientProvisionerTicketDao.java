package ehrscapeProvisioner.ticketDao;

/**
 * This interface outlines the operations needed for manipulating ticket resources in this application
 */
public interface MultiPatientProvisionerTicketDao {
	
	/**
	 * This method is used to get a ticket from the data store given its ticketID which should uniquely identify it
	 * @param ticketId
	 * @return MultiPatientProvisionerTicket 
	 */
	public MultiPatientProvisionerTicket getTicketRecord(String ticketId);
	
	/**
	 * This method stores a new ticket instance in the data store 
	 * @param ticket MultiPatientProvisionerTicket 
	 */
	public void createTicketRecord(MultiPatientProvisionerTicket ticket);
	
	/**
	 * This method overwrites a ticket record in the data store given the new version
	 * @param ticket MultiPatientProvisionerTicket
	 */
	public void updateTicketRecord(MultiPatientProvisionerTicket ticket);

}
