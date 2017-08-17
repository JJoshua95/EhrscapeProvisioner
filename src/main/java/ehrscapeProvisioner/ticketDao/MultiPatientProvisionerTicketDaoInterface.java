package ehrscapeProvisioner.ticketDao;

public interface MultiPatientProvisionerTicketDaoInterface {
	
	public MultiPatientProvisionerTicket getTicketRecord(String ticketId);
	public MultiPatientProvisionerTicket createTicketRecord(MultiPatientProvisionerTicket ticket);

}
