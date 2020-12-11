package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class TemperatureSensorDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(TemperatureSensorDeviceHandler.class);

  public TemperatureSensorDeviceHandler()
  {
    log.trace("TemperatureSensorDeviceHandler()");
  }

  @Override
  public Map<String, Object> execute(String command, Map<String, Object> parameters)
  {
    log.warn("Command |%s| not implemented.", command);
    return null;
  }

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
}
