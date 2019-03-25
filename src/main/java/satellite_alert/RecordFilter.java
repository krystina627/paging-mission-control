package satellite_alert;

/**
*
* @author Krystina Poling
*/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;

// Receives the satellite data file that needs to be processed
// and parses all the data into related classes based on component (battery or thermostat)
public class RecordFilter {

	private ArrayList<BatteryRecord> batteryRecords = new ArrayList<BatteryRecord>();
	private ArrayList<ThermostatRecord> thermostatRecords = new ArrayList<ThermostatRecord>();
	private static List<SatelliteRecord> printList = new ArrayList<SatelliteRecord>();

	public RecordFilter(String fileName) throws IOException {

		batteryRecords = new ArrayList<BatteryRecord>();
		thermostatRecords = new ArrayList<ThermostatRecord>();

		FileInputStream fis = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;

		try {
			while (!(line = br.readLine()).equals("")) {
				List<String> list = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(line, "|");
				while (st.hasMoreTokens()) {
					list.add(st.nextToken());
				}
				LocalDateTime dateTime = convertStringToLocalDateTime(list.get(0));
				int satelliteId = Integer.parseInt(list.get(1));
				int redHighLimit = Integer.parseInt(list.get(2));
				int yellowHighLimit = Integer.parseInt(list.get(3));
				int yellowLowLimit = Integer.parseInt(list.get(4));
				int redLowLimit = Integer.parseInt(list.get(5));
				float rawValues = Float.parseFloat(list.get(6));
				String component = list.get(7);

				if (component.equals("BATT")) {
					BatteryRecord batteryRecord = new BatteryRecord(dateTime, satelliteId, redHighLimit,
							yellowHighLimit, yellowLowLimit, redLowLimit, rawValues, component);
					batteryRecords.add(batteryRecord);
				}
				if (component.equals("TSTAT")) {
					ThermostatRecord thermostatRecord = new ThermostatRecord(dateTime, satelliteId, redHighLimit,
							yellowHighLimit, yellowLowLimit, redLowLimit, rawValues, component);
					thermostatRecords.add(thermostatRecord);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
	}

	// Removes waste: all records that do not meet violation criteria are removed
	// from storage
	public void filterDataForViolations(ArrayList<? extends SatelliteRecord> satelliteRecords) {

		satelliteRecords.removeIf(n -> (n.getComponent().equals("TSTAT") && (n.getRawValues() < n.getRedHighLimit())));
		satelliteRecords.removeIf(n -> (n.getComponent().equals("BATT") && (n.getRawValues() > n.getYellowLowLimit())));

		// groups filtered data by satellite Id and then sorts by date and time
		groupBySatIdAndFilterByDateTime(getBatteryRecords());
		groupBySatIdAndFilterByDateTime(getThermostatRecords());
	}

	// groups filtered data by satellite Id and then sorts by date and time
	private void groupBySatIdAndFilterByDateTime(ArrayList<? extends SatelliteRecord> satelliteRecords) {
		Collections.sort(satelliteRecords,
				Comparator.comparing(SatelliteRecord::getSatelliteId).thenComparing(SatelliteRecord::getDateTime));
	}

	// Filters remaining violation list to final alert print list
	public void createPrintListForAlerts(ArrayList<? extends SatelliteRecord> satelliteRecords) {
		printList = getPrintList();
		int j = 0;
		for (int i = 0; i < satelliteRecords.size(); i++) {
			if (j == satelliteRecords.size()) {
				break;
			}
			for (j = 2; j < satelliteRecords.size(); j++) {
				if (satelliteRecords.get(i).getSatelliteId() == satelliteRecords.get(j).getSatelliteId()) {
					if (alertDurationCheck(satelliteRecords.get(j).getDateTime(),
							satelliteRecords.get(i).getDateTime()) <= 5) {
								satelliteRecords.get(i).getDateTime();
						printList.add(satelliteRecords.get(i));

					}
				}
			}
		}
	}

	// Convert alert print list to JSON and print to console
	public void printListToJSON() {
		printList = getPrintList();
		printList.sort(Comparator.comparing(n -> n.getDateTime()));
		JSONArray satelliteAlerts = new JSONArray();

		for (int i = 0; i < printList.size(); i++) {
			satelliteAlerts.put(printList.get(i).toJSONString());
		}
		System.out.println(satelliteAlerts.toString(4)); // Print formatted JSON to console
	}

	// convert input string timestamp to LocalDateTime
	private static LocalDateTime convertStringToLocalDateTime(String stringTimeStamp) {
		String pattern = "yyyyMMdd HH:mm:ss.SSS";
		String str = stringTimeStamp;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

		return dateTime;
	}

	// check for three violations within a five minute interval
	private static long alertDurationCheck(LocalDateTime thirdAlert, LocalDateTime firstAlert) {
		long minutes = firstAlert.until(thirdAlert, ChronoUnit.MINUTES);
		thirdAlert = thirdAlert.plusMinutes(minutes);
		return minutes;
	}

	public ArrayList<BatteryRecord> getBatteryRecords() {
		return batteryRecords;
	}

	public ArrayList<ThermostatRecord> getThermostatRecords() {
		return thermostatRecords;
	}

	public static List<SatelliteRecord> getPrintList() {
		return printList;
	}

}
