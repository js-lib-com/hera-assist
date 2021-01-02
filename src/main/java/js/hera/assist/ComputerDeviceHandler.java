package js.hera.assist;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import js.lang.AsyncTask;
import js.log.Log;
import js.log.LogFactory;

public class ComputerDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(ComputerDeviceHandler.class);

  public ComputerDeviceHandler()
  {
    log.trace("ComputerDeviceHandler()");
  }

  @Override
  public Map<String, Object> execute(String user, String command, Map<String, Object> parameters)
  {
    switch(command) {
    case "action.devices.commands.OnOff":
      boolean parameter = (boolean)parameters.get("on");
      if(parameter) {
        // by convention, for computers, device name is the MAC address
        WOL(getDeviceName());
      }
      else {
        stanby(getHostName());
      }
      return state(parameter);

    default:
      log.warn("Command |%s| not implemented.", command);
      break;
    }
    return null;
  }

  @Override
  public Map<String, Object> query(String user)
  {
    return state(true);
  }

  private static Map<String, Object> state(boolean state)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("on", state);
    return states;
  }

  private static final String BCAST_ADDRESS = "192.168.0.255";
  private static final int BCAST_PORT = 9;

  public static void WOL(String MAC)
  {
    try {
      byte[] macBytes = getMacBytes(MAC);
      byte[] payload = new byte[6 + 16 * macBytes.length];
      for(int i = 0; i < 6; i++) {
        payload[i] = (byte)0xFF;
      }
      for(int i = 6; i < payload.length; i += macBytes.length) {
        System.arraycopy(macBytes, 0, payload, i, macBytes.length);
      }

      DatagramPacket packet = new DatagramPacket(payload, payload.length, InetAddress.getByName(BCAST_ADDRESS), BCAST_PORT);
      DatagramSocket socket = new DatagramSocket();
      socket.send(packet);
      socket.close();

      log.debug("WOL packet sent to |%s|.", MAC);
    }
    catch(Exception e) {
      log.error("Failed to send WOL packet: %s", e);
    }
  }

  private static byte[] getMacBytes(String macStr) throws IllegalArgumentException
  {
    byte[] bytes = new byte[6];
    String[] hex = macStr.split("(\\:|\\-)");
    if(hex.length != 6) {
      throw new IllegalArgumentException("Invalid MAC address.");
    }
    try {
      for(int i = 0; i < 6; i++) {
        bytes[i] = (byte)Integer.parseInt(hex[i], 16);
      }
    }
    catch(NumberFormatException e) {
      throw new IllegalArgumentException("Invalid hex digit in MAC address.");
    }
    return bytes;
  }

  private static void stanby(String hostName)
  {
    log.trace("stanby(String)");
    AsyncTask<Void> task = new AsyncTask<Void>()
    {
      @Override
      protected Void execute() throws Throwable
      {
        HttpURLConnection connection = null;
        try {
          URL url = new URL(String.format("http://%s:7777/API/Standby.rmi", hostName));
          log.debug("URL: %s", url);

          connection = (HttpURLConnection)url.openConnection();
          connection.setRequestMethod("POST");

          int responseCode = connection.getResponseCode();
          log.debug("Response code: %d", responseCode);
        }
        catch(Throwable t) {
          log.error(t);
        }
        finally {
          if(connection != null) {
            connection.disconnect();
          }
        }
        return null;
      }
    };
    task.start();
  }
}
