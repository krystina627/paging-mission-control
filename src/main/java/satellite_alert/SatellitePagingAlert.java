package satellite_alert;

/**
*
* @author Krystina Poling
*/

import java.io.IOException;


// Main class that specifies the satellite text file to be processed
// 
public class SatellitePagingAlert {

	public static void main(String[] args) throws IOException {
		
		// set satellite data file 
		String fileName = "input.txt";
		
		RecordFilter recordFilter = new RecordFilter(fileName);
		recordFilter.filterDataForViolations(recordFilter.getBatteryRecords());
		recordFilter.filterDataForViolations(recordFilter.getThermostatRecords());
		recordFilter.createPrintListForAlerts(recordFilter.getBatteryRecords());
		recordFilter.createPrintListForAlerts(recordFilter.getThermostatRecords());
		recordFilter.printListToJSON();
	}
}
