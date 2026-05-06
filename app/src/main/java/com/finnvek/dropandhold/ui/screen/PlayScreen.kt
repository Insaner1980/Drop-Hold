package com.finnvek.dropandhold.ui.screen

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finnvek.dropandhold.R
import com.finnvek.dropandhold.game.GameActivity
import com.finnvek.dropandhold.game.GameMode
import com.finnvek.dropandhold.ui.theme.Dimens

@Composable
fun PlayScreen(viewModel: PlayViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                GameActivity.parseResult(result.data)?.let(viewModel::onGameFinished)
            }
        }

    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(Dimens.ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(Dimens.SectionSpacing))

        StatsCard(
            bestScore = uiState.bestScore,
            bestSimultaneous = uiState.bestSimultaneous,
            totalRuns = uiState.totalRuns,
        )

        Spacer(Modifier.height(Dimens.SectionSpacing))

        Button(
            onClick = { launcher.launch(GameActivity.createIntent(context, GameMode.CLASSIC)) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
        ) {
            Text(stringResource(R.string.play_classic), style = MaterialTheme.typography.titleMedium)
        }
    }

    uiState.lastResult?.let { result ->
        GameOverDialog(
            result = result,
            onPlayAgain = {
                viewModel.dismissResult()
                launcher.launch(GameActivity.createIntent(context, result.mode))
            },
            onDismiss = viewModel::dismissResult,
        )
    }
}

@Composable
private fun StatsCard(
    bestScore: Int,
    bestSimultaneous: Int,
    totalRuns: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
        ) {
            StatRow(stringResource(R.string.best_score), bestScore.toString())
            StatRow(stringResource(R.string.objects_record), bestSimultaneous.toString())
            StatRow("Runs", totalRuns.toString())
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

