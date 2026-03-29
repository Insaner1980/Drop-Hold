package com.finntek.dropandhold.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.finntek.dropandhold.R
import com.finntek.dropandhold.ui.theme.Dimens

@Composable
fun PlayScreen() {
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

        Button(
            onClick = { /* Game launch will be connected in gameplay implementation phase */ },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SectionSpacing),
        ) {
            Text(stringResource(R.string.play_classic))
        }
    }
}
