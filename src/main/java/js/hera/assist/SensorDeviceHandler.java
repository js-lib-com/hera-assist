package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class SensorDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(SensorDeviceHandler.class);

  public SensorDeviceHandler()
  {
    log.trace("SensorDeviceHandler()");
  }

  @Override
  public Map<String, Object> execute(String user, String command, Map<String, Object> parameters)
  {
    log.warn("Command |%s| not implemented.", command);
    return null;
  }

  @Override
  public Map<String, Object> query(String user)
  {
    Value value = rmi(user, "getValue", Value.class);

    Map<String, Object> states = new HashMap<>();
    states.put("online", true);
    // states.put("humiditySetpointPercent", humidity);
    states.put("humidityAmbientPercent", Math.round(value.humidity));
    // states.put("temperatureSetpointCelsius", temperature);
    states.put("temperatureAmbientCelsius", value.temperature);
    return states;
  }

  private static class Value
  {
    double humidity;
    double temperature;
  }
}
