package com.finnvek.dropandhold.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finnvek.dropandhold.R
import com.finnvek.dropandhold.game.model.GameResult
import com.finnvek.dropandhold.ui.theme.Dimens

/**
 * Dialog shown after a run completes. Displays score, time, objects, shards earned,
 * and provides Play Again / Home actions. Highlighted in gold when isNewBest is true.
 *
 * Uses AlertDialog for v1; replace with ModalBottomSheet for v2 polish phase.
 */
@Composable
fun GameOverDialog(
    result: GameResult,
    onPlayAgain: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (result.isNewBest) stringResource(R.string.new_best) else stringResource(R.string.game_over),
                style = MaterialTheme.typography.headlineMedium,
                color = if (result.isNewBest) Gold else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
            ) {
                ResultRow(stringResource(R.string.label_score), result.score.toString())
                ResultRow("Best", result.bestScore.toString())
                ResultRow(stringResource(R.string.label_time), formatDuration(result.timeSeconds))
                ResultRow(stringResource(R.string.label_objects), result.objectsBalanced.toString())
                ResultRow("Best Chain", "${result.bestChainSeconds.toInt()}s")

                if (result.shardsEarned > 0) {
                    Spacer(Modifier.height(Dimens.ItemSpacing))
                    Text(
                        text = stringResource(R.string.shards_earned, result.shardsEarned),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight),
            ) {
                Text(stringResource(R.string.play_again))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.go_home))
            }
        },
    )
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
    }
}

private fun formatDuration(seconds: Float): String {
    val total = seconds.toInt()
    val m = total / 60
    val s = total % 60
    return "%d:%02d".format(m, s)
}

private val Gold = Color(0xFFE4B12B)
