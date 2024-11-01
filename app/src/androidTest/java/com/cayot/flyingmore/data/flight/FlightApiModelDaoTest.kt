package com.cayot.flyingmore.data.flight

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cayot.flyingmore.data.DatabaseTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
//class FlightApiModelDaoTest : DatabaseTest() {
//    private lateinit var flightDao: FlightDao
//
//    private val flightList = listOf(FlightApiModel.getPlaceholderFlight1(), FlightApiModel.getPlaceholderFlight2())
//
//    override fun createDatabase() {
//        super.createDatabase()
//        flightDao = flyingMoreDatabase.flightDao()
//    }
//
//    private suspend fun fillDatabase() {
//        flightList.forEach {
//            flightDao.insert(it)
//        }
//    }
//
//    @Test
//    fun getAllFlightsTest() = runBlocking {
//        fillDatabase()
//        val dbFlights = flightDao.getAllFlightsBrief().first()
//        assertEquals(dbFlights.size, flightList.size)
//        assertEquals(dbFlights, flightList)
//    }
//
//    @Test
//    fun insertFlightTest() = runBlocking {
//        val flight = FlightApiModel.getPlaceholderFlight1()
//        flightDao.insert(flight)
//        val dbFlight = flightDao.getAllFlightsBrief().first().first()
//        assertEquals(dbFlight, flight)
//    }
//
//    @Test
//    fun updateFlightTest() = runBlocking {
//        var flight = FlightApiModel.getPlaceholderFlight1()
//        flightDao.insert(flight)
//        flight = flightDao.getAllFlightsBrief().first().first()
//        val updatedFlight = flight.copy(departureTime = System.currentTimeMillis() + 2000)
//        flightDao.update(updatedFlight)
//        val dbFlight = flightDao.getFlight(flight.id).first()
//        assertEquals(dbFlight, updatedFlight)
//    }
//
//    @Test
//    fun deleteFlightByIdTest() = runBlocking {
//        val flight = FlightApiModel.getPlaceholderFlight1()
//        flightDao.insert(flight)
//        flightDao.deleteById(flight.id)
//        val dbFlight = flightDao.getFlight(flight.id).first()
//        assertEquals(dbFlight, null)
//    }
//}
