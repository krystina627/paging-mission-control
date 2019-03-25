package satellite_alert;

import java.time.Instant;

/**
*
* @author Krystina Poling
*/

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.json.JSONObject;

// Child class of SatelliteRecord inherits all attributes and methods from parent
public class ThermostatRecord extends SatelliteRecord {

	
	public ThermostatRecord(LocalDateTime dateTime, int satelliteId, int redHighLimit, int yellowHighLimit,
			int yellowLowLimit, int redLowLimit, float rawValues, String component)
	{
		super(dateTime, satelliteId, redHighLimit, yellowHighLimit,
				yellowLowLimit, redLowLimit, rawValues, component);
}
	@Override
	public JSONObject toJSONString()
    {
		Instant instant = getDateTime().toInstant(ZoneOffset.UTC);

		JSONObject record = new JSONObject();
		record.put("satelliteId", getSatelliteId());
		record.put("severity", "RED HIGH");
		record.put("component", getComponent());
		
		record.put("timestamp", instant);
		
		return record;

    }

}
