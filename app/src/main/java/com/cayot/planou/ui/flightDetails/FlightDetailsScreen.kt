package com.cayot.planou.ui.flightDetails

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cayot.planou.R
import com.cayot.planou.data.FlightMapState
import com.cayot.planou.data.airport.Airport
import com.cayot.planou.data.flight.Flight
import com.cayot.planou.data.flight.getArrivalTimeString
import com.cayot.planou.data.flight.getDepartureDateString
import com.cayot.planou.data.flight.getDepartureTimeString
import com.cayot.planou.data.flight.getDistanceString
import com.cayot.planou.data.flightNotes.FlightNotes
import com.cayot.planou.ui.AppViewModelProvider
import com.cayot.planou.ui.PlanouTopBar
import com.cayot.planou.ui.composable.FlightMap
import com.cayot.planou.ui.composable.LabelledData
import com.cayot.planou.ui.composable.composableBitmap
import com.cayot.planou.ui.navigation.PlanouScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightDetailsScreen(
	navigateBack: () -> Unit,
	onNavigateUp: () -> Unit,
	modifier: Modifier = Modifier
) {
	val viewModel: FlightDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
	val uiState by viewModel.uiState.collectAsState()
	val context = LocalContext.current

	if (uiState.flight != null) {
		LaunchedEffect (Unit) {
			viewModel.shareCard.collect {
				shareFlight(context, it, uiState.flight!!.originAirportCode, uiState.flight!!.destinationAirportCode)
			}
		}
	}

	Scaffold (
		topBar = {
			PlanouTopBar(
				title = stringResource(PlanouScreen.Details.title),
				canNavigateBack = true,
				navigateUp = onNavigateUp,
				actions = {
					if (uiState.flight != null) {
						Box {
							IconButton(onClick = viewModel::updateTopBarDropdownVisibility) {
								Icon(Icons.Default.MoreVert, contentDescription = "Options")
							}

							DropdownMenu(
								expanded = uiState.topBarDropdownExpanded,
								onDismissRequest = viewModel::updateTopBarDropdownVisibility
							) {
								DropdownMenuItem(
									onClick = { },
									text = {
										Text(
											text = stringResource(R.string.edit_flight),
											style = typography.titleMedium
										)
									}
								)
								DropdownMenuItem(
									onClick = { },
									text = {
										Text(
											text = stringResource(R.string.delete_flight),
											style = typography.titleMedium

										)
									}
								)
								if (uiState.flightNotes != null) {
									DropdownMenuItem(
										onClick = viewModel::deleteFlightNotes,
										text = {
											Text(
												text = stringResource(R.string.delete_notes),
												style = typography.titleMedium
											)
										}
									)
								}
							}
						}
					}
				}
			)
		}
	) { innerPadding ->
		FlightDetailsScreenContent(
			uiState = uiState,
			onBackPressed = navigateBack,
			onSharePressed = viewModel::shareFlightCard,
			updateNotesVisibility = viewModel::updateNotesVisibility,
			onFlightNotesChange = {
				viewModel.onFlightNotesChange(uiState.flightNotes!!.copy(text = it))
			},
			editNotes = viewModel::editNotes,
			discardNotesChanges = viewModel::discardFlightNotesChanges,
			saveNotes = viewModel::saveFlightNotes,
			modifier = modifier
				.padding(
					start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
					end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
					top = innerPadding.calculateTopPadding()
				)
				.fillMaxWidth()
		)
	}
}

@Composable
fun FlightDetailsScreenContent(
	uiState: FlightDetailsUIState,
	onBackPressed: () -> Unit,
	onSharePressed: (Bitmap) -> Unit,
	updateNotesVisibility: () -> Unit,
	onFlightNotesChange: (String) -> Unit,
	editNotes: () -> Unit,
	discardNotesChanges: () -> Unit,
	saveNotes: () -> Unit,
	modifier: Modifier = Modifier
) {
	if (uiState.flight != null) {
		FlightDetails(
			uiState = uiState,
			onBackPressed = onBackPressed,
			onSharePressed = onSharePressed,
			updateNotesVisibility = updateNotesVisibility,
			onFlightNotesChange = onFlightNotesChange,
			editNotes = editNotes,
			discardNotesChanges = discardNotesChanges,
			saveNotes = saveNotes,
			modifier = modifier
		)
	} else if (uiState.isRetrievingFlight) {
		Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Column (
				modifier = Modifier.padding(dimensionResource(R.dimen.padding_smadium)),
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_smadium)),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				CircularProgressIndicator(
					modifier = Modifier.size(50.dp)
				)
				Text(
					text = stringResource(R.string.loading_flight_data),
					style = typography.titleLarge
				)
			}
		}
	} else {
		Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Column (
				modifier = Modifier.padding(dimensionResource(R.dimen.padding_smadium)),
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_smadium)),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = stringResource(R.string.error_retriving_flight),
					style = typography.titleLarge
				)
			}
		}
	}
}

@Composable
fun FlightDetails(
	uiState: FlightDetailsUIState,
	onBackPressed: () -> Unit = {},
	onSharePressed: (Bitmap) -> Unit = {},
	updateNotesVisibility: () -> Unit = {},
	onFlightNotesChange: (String) -> Unit = {},
	editNotes: () -> Unit = {},
	discardNotesChanges: () -> Unit = {},
	saveNotes: () -> Unit = {},
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = dimensionResource(id = R.dimen.padding_smadium)),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_smadium)),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		val flightCard = composableBitmap { FlightCard(
			flight = uiState.flight!!,
			originAirport = uiState.retrievedOriginAirport,
			destinationAirport = uiState.retrievedDestinationAirport,
			flightMapState = uiState.flightMapState,
		) }
		FlightNotesComposable(
			flightNotes = uiState.flightNotes,
			notesVisible = uiState.notesVisible,
			notesEdition = uiState.notesEdition,
			updateNotesVisibility = updateNotesVisibility,
			onFlightNotesChange = onFlightNotesChange,
			discardNotesChanges = discardNotesChanges,
			editNotes = editNotes,
			saveNotes = saveNotes,
			modifier = Modifier.fillMaxWidth()
		)
		ActionButtons(
			flightCard = flightCard,
			onSharePressed = onSharePressed,
			onBackPressed = onBackPressed,
			modifier = Modifier.fillMaxWidth()
		)
	}
}

@Composable
fun FlightCard(
	flight: Flight,
	originAirport: Airport?,
	destinationAirport: Airport?,
	flightMapState: FlightMapState?,
	modifier: Modifier = Modifier,
	elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
) {
	Card(
		elevation = elevation,
		modifier = modifier
	) {
		Column (
			modifier = Modifier.padding(dimensionResource(R.dimen.padding_smadium)),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_smadium))
		){
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier.height(200.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp))
			) {
				if (flightMapState != null) {
					FlightMap(
						flightMapState = flightMapState,
					)
				}
			}
			Row (
				modifier = Modifier.fillMaxWidth()
			){
				Text(
					text = flight.airline,
					style = typography.headlineMedium,
					overflow = TextOverflow.Ellipsis,
					fontWeight = FontWeight.SemiBold
				)
				Spacer(modifier = modifier.weight(1f))
				LabelledData(
					horizontalAlignment = Alignment.CenterHorizontally,
					labelText = stringResource(R.string.date),
					labelStyle = typography.labelSmall,
					dataText = flight.getDepartureDateString(),
					dataStyle = typography.bodyMedium,
					dataWeight = FontWeight.SemiBold,
				)
			}
			Row (
				modifier = Modifier.fillMaxWidth()
					.height(64.dp)
			){
				LabelledData(
					horizontalAlignment = Alignment.Start,
					labelText = originAirport?.municipality ?: stringResource(R.string.error_retriving_airport),
					dataText = originAirport?.iataCode ?: flight.originAirportCode,
					dataStyle = typography.displayMedium,
					modifier = Modifier.weight(1f)
						.fillMaxHeight())
				Column (
					horizontalAlignment = Alignment.CenterHorizontally,
					modifier = Modifier.weight(1f)
						.fillMaxHeight()
				){
					Text(
						text = flight.flightNumber,
						style = typography.labelMedium,
						maxLines = 1,
						modifier = Modifier.weight(1f)
							.padding(2.dp)
					)
					Image(
						painter = painterResource(R.drawable.flight_24px),
						contentDescription = stringResource(R.string.plane_right_icon),
						alignment = Alignment.Center,
						contentScale = ContentScale.FillHeight,
						modifier = Modifier
							.weight(2f)
					)
				}
				LabelledData(
					horizontalAlignment = Alignment.End,
					labelText = destinationAirport?.municipality ?: stringResource(R.string.error_retriving_airport),
					dataText = destinationAirport?.iataCode ?: flight.destinationAirportCode,
					dataStyle = typography.displayMedium,
					modifier = Modifier.weight(1f)
						.fillMaxHeight())
			}
			Row (
				modifier = Modifier.fillMaxWidth()
					.height(54.dp)
			) {
				LabelledData (
					horizontalAlignment = Alignment.Start,
					labelText = stringResource(R.string.plane_model) + " :",
					dataText = flight.planeModel,
					dataStyle = typography.headlineSmall,
					dataWeight = FontWeight.SemiBold,
					dataOverflow = TextOverflow.Ellipsis,
					modifier = Modifier.weight(4f)
				)
				Spacer(modifier.weight(1f))
				LabelledData (
					horizontalAlignment = Alignment.End,
					labelText = stringResource(R.string.travel_class) + " :",
					dataText = flight.travelClass.name,
					dataStyle = typography.headlineSmall,
					dataWeight = FontWeight.SemiBold,
					dataOverflow = TextOverflow.Ellipsis,
					modifier = Modifier.weight(4f)
				)
			}
			Row (
				modifier = Modifier.fillMaxWidth()
			) {
				LabelledData(
					horizontalAlignment = Alignment.CenterHorizontally,
					labelText = stringResource(R.string.departure_time),
					labelStyle = typography.labelSmall,
					dataText = flight.getDepartureTimeString(),
					dataStyle = typography.bodyMedium,
					dataWeight = FontWeight.SemiBold,
					modifier = Modifier.weight(1f)
				)
				LabelledData(
					horizontalAlignment = Alignment.CenterHorizontally,
					labelText = stringResource(R.string.arrival_time),
					labelStyle = typography.labelSmall,
					dataText = flight.getArrivalTimeString(),
					dataStyle = typography.bodyMedium,
					dataWeight = FontWeight.SemiBold,
					modifier = Modifier.weight(1f)
				)
				LabelledData(
					horizontalAlignment = Alignment.CenterHorizontally,
					labelText = stringResource(R.string.distance),
					labelStyle = typography.labelSmall,
					dataText = flight.getDistanceString(),
					dataStyle = typography.bodyMedium,
					dataWeight = FontWeight.SemiBold,
					modifier = Modifier.weight(1f)
				)
				LabelledData(
					horizontalAlignment = Alignment.CenterHorizontally,
					labelText = stringResource(R.string.seat),
					labelStyle = typography.labelSmall,
					dataText = "-",
					dataStyle = typography.bodyMedium,
					dataWeight = FontWeight.SemiBold,
					modifier = Modifier.weight(1f)
				)
			}
		}
	}
}

@Composable
fun FlightNotesComposable(
	flightNotes: FlightNotes?,
	notesVisible: Boolean,
	notesEdition: Boolean,
	updateNotesVisibility: () -> Unit = {},
	onFlightNotesChange: (String) -> Unit = {},
	editNotes: () -> Unit = {},
	discardNotesChanges: () -> Unit = {},
	saveNotes: () -> Unit = {},
	modifier: Modifier = Modifier
) {
	Row (
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier.height(25.dp)
			.clickable { updateNotesVisibility() }
	) {
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier.fillMaxHeight()
		) {
			Text(
				text = stringResource(R.string.notes),
				textAlign = TextAlign.Start,
				style = typography.bodyLarge,
			)
		}
		Icon(
			Icons.Filled.ArrowDropDown,
			null,
			Modifier.rotate(if (notesVisible) 0f else 270f)
				.fillMaxHeight()
		)
	}
	AnimatedVisibility(notesVisible) {
		Card(
			modifier = modifier
		) {
			Column (
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_smadium)),
				modifier = Modifier.fillMaxWidth()
					.padding(dimensionResource(R.dimen.padding_small))
			) {
				if (!notesEdition) {
					if (flightNotes != null) {
						Text(
							text = flightNotes.text,
							modifier = Modifier.fillMaxWidth()
						)
					}
					Button (
						onClick = editNotes
					) {
						Text(
							text =  if (flightNotes != null)
								stringResource(R.string.edit_notes)
							else
								stringResource(R.string.create_notes),
							fontSize = 14.sp
						)
					}
				} else {
					if (flightNotes != null) {
						TextField(
							value = flightNotes.text,
							onValueChange = onFlightNotesChange,
							modifier = Modifier.fillMaxWidth()
						)
						Row (
							horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
							modifier = modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))
						){
							OutlinedButton(
								onClick = discardNotesChanges,
								modifier = Modifier.weight(1f)
							) {
								Text(
									text = stringResource(R.string.back),
									fontSize = 14.sp
								)
							}
							Button(
								onClick = saveNotes,
								modifier = Modifier.weight(1f)
							) {
								Text(
									text = stringResource(R.string.save_notes),
									fontSize = 14.sp
								)
							}
						}
					} else
						CircularProgressIndicator(
							modifier = Modifier.size(30.dp)
						)
				}
			}
		}

	}
}

@Composable
fun ActionButtons(
	flightCard: () -> Bitmap,
	onBackPressed: () -> Unit,
	onSharePressed: (Bitmap) -> Unit,
	modifier: Modifier = Modifier
) {
	Row (
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
		modifier = modifier
	){
		OutlinedButton(
			onClick = onBackPressed,
			modifier = Modifier.weight(1f)
		) {
			Text(
				text = stringResource(R.string.back),
				fontSize = 16.sp
			)
		}
		Button(
			onClick = {
				onSharePressed(flightCard.invoke())
			},
			modifier = Modifier.weight(1f)
		) {
			Text(
				text = stringResource(R.string.share_flight),
				fontSize = 16.sp
			)
		}
	}
}

private fun shareFlight(context: Context, flightCardUri: Uri, departureCode: String, arrivalCode: String) {
	val intent = Intent(Intent.ACTION_SEND).apply {
		putExtra(Intent.EXTRA_TITLE, "Check my flight from $departureCode to $arrivalCode")
		putExtra(Intent.EXTRA_STREAM, flightCardUri)
		type = "image/png"
	}
	context.startActivity(
		Intent.createChooser(
			intent,
			context.getString(R.string.share_flight)
		)
	)
}

@Preview
@Composable
fun FlightDetailsPreview() {
	FlightDetails(
		uiState = FlightDetailsUIState(
			flight = Flight.getPlaceholderFlight1()
		)
	)
}

@Preview
@Composable
fun FlightNotesReadPreview() {
	FlightNotesComposable(
		flightNotes = FlightNotes(0, "My flight from Amsterdam to Malaga was a seamless journey that took just over three hours. The plane departed Schiphol Airport on time and landed smoothly at Malaga Airport under the warm Spanish sun. Throughout the flight, the cabin crew was attentive and friendly, ensuring a comfortable experience. The view from above as we approached the Mediterranean coast was breathtaking, with the clear blue sea and sprawling beaches serving as a perfect welcome to Malaga."),
		notesVisible = true,
		notesEdition = false
	)
}

@Preview
@Composable
fun FlightNotesEditPreview() {
	FlightNotesComposable(
		flightNotes = FlightNotes(0, "My flight from Amsterdam to Malaga was a seamless journey that took just over three hours. The plane departed Schiphol Airport on time and landed smoothly at Malaga Airport under the warm Spanish sun. Throughout the flight, the cabin crew was attentive and friendly, ensuring a comfortable experience. The view from above as we approached the Mediterranean coast was breathtaking, with the clear blue sea and sprawling beaches serving as a perfect welcome to Malaga."),
		notesVisible = true,
		notesEdition = true
	)
}