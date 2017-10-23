package ka20er.aurinwayfinder;

import android.app.IntentService;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataRetrievalService extends IntentService {
    public static final String MESSAGE = "BBoxSent";
    public static final String OPTION = "DataOption";
    public static ArrayList<RelevantLocation> listRelevantLocation;
    private static String BBoxVal;
    private static String dataSetVal;
    private static String selectedSet;
    private static final String PROTOCOL = "http://openapi.aurin.org.au/wfs?request=GetFeature&service=WFS&version=1.0.0&";
    private static final String DATASET1 = "aurin:datasource-VIC_Govt_VCGLR-UoM_AURIN_DB_vcglr_liquor_licence_2016&SRSNAME=EPSG:4283&";
    private static final String DATASET2 = "aurin:datasource-au_govt_dss-UoM_AURIN_national_public_toilets_2017&SRSNAME=EPSG:4283&";
    private static final String DATASET3 = "aurin:datasource-AU_Govt_DHS-UoM_AURIN_DB_au_govt_medicare_offices_2017&SRSNAME=EPSG:4283&";
    private static final String DATASET4 = "aurin:datasource-AU_Govt_DHS-UoM_AURIN_DB_au_govt_centrelink_offices_2017&SRSNAME=EPSG:4283&";
    private static final String DATASET5 = "aurin:datasource-VIC_Govt_DET-UoM_AURIN_DB_vic_school_locations_2016&SRSNAME=EPSG:4283&";
    private static final String DATASET6 = "aurin:datasource-VIC_Govt_DHHS-UoM_AURIN_DB_vic_sport_and_recreation_2015&SRSNAME=EPSG:4283&";
    private static final String DATASET7 = "aurin:datasource-VIC_Govt_DELWP-VIC_Govt_DELWP_datavic_PTV_BUS_STOP&SRSNAME=EPSG:4283&";
    private static final String DATASET8 = "aurin:datasource-VIC_Govt_DELWP-VIC_Govt_DELWP_datavic_PTV_TRAM_STOP&SRSNAME=EPSG:4283&";
    private static final String DATASET9 = "aurin:datasource-VIC_Govt_DELWP-VIC_Govt_DELWP_datavic_PTV_TRAIN_STATION&SRSNAME=EPSG:4283&";

    private static final String STATUS = "RECEIVED";
    private static final int LONGITUDE = 0;
    private static final int LATITUDE = 1;
    private static int totalRetLocation = 0;
    private static HttpURLConnection connection;

    public DataRetrievalService() {
        super("DataRetrievalService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BBoxVal = intent.getStringExtra(MESSAGE);
        dataSetVal = intent.getStringExtra(OPTION);
        if (dataSetVal.equals("0")) {
            selectedSet = DATASET1;
        } else if (dataSetVal.equals("1")) {
            selectedSet = DATASET2;
        } else if (dataSetVal.equals("2")) {
            selectedSet = DATASET3;
        } else if (dataSetVal.equals("3")) {
            selectedSet = DATASET4;
        } else if (dataSetVal.equals("4")) {
            selectedSet = DATASET5;
        } else if (dataSetVal.equals("5")) {
            selectedSet = DATASET6;
        } else if (dataSetVal.equals("6")) {
            selectedSet = DATASET7;
        } else if (dataSetVal.equals("7")) {
            selectedSet = DATASET8;
        } else if (dataSetVal.equals("8")) {
            selectedSet = DATASET9;
        }
        listRelevantLocation = new ArrayList<>();

        try{
            URL url = new URL(PROTOCOL +
                    "TypeName=" + selectedSet +
                    "BBOX=" + BBoxVal + "&OUTPUTFORMAT=application/json");

            // It is a requirement to have the word "Basic "
            String basicAuth = "Basic " + new String(Base64.encodeToString("student:dj78dfGF".getBytes(),0));
            Log.i("BASIC AUTHENTICATION: ", basicAuth);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Log.i("CONNECTION: ", "DONE");

            StringBuilder lineBuilder = new StringBuilder();
            String line;

            line = reader.readLine();
            while (line != null) {
                lineBuilder.append(line);
                line = reader.readLine();
            }

            String dataJSON = lineBuilder.toString();
            Log.i("LINE BUILDER: ", "DONE");
            System.out.println(dataJSON);

            char firstChar = dataJSON.charAt(0);
            if (firstChar != '{') {
                Log.i("STATUS:", "NO DATA");

                Intent notice = new Intent("Nothing_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("NO DATA NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("0")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("LiquorLicense_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("LIQUOR NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("1")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("PublicToilet_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("PUBLIC TOILET NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("2")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("Medicare_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("MEDICARE NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("3")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("Centrelink_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("CENTRELINK NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("4")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("School_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("SCHOOL NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("5")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("RecreationFacility_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("RECREATION NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("6")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("Bus_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("BUS NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("7")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("Tram_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("TRAM NOTICE:", "SENT");
            } else if (dataSetVal.equalsIgnoreCase("8")) {
                parseJSON(dataJSON, dataSetVal);
                Log.i("DATA RETRIEVAL: ", "DONE");

                Intent notice = new Intent("Train_DATA_RETRIEVED");
                notice.putExtra("dataStatus", STATUS);
                sendBroadcast(notice);
                Log.i("TRAIN NOTICE:", "SENT");
            }
        } catch (IOException e) {
            System.out.println("Error Making Connection");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Other Errors");
            e.printStackTrace();
        }
    }

    /*
    * There are some redundancies in this section. The un-optimized codes show clarity and allow for
    * flexibility in the near future should AURIN JSON-based data set change or expand to accomodate
    * broader information
    */
    private void parseJSON (String dataJSON, String dataSetVal) {
        JSONObject readerJSON = null;
        try {
            readerJSON = new JSONObject(dataJSON);
            JSONArray featuresVal = readerJSON.getJSONArray("features");
            totalRetLocation = featuresVal.length();
            Log.i("DATA SIZE:", String.format("%d", totalRetLocation));

            if (dataSetVal.equalsIgnoreCase("0")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("trade_name");
                    if (retName.equals("null")) {
                        retName = properties.getString("licensee");
                    }

                    String retCategory = (String) properties.getString("category");
                    String retTradeHour = (String) properties.getString("trade_hour");
                    String retAfterHour = (String) properties.getString("after_11_p");
                    String retAddress = (String) properties.getString("address_1");
                    String retSuburb = (String) properties.getString("suburb");
                    int retPostcode;
                    if (properties.getString("postcode").equals("null")){
                        retPostcode = 0000;
                    } else {
                        retPostcode = (int) properties.getInt("postcode");
                    }

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
            } else if (dataSetVal.equalsIgnoreCase ("1")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("name");

                    String retCategory = (String) properties.getString("facility_type");
                    String male = (String) properties.getString("male");
                    String female = (String) properties.getString("female");
                    String retAddress = (String) properties.getString("address1");
                    String retSuburb = (String) properties.getString("town");
                    int retPostcode;
                    if (properties.getString("postcode").equals("null")){
                        retPostcode = 0000;
                    } else {
                        retPostcode = (int) properties.getInt("postcode");
                    }
                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, male, female, retAddress, retSuburb, retPostcode));
                }
            } else if (dataSetVal.equalsIgnoreCase ("2")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("site_name");

                    String retCategory = (String) properties.getString("office_type");
                    String retTradeHour = (String) properties.getString("open");
                    String retAfterHour = (String) properties.getString("close");
                    String retAddress = (String) properties.getString("address");
                    String retSuburb = (String) properties.getString("suburb");
                    int retPostcode = (int) properties.getInt("postcode");

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
            } else if (dataSetVal.equalsIgnoreCase("3")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("site_name");

                    String retCategory = (String) properties.getString("office_type");
                    String retTradeHour = (String) properties.getString("open");
                    String retAfterHour = (String) properties.getString("close");
                    String retAddress = (String) properties.getString("address");
                    String retSuburb = (String) properties.getString("suburb");
                    int retPostcode = (int) properties.getInt("postcode");

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
            } else if (dataSetVal.equalsIgnoreCase("4")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("school_name");

                    String retCategory = (String) properties.getString("school_type");
                    String retTradeHour = "Not Applicable";
                    String retAfterHour = "Not Applicable";
                    String retAddress = (String) properties.getString("address_line_1");
                    String retSuburb = (String) properties.getString("address_town");
                    int retPostcode = (int) properties.getInt("address_postcode");

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
            }  else if (dataSetVal.equalsIgnoreCase("5")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("facilityname");

                    String retCategory = (String) properties.getString("sportsplayed");
                    String retTradeHour = "Not Applicable";
                    String retAfterHour = "Not Applicable";
                    String retAddress = (String) properties.getString("streetname");
                    String retSuburb = (String) properties.getString("suburbtown");
                    int retPostcode = (int) properties.getInt("postcode");

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
    /**
     * Data observation about AURIN API and data retrieved for Bus, Tram and Train.
     * Note: Transportation happens in two directions.
     *  1) Duplicate stop names (two usually) occur because one stop can only serves one direction.
     *  2) Some retrieved data can be a little bit over the specified Bounding Box.
     *  This is inherent issue in the dataset.
     */
            } else if (dataSetVal.equalsIgnoreCase("6")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("STOPSPECNAME");

                    String retCategory = "Not Applicable";
                    String retTradeHour = "Not Applicable";
                    String retAfterHour = "Not Applicable";
                    // This is to ensure that I only retrieved one char of value 1 or 2
                    String retAddress = "Zone "+ properties.getString("ZONES").charAt(0);
                    String retSuburb = "Not Applicable";
                    int retPostcode = 0;

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
            } else if (dataSetVal.equalsIgnoreCase("7")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("STOPSPECNAME");

                    String retCategory = "Not Applicable";
                    String retTradeHour = "Not Applicable";
                    String retAfterHour = "Not Applicable";
                    // This is to ensure that I only retrieved one char of value 1 or 2
                    String retAddress = "Zone "+ properties.getString("ZONES").charAt(0);
                    String retSuburb = "Not Applicable";
                    int retPostcode = 0;

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
            } else if (dataSetVal.equalsIgnoreCase("8")) {
                for (int i = 0; i < totalRetLocation; i++) {
                    JSONObject oneJSONObject = (JSONObject) featuresVal.getJSONObject(i);
                    System.out.printf("Iteration %d:, %s\n", i, oneJSONObject);

                    JSONObject geometry = (JSONObject) oneJSONObject.get("geometry");
                    JSONArray internalCoord = (JSONArray) geometry.get("coordinates");
                    double retLongitude = internalCoord.getDouble(LONGITUDE);
                    double retLatitude = internalCoord.getDouble(LATITUDE);

                    JSONObject properties = (JSONObject) oneJSONObject.get("properties");
                    String retName = (String) properties.getString("STATIONNAME");

                    String retCategory = (String) properties.getString("STATIONTYPE");
                    String retTradeHour = "Not Applicable";
                    String retAfterHour = "Not Applicable";
                    // This is to ensure that I only retrieved one char of value 1 or 2
                    String retAddress = "Zone "+ properties.getString("ZONES").charAt(0);
                    String retSuburb = "Not Applicable";
                    int retPostcode = 0;

                    listRelevantLocation.add(new RelevantLocation(retLongitude, retLatitude, retName.toString(),
                            retCategory, retTradeHour, retAfterHour, retAddress, retSuburb, retPostcode));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
