package com.finnvek.dropandhold.game.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.finnvek.dropandhold.game.DropAndHoldGame
import com.finnvek.dropandhold.game.config.HudConfig
import com.finnvek.dropandhold.game.state.GameState

/**
 * Scene2D HUD overlay drawn over the 3D scene.
 *
 * Layout (top of screen):
 * ┌─────────────────────────────────────┐
 * │ Score: 1,240   x2.4         ⏸     │
 * │ Objects: 8     Time: 0:47           │
 * └─────────────────────────────────────┘
 *
 * Centered overlays during pre-game:
 *  - CALIBRATING — "Hold steady..."
 *  - COUNTDOWN — "3 / 2 / 1"
 */
class GameHud : Disposable {
    private lateinit var stage: Stage
    private lateinit var font: BitmapFont
    private lateinit var bigFont: BitmapFont
    private lateinit var bgTexture: Texture

    private lateinit var scoreLabel: Label
    private lateinit var multiplierLabel: Label
    private lateinit var objectsLabel: Label
    private lateinit var timeLabel: Label
    private lateinit var statusLabel: Label

    fun init() {
        stage = Stage(ScreenViewport())
        font = BitmapFont().apply { data.setScale(HudConfig.FONT_SCALE) }
        bigFont = BitmapFont().apply { data.setScale(HudConfig.BIG_FONT_SCALE) }

        bgTexture = makeFlatTexture(HudConfig.HUD_BACKGROUND)

        val scoreStyle = Label.LabelStyle(font, HudConfig.TEXT_PRIMARY)
        val accentStyle = Label.LabelStyle(font, HudConfig.TEXT_ACCENT)
        val secondaryStyle = Label.LabelStyle(font, HudConfig.TEXT_SECONDARY)
        val statusStyle = Label.LabelStyle(bigFont, HudConfig.TEXT_PRIMARY)

        scoreLabel = Label("0", scoreStyle)
        multiplierLabel = Label("x1.0", accentStyle)
        objectsLabel = Label("Objects: 0", secondaryStyle)
        timeLabel = Label("0:00", secondaryStyle)
        statusLabel =
            Label("", statusStyle).apply {
                setAlignment(Align.center)
            }

        val topRow =
            Table().apply {
                add(scoreLabel).left().padRight(HudConfig.LABEL_PAD).expandX()
                add(multiplierLabel).right().padRight(HudConfig.EDGE_PAD)
            }
        val bottomRow =
            Table().apply {
                add(objectsLabel).left().padRight(HudConfig.LABEL_PAD).expandX()
                add(timeLabel).right().padRight(HudConfig.EDGE_PAD)
            }
        val rootHud =
            Table().apply {
                setFillParent(true)
                top()
                pad(HudConfig.EDGE_PAD)
                add(topRow).growX().row()
                add(bottomRow).growX().padTop(HudConfig.ROW_SPACING).row()
            }
        val overlayRoot =
            Table().apply {
                setFillParent(true)
                center()
                add(statusLabel)
            }
        stage.addActor(rootHud)
        stage.addActor(overlayRoot)
    }

    fun resize(
        width: Int,
        height: Int,
    ) {
        stage.viewport.update(width, height, true)
    }

    fun render(game: DropAndHoldGame) {
        when (game.stateMachine.currentState) {
            GameState.CALIBRATING -> {
                statusLabel.setText(HudConfig.STATUS_CALIBRATING)
            }

            GameState.COUNTDOWN -> {
                statusLabel.setText(HudConfig.STATUS_COUNTDOWN)
            }

            GameState.PLAYING -> {
                statusLabel.setText("")
                scoreLabel.setText("Score: ${game.score.toInt()}")
                multiplierLabel.setText("x%.1f".format(game.multiplier))
                objectsLabel.setText("Objects: ${game.physicsWorld.activeObjectCount}")
                timeLabel.setText(formatTime(game.elapsedTime))
            }

            GameState.PAUSED -> {
                statusLabel.setText(HudConfig.STATUS_PAUSED)
            }

            GameState.GAME_OVER -> {
                statusLabel.setText(HudConfig.STATUS_GAME_OVER)
            }

            GameState.INITIALIZING -> {
                statusLabel.setText("")
            }
        }

        stage.act(Gdx.graphics.deltaTime.coerceAtMost(HudConfig.MAX_DELTA))
        stage.draw()
    }

    private fun formatTime(seconds: Float): String {
        val total = seconds.toInt()
        val m = total / 60
        val s = total % 60
        return "%d:%02d".format(m, s)
    }

    private fun makeFlatTexture(color: Color): Texture {
        val pixmap =
            Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                setColor(color)
                fill()
            }
        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

    override fun dispose() {
        stage.dispose()
        font.dispose()
        bigFont.dispose()
        bgTexture.dispose()
    }
}
