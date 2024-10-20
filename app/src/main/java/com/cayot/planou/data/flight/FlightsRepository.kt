package com.cayot.planou.data.flight

import kotlinx.coroutines.flow.Flow

interface FlightsRepository {

	fun getAllFlightsStream() : Flow<List<Flight>>

	fun getFlight(id: Int): Flow<Flight>

	suspend fun insertFlight(flight: Flight)

	suspend fun deleteFlight(flight: Flight)

	suspend fun updateFlight(flight: Flight)
}