package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastFragment extends Fragment implements View.OnClickListener {

    final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    final String QUERY_PARAM = "q";
    final String FORMAT_PARAM = "mode";
    final String UNITS_PARAM = "units";
    final String DAYS_PARAM = "cnt";
    public Button mButtonPostcode;
    public EditText mEditTextPostcode;
    public String[] forecastList;

    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
//            new FetchWeatherTask().execute();
//            FetchWeatherTask weatherTask = new FetchWeatherTask();
//            weatherTask.execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mButtonPostcode = (Button) rootView.findViewById(R.id.button_postcode);
        mButtonPostcode.setOnClickListener(this);

        mEditTextPostcode = (EditText) rootView.findViewById(R.id.editText_postcode);

        String[] forecastArray = {
                "Today - Sunny - 88/64",
                "Tomorrow - Sunny - 88/64",
                "Weds - Sunny - 88/64",
                "Thurs - Sunny - 88/64",
                "Fri - Sunny - 88/64",
                "Sat - Sunny - 88/64",
                "Sun - Sunny - 88/64"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));


        ArrayList<String> weekForecastArrayList = new ArrayList<>();
        weekForecastArrayList.add("Today - Sunny - 88/64");
        weekForecastArrayList.add("Tomorrow - Sunny - 88/64");
        weekForecastArrayList.add("Weds - Sunny - 88/64");
        weekForecastArrayList.add("Thurs - Sunny - 88/64");
        weekForecastArrayList.add("Fri - Sunny - 88/64");
        weekForecastArrayList.add("Sat - Sunny - 88/64");
        weekForecastArrayList.add("Sun - Sunny - 88/64");
        weekForecastArrayList.add("Today - Cloudy - 88/64");
        weekForecastArrayList.add("Tomorrow - Cloudy - 88/64");
        weekForecastArrayList.add("Weds - Cloudy - 88/64");
        weekForecastArrayList.add("Thurs - Cloudy - 88/64");
        weekForecastArrayList.add("Fri - Cloudy - 88/64");
        weekForecastArrayList.add("Sat - Cloudy - 88/64");
        weekForecastArrayList.add("Sun - Cloudy - 88/64");
        weekForecastArrayList.add("Today - Rainy - 88/64");
        weekForecastArrayList.add("Tomorrow - Rainy - 88/64");
        weekForecastArrayList.add("Weds - Rainy - 88/64");
        weekForecastArrayList.add("Thurs - Rainy - 88/64");
        weekForecastArrayList.add("Fri - Rainy - 88/64");
        weekForecastArrayList.add("Sat - Rainy - 88/64");
        weekForecastArrayList.add("Sun - Rainy - 88/64");

        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecastArrayList);

        // Smaller subtree to search for given view
//            ListView mListView = (ListView) container.findViewById(R.id.listview_forecast);
        ListView mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);

//        new FetchWeatherTask().execute();

        return rootView;
    }

    @Override
    public void onClick(View v) {


        /*Uri.Builder builder = new Uri.Builder();
//        "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
        builder.scheme("http").
                authority("api.openweathermap.org").
                appendPath("data/2.5").appendPath("forecast").appendPath("daily").
                appendQueryParameter("q", "94043").*/

        if (v == mButtonPostcode) {
            String postcode = mEditTextPostcode.getText().toString();

            new FetchWeatherTask().execute(postcode);

//            Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        }
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = "DEBUG-->";//FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            Log.d(LOG_TAG, "got String: " + params[0]);

            String format="json";
            String units="metric";
            int numDays=7;

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(7)).build();

            Log.d(LOG_TAG, "URL is " + builtUri.toString());

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(builtUri.toString());
//                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.d(LOG_TAG, "Forecast JSON string: " + forecastJsonStr);



            } catch (IOException e) {
//                Log.e("PlaceholderFragment", "Error ", e);
                Log.e(LOG_TAG, "Error ", e);

                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
//                        Log.e("PlaceholderFragment", "Error closing stream", e);
                        Log.e(LOG_TAG, "Error closing stream", e);

                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] results) {
            Log.d(LOG_TAG, "DEBUG log");
            if (results != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : results) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }

            Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
        * so for convenience we're breaking it out into its own method now.
        */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.d(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }
    }

    public class WeatherDataParser {

        /**
         * Given a string of the form returned by the api call:
         * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
         * retrieve the maximum temperature for the day indicated by dayIndex
         * (Note: 0-indexed, so 0 would refer to the first day).
         */
        private final String LOG_TAG = "DEBUG-->";//WeatherDataParser.class.getSimpleName();

        public double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) throws JSONException {
            // TODO: add parsing code here
            String in;
            JSONObject reader = new JSONObject(weatherJsonStr);

            String country = reader.getString("country");
            Log.d(LOG_TAG, "is: " + country);
            Log.d(LOG_TAG, "weatherJsonStr is: " + weatherJsonStr + " dayIndex: " + dayIndex + " reader " + reader);

            JSONObject objWeather = new JSONObject(weatherJsonStr);
            JSONArray arrDays = objWeather.getJSONArray("list");
            JSONObject objDayInfo = (JSONObject)  arrDays.get(dayIndex);
            JSONObject objTempInfo = (JSONObject) objDayInfo.getJSONObject("temp");
            Double tempMax = objTempInfo.getDouble("max");

            Log.d(LOG_TAG, "tempMax: " + tempMax);
            return tempMax;
//
//            JSONObject list = reader.getJSONObject("list");
//            Log.d(LOG_TAG, "is: " + list);
//            JSONObject temp = list.getJSONObject("temp");
//            Log.d(LOG_TAG, "is: " + temp);
//            String minTemp = temp.getString("min");
//            String maxTemp = temp.getString("max");
//            Log.d(LOG_TAG, "getMaxTemperatureForDay min: " + minTemp + " max: " + maxTemp);
//
//
//            JSONObject weather = reader.getJSONObject("weather");
//            String main = weather.getString("main");
//
//            Log.d(LOG_TAG, "getMaxTemperatureForDay min: " + minTemp + " max: " + " main: " + main);
//
//            return -1;

        }
    }
}