package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class ThermostatDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(ThermostatDeviceHandler.class);

  public ThermostatDeviceHandler()
  {
    log.trace("ThermostatDeviceHandler()");
  }

  @Override
  public Map<String, Object> query()
  {
    return states(rmi("getState", State.class));
  }

  @Override
  public Map<String, Object> execute(String command, Map<String, Object> parameters)
  {
    switch(command) {
    case "action.devices.commands.ThermostatTemperatureSetpoint":
      Number setpoint = (Number)parameters.get("thermostatTemperatureSetpoint");
      return states(rmi("updateSetpoint", setpoint.toString(), State.class));

    default:
      log.warn("Command |%s| not implemented.", command);
      break;
    }
    return null;
  }

  private static Map<String, Object> states(State state)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("online", true);
    states.put("thermostatMode", "heat");
    states.put("thermostatTemperatureSetpoint", state.setpoint);
    states.put("thermostatTemperatureAmbient", state.temperature);
    return states;
  }

  private static class State
  {
    double setpoint;
    double temperature;
  }
}
