package com.example.mrweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.os.AsyncTask
/* we need to grab weather information from an API, so we'll make a http request to the URL of the API, so we use AsyncTask.
AsyncTask enables proper and easy use of the UI thread. This class allows you to perform background operations and
publish results on the UI thread without having to manipulate threads and/or handlers */

import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var CITY: String = "Guntur,IN"
    val API: String = "8e9f34d33a145d2a0349928cc6de5d96"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                var jsonObj = JSONObject(result)
                var main = jsonObj.getJSONObject("main")
                var sys = jsonObj.getJSONObject("sys")
                var wind = jsonObj.getJSONObject("wind")
                var weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                var updatedAt:Long = jsonObj.getLong("dt")
                var updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                var temp = main.getString("temp")+"°C"
                var tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                var tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                var pressure = main.getString("pressure")
                var humidity = main.getString("humidity")

                var sunrise:Long = sys.getLong("sunrise")
                var sunset:Long = sys.getLong("sunset")
                var windSpeed = wind.getString("speed")
                var weatherDescription = weather.getString("description")

                var address = jsonObj.getString("name")+", "+sys.getString("country")


                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}