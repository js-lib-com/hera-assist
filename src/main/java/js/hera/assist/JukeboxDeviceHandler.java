package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class JukeboxDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(JukeboxDeviceHandler.class);

  public JukeboxDeviceHandler()
  {
    log.trace("JukeboxDeviceHandler()");
  }

  @Override
  public Map<String, Object> execute(String user, String command, Map<String, Object> parameters)
  {
    log.trace("execute(String,Map<String, Object>)");
    return query(user);
  }

  @Override
  public Map<String, Object> query(String user)
  {
    log.trace("query()");
    Map<String, Object> states = new HashMap<>();
    states.put("online", true);
    states.put("on", true);
    states.put("currentVolume", 5);
    states.put("isMuted", false);
    states.put("activityState", "ACTIVE");
    states.put("playbackState", "PLAYING");
    return states;
  }
}
