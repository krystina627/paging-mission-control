package satellite_alert;

import java.time.Instant;

/**
*
* @author Krystina Poling
*/

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.json.JSONObject;

// Generic class to represent any satellite component record
// SatelliteRecord class has two child classes ThermostatRecord and BatteryReord
public abstract class SatelliteRecord {

	protected LocalDateTime dateTime;
	protected int satelliteId;
	protected int redHighLimit;
	protected int yellowHighLimit;
	protected int yellowLowLimit;
	protected int redLowLimit;
	protected float rawValues;
	protected String component;

	// default constructor ((non-argumented)
	public SatelliteRecord() {

	}

	// argumented constructor
	public SatelliteRecord(LocalDateTime dateTime, int satelliteId, int redHighLimit, int yellowHighLimit,
			int yellowLowLimit, int redLowLimit, float rawValues, String component) {
		super();
		this.dateTime = dateTime;
		this.satelliteId = satelliteId;
		this.redHighLimit = redHighLimit;
		this.yellowHighLimit = yellowHighLimit;
		this.yellowLowLimit = yellowLowLimit;
		this.redLowLimit = redLowLimit;
		this.rawValues = rawValues;
		this.component = component;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public int getSatelliteId() {
		return satelliteId;
	}

	public int getRedHighLimit() {
		return redHighLimit;
	}

	public int getYellowHighLimit() {
		return yellowHighLimit;
	}

	public int getYellowLowLimit() {
		return yellowLowLimit;
	}

	public int getRedLowLimit() {
		return redLowLimit;
	}

	public float getRawValues() {
		return rawValues;
	}

	public String getComponent() {
		return component;
	}
	
	// format attributes to JSON object and return
	public JSONObject toJSONString()
    {
		Instant instant = getDateTime().toInstant(ZoneOffset.UTC);
		JSONObject record = new JSONObject();
		record.put("satelliteId", getSatelliteId());
		record.put("severity", " ");
		record.put("component", getComponent());
		record.put("timestamp", instant);
		
		return record;

    }
}
