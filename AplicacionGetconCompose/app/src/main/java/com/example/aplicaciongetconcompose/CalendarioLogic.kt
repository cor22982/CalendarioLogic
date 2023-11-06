package com.example.aplicaciongetconcompose

import android.app.Activity
import android.content.Context

import android.net.ConnectivityManager
import android.util.Log
import com.example.aplicaciongetconcompose.ui.theme.Constants.REQUEST_ACCOUNT_PICKER
import com.example.aplicaciongetconcompose.ui.theme.Constants.REQUEST_GOOGLE_PLAY_SERVICES
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class CalendarioLogic(private val context: Context) {
    private var mCredential: GoogleAccountCredential? = null
    private var mService: Calendar? = null
    init {
        initCredentials()

    }
    private fun initCredentials() {
        mCredential = GoogleAccountCredential.usingOAuth2(
            context,
            arrayListOf(CalendarScopes.CALENDAR)
        )
            .setBackOff(ExponentialBackOff())

        initCalendarBuild(mCredential)
    }
    private fun initCalendarBuild(credential: GoogleAccountCredential?) {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        mService = Calendar.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName("GetEventCalendar")
            .build()
      mCredential!!.selectedAccountName = "cor22982@uvg.edu.gt"
    }



    fun getDataFromCalendar(){
        val now = DateTime(System.currentTimeMillis())
        try {
            Log.d("Google proc", "se comenzó")
            val events = mService!!.events().list("mcorderoeb5bm2019071@gmail.com")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
            Log.d("Google proc", "Se obtuvieron los eventos")
            val items = events.items
            Log.d("Google proc", "Se creo items")

            for (event in items) {
                Log.d("Mostrar evento", "Evento: "+event.summary+" /"+event.start.dateTime.toString()+" /"+event.end.dateTime)

            }


        } catch (e: UserRecoverableAuthIOException) {

            Log.d("Google error 2", e.message.toString())
            Log.d("Google error 2", e.stackTraceToString())
        } catch (e: IOException) {
            Log.d("Google error", e.message.toString())
            Log.d("Google error", e.stackTraceToString())

        }
    }

    fun getResultsFromApi(activity: Activity) {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices(activity)
        } else if (mCredential!!.selectedAccountName == null) {
            // No se ha seleccionado una cuenta, solicitar al usuario que elija una
            val intent = mCredential!!.newChooseAccountIntent()
            activity.startActivityForResult(intent, REQUEST_ACCOUNT_PICKER)
        } else if (!isDeviceOnline(activity)) {
            Log.d("No hay conexión", "No hay conexión a Internet.")
        } else {
            // Ejecuta la operación en un hilo secundario
            CoroutineScope(Dispatchers.IO).launch {
        //        getDataFromCalendar()
                setDataFromCalendar()
            }
        }
    }


    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    //Cihazın Google play servislerini destekleyip desteklemediğini kontrol ediyor
    private fun acquireGooglePlayServices(activity: Activity) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            val dialog = apiAvailability.getErrorDialog(activity, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES)
            dialog?.show()
        }
    }

    private fun isDeviceOnline(activity: Activity): Boolean {
        val connMgr = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    fun setDataFromCalendar(){

        try {
            Log.d("Google proc", "se comenzó")
            val event = Event()
                .setSummary("EVENTO NUEVO")
                .setLocation("Ciudad de Guatemala, Edificio CIT Universidad del Valle")
                .setDescription("Esta es una prueba")
            val startDateTime = DateTime("2023-11-06T13:00:00-06:00")
            val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Guatemala")
            event.start = start
            val endDateTime = DateTime("2023-11-06T16:00:00-06:00")
            val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Guatemala")
            event.end = end

            val insertado = mService!!.events().insert("mcorderoeb5bm2019071@gmail.com",event).execute()
            Log.d("Google proc", "Evento insertado: ${insertado.htmlLink}")

        } catch (e: UserRecoverableAuthIOException) {

            Log.d("Google error 2", e.message.toString())
            Log.d("Google error 2", e.stackTraceToString())
        } catch (e: IOException) {
            Log.d("Google error", e.message.toString())
            Log.d("Google error", e.stackTraceToString())

        }
    }

}