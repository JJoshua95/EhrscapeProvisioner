package ehrscapeProvisioner.ticketDao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MultiPatientProvisionerTicketDao implements MultiPatientProvisionerTicketDaoInterface {
	
	@Override
	public MultiPatientProvisionerTicket getTicketRecord(String ticketId) {
		// TODO Auto-generated method stub
		MultiPatientProvisionerTicket ticketObj = null;
		try {
			String ticketFileStr = readTicketFile(ticketId);
			JsonParser parser = new JsonParser();
			JsonElement jsonObj = parser.parse(ticketFileStr).getAsJsonObject();
			Gson gson = new Gson();
			ticketObj = gson.fromJson(jsonObj, 
					MultiPatientProvisionerTicket.class);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ticketObj;
	}

	@Override
	public MultiPatientProvisionerTicket createTicketRecord(MultiPatientProvisionerTicket ticket) {
		// TODO Auto-generated method stub
		writeTicketObjToFile(ticket);
		return ticket;
	}
	
	@Override
	public MultiPatientProvisionerTicket updateTicketRecord(MultiPatientProvisionerTicket ticket) {
		// TODO Auto-generated method stub
		// get the current object record
		getTicketRecord(ticket.getTicketId());
		createTicketRecord(ticket);
		// rewrite it
		return ticket;
	}
	
	// functions to read and write files
	
	private String readTicketFile(String ticketId) throws FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
		String ticketDirectory = "savedData/tickets";
		File file = new File(classLoader.getResource(ticketDirectory).getFile() + "ticket-" + ticketId+".txt");
		FileReader fr = new FileReader(file);
		BufferedReader bReader = new BufferedReader(fr);
		StringBuilder result = new StringBuilder();
		String line;
		try {
			while ((line = bReader.readLine()) != null) {
				// should only be a single line
				result.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bReader != null)
					bReader.close();
				if (fr != null)
					fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
	
	private void writeTicketObjToFile(MultiPatientProvisionerTicket ticket) {
		//String uniqueId = UUID.randomUUID().toString();
		String newFileName = "ticket-" + ticket.getTicketId() +".txt";
		ClassLoader classLoader = getClass().getClassLoader();
		URL directory = classLoader.getResource("savedData/tickets/");
		System.out.println(directory.getFile());
		String fileDirectoryString = directory.getFile();
		File file = new File(fileDirectoryString + newFileName);
		System.out.println(fileDirectoryString + newFileName);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (file.createNewFile()) {
			    System.out.println("File is created!");
			    System.out.println(file.exists());
			} else {
			    System.out.println("File already exists.");
			    System.out.println(file.exists());
			}
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			System.out.println(ticket.toJsonObject().toString());
			bw.write(ticket.toJsonObject().toString());
			System.out.println("written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/*
	// not needed as the above method will rewrite a file if found
	private void updateJsonFile(String ticketId, String updatedBody) {
		// get the specific file
		// rewrite it
	}
	*/
	
}
