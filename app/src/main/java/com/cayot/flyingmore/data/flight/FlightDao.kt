package com.cayot.flyingmore.data.flight

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insert(flight: Flight)

	@Update
	suspend fun update(flight: Flight)

	@Query("DELETE from flights WHERE id = :id")
	suspend fun deleteById(id: Int)

	@Query("SELECT * from flights WHERE id = :id")
	fun getFlight(id: Int) : Flow<Flight?>

	@Query("SELECT * from flights ORDER BY departureTime DESC")
	fun getAllFlights() : Flow<List<Flight>>
}