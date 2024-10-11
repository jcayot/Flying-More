package com.cayot.planou.ui.flightList

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cayot.planou.R
import com.cayot.planou.data.FlightMapState
import com.cayot.planou.data.flight.Flight
import com.cayot.planou.data.flight.getDepartureDateString
import com.cayot.planou.data.flight.getDistanceString
import com.cayot.planou.ui.AppViewModelProvider
import com.cayot.planou.ui.PlanouTopBar
import com.cayot.planou.ui.composable.FlightMap
import com.cayot.planou.ui.navigation.PlanouScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightListScreen(
	onFlightPressed: (Int) -> Unit,
	onAddFlightPressed: () -> Unit,
	modifier: Modifier = Modifier
) {
	val viewModel: FlightListViewModel = viewModel(factory = AppViewModelProvider.factory)
	val uiState by viewModel.uiState.collectAsState()
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold (
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
		topBar = {
			PlanouTopBar(
				title = stringResource(PlanouScreen.List.title),
				canNavigateBack = false,
				actions = {
					IconButton(
						onClick = onAddFlightPressed
					) {
						Icon(
							imageVector = Icons.Filled.Add,
							contentDescription = Icons.Filled.Add.name
						)
					}
				}
			)
		}
	) { innerPadding ->
		FlightListScreenContent(
			flightItemList = uiState.flightItemList,
			listState = uiState.listState,
			onFlightPressed = onFlightPressed,
			onItemVisible = viewModel::updateFlightMap,
			modifier = modifier.fillMaxSize(),
			contentPadding = innerPadding
		)
	}
}

@Composable
fun FlightListScreenContent(
	flightItemList: List<FlightItem>,
	listState: LazyListState?,
	onFlightPressed: (Int) -> Unit,
	onItemVisible: (FlightItem) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp)
) {
	if (flightItemList.isEmpty() || listState == null) {
		EmptyFlightListPlaceholder(
			modifier = Modifier
				.padding(contentPadding)
				.fillMaxSize()
		)
	} else {
		FlightListList(
			flightItemList = flightItemList,
			listState = listState,
			onFlightPressed = onFlightPressed,
			onItemVisible = onItemVisible,
			modifier = modifier,
			contentPadding = contentPadding
		)
	}
}

@Composable
fun EmptyFlightListPlaceholder(
	modifier: Modifier = Modifier,
) {
	Column (
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier,
	) {
		Image(
			painter = painterResource(R.drawable.airplanemode_inactive_40px),
			contentDescription = stringResource(R.string.list_empty_sad_plane),
			modifier = Modifier
				.padding(top = 80.dp)
				.size(100.dp)
		)
		Text(
			text = stringResource(R.string.flight_list_empty),
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.headlineSmall,
			modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
		)
	}
}

@Composable
fun FlightListList(
	flightItemList: List<FlightItem>,
	listState: LazyListState,
	onFlightPressed: (Int) -> Unit,
	onItemVisible: (FlightItem) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp)
) {
	LazyColumn (
		state = listState,
		contentPadding = contentPadding,
		verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_smadium)),
		modifier = modifier.padding(dimensionResource(id = R.dimen.padding_smadium)),
	) {
		items(flightItemList) { flightItem: FlightItem ->
			onItemVisible(flightItem)
			FlightListItem(
				flightItem = flightItem,
				onFlightPressed = onFlightPressed
			)
		}
	}
}

@Composable
fun FlightListItem(
	flightItem : FlightItem,
	onFlightPressed: (Int) -> Unit,
	modifier: Modifier = Modifier
) {
	Card(elevation = CardDefaults.cardElevation(),
		shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
		onClick = { onFlightPressed(flightItem.flight.id) },
		modifier = modifier
	) {
		Row (modifier = Modifier
			.fillMaxWidth()
			.size(dimensionResource(R.dimen.flight_item_height))
			.padding(dimensionResource(R.dimen.padding_small))
		) {
			FlightItemMap(
				flightMapState = flightItem.flightMapState,
				modifier = Modifier.size(dimensionResource(R.dimen.flight_item_height))
			)
			Column (
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
				modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_smadium))
			) {
				Row (
					modifier = Modifier.fillMaxWidth()
				) {
					Badge(containerColor = colorResource(R.color.flight_distance_green),
						contentColor = Color.White) {
						Text(
							text = "+ " + flightItem.flight.getDistanceString(),
							style = MaterialTheme.typography.titleMedium,
						)
					}
					Spacer(
						Modifier.weight(1f)
					)
					Text(
						text = flightItem.flight.getDepartureDateString()
					)
				}
				Text(
					text = flightItem.flight.originAirportCode + " - " + flightItem.flight.destinationAirportCode,
					style = MaterialTheme.typography.titleLarge,
				)
				Spacer(
					Modifier.weight(1f)
				)
				Row {
					Text(
						text = flightItem.flight.airline,
						style = MaterialTheme.typography.labelLarge
					)
					Spacer(
						Modifier.weight(1f)
					)
					Text(
						text = flightItem.flight.flightNumber,
						style = MaterialTheme.typography.labelLarge
					)
				}
			}
		}
	}
}

@Composable
fun FlightItemMap(
	flightMapState: FlightMapState?,
	modifier: Modifier = Modifier
) {
	Box(modifier = modifier) {
		if (flightMapState != null) {
			FlightMap(
				flightMapState = flightMapState,
			)
		} else {
			Image(
				painter = painterResource(R.drawable.ic_map_placeholder),
				contentDescription = stringResource(R.string.list_item_map_image),
				alignment = Alignment.Center,
				contentScale = ContentScale.FillWidth,
				modifier = Modifier
					.fillMaxSize()
			)
		}
	}
}

@Preview (showBackground = true)
@Composable
fun	FlightListScreenContentPreview() {
	FlightListScreenContent(
		flightItemList = listOf(
			FlightItem(Flight.getPlaceholderFlight())
		),
		listState = rememberLazyListState(),
		onFlightPressed = {},
		onItemVisible = {}
	)
}

@Preview (showBackground = true)
@Composable
fun EmptyFlightListPreview() {
	EmptyFlightListPlaceholder()
}

@Preview
@Composable
fun FlightListItemPreview() {
	FlightListItem(
		flightItem = FlightItem(Flight.getPlaceholderFlight()),
		onFlightPressed = {},
	)
}