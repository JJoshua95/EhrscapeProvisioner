package ehrscapeProvisioner.ticketDao;

public interface MultiPatientProvisionerTicketDao {
	
	public MultiPatientProvisionerTicket getTicketRecord(String ticketId);
	public void createTicketRecord(MultiPatientProvisionerTicket ticket);
	public void updateTicketRecord(MultiPatientProvisionerTicket ticket);

}
