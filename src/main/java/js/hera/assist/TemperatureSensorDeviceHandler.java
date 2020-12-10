package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

public class TemperatureSensorDeviceHandler extends DeviceHandler
{
  @Override
  public Map<String, Object> query()
  {
    double temperature = rmi("getTemperature", double.class);
    Map<String, Object> states = new HashMap<>();
    states.put("online", true);
    states.put("temperatureSetpointCelsius", temperature);
    states.put("temperatureAmbientCelsius", temperature);
    return states;
  }

  @Override
  public Map<String, Object> execute(Map<String, Object> parameters)
  {
    return parameters;
  }
}
