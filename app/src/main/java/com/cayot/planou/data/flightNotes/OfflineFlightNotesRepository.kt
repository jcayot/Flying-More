package com.cayot.planou.data.flightNotes

class OfflineFlightNotesRepository(private val flightNotesDao: FlightNotesDao) : FlightNotesRepository {
    override suspend fun getFromFlight(flightId: Int): FlightNotes? {
        return (flightNotesDao.getFromFlight(flightId))
    }

    override suspend fun insertFlightNotes(flightNotes: FlightNotes) {
        return (flightNotesDao.insert(flightNotes))
    }

    override suspend fun updateFlightNotes(flightNotes: FlightNotes) {
        return (flightNotesDao.update(flightNotes))
    }

    override suspend fun removeFlightNotes(flightNotes: FlightNotes) {
        return (flightNotesDao.delete(flightNotes))
    }
}