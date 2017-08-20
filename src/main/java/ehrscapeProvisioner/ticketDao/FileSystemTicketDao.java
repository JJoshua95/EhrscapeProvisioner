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


// Temporary solution for storing tickets due to time concerns
// TODO change where the files are uploaded must be outside the webapp folders
// TODO Move this into a database

public class FileSystemTicketDao implements MultiPatientProvisionerTicketDao {
	
	@Override
	public MultiPatientProvisionerTicket getTicketRecord(String ticketId) {
		MultiPatientProvisionerTicket ticketObj = null;
		try {
			String ticketFileStr = readTicketFile(ticketId);
			JsonParser parser = new JsonParser();
			JsonElement jsonObj = parser.parse(ticketFileStr).getAsJsonObject();
			Gson gson = new Gson();
			ticketObj = gson.fromJson(jsonObj, 
					MultiPatientProvisionerTicket.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ticketObj;
	}

	@Override
	public void createTicketRecord(MultiPatientProvisionerTicket ticket) {
		writeTicketObjToFile(ticket);
	}
	
	// not needed as create record overwrites the record if necessary if using files to store data
	@Override
	public void updateTicketRecord(MultiPatientProvisionerTicket ticket) {
		// get the current object record
		getTicketRecord(ticket.getTicketId());
		createTicketRecord(ticket);
		// rewrite it
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
		
		String newFileName = "ticket-" + ticket.getTicketId() +".txt";
		ClassLoader classLoader = getClass().getClassLoader();
		URL directory = classLoader.getResource("/savedData/tickets/");
		System.out.println(directory.getFile());
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
	
}
