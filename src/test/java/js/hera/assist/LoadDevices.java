package js.hera.assist;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import js.util.Strings;

public class LoadDevices
{
  @Test
  public void loadDevices() throws JSONException, IOException
  {
    JSONArray jsonArray = new JSONArray(Strings.load(getClass().getResourceAsStream("/devices.json")));

    for(int i = 0; i < jsonArray.length(); ++i) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      System.out.println(jsonObject);
    }
  }
}
