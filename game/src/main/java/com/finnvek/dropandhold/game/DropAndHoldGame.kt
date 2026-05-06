package com.finnvek.dropandhold.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.finnvek.dropandhold.game.config.GameplayConfig
import com.finnvek.dropandhold.game.hud.GameHud
import com.finnvek.dropandhold.game.input.SensorInputManager
import com.finnvek.dropandhold.game.model.GameResult
import com.finnvek.dropandhold.game.physics.PhysicsWorld
import com.finnvek.dropandhold.game.physics.PlatformController
import com.finnvek.dropandhold.game.render.GameRenderer
import com.finnvek.dropandhold.game.spawn.ObjectSpawner
import com.finnvek.dropandhold.game.state.GameState
import com.finnvek.dropandhold.game.state.GameStateMachine

class DropAndHoldGame(
    internal val sensorInput: SensorInputManager,
    private val mode: String = GameMode.CLASSIC,
) : ApplicationAdapter() {
    lateinit var physicsWorld: PhysicsWorld
        private set
    lateinit var renderer: GameRenderer
        private set
    lateinit var platformController: PlatformController
        private set
    lateinit var objectSpawner: ObjectSpawner
        private set
    lateinit var stateMachine: GameStateMachine
        private set
    lateinit var hud: GameHud
        private set

    var score: Float = 0f
        private set
    var multiplier: Float = 1f
        private set
    var elapsedTime: Float = 0f
        private set
    var objectsLost: Int = 0
        private set
    var objectsSpawned: Int = 0
        private set
    var maxSimultaneous: Int = 0
        private set
    var bestChainSeconds: Float = 0f
        private set
    private var timeSinceLastLoss: Float = 0f
    private var currentChainStart: Float = 0f

    /** Invoked once when state transitions to GAME_OVER. Activity uses this to finish with result. */
    var onGameEnded: ((GameResult) -> Unit)? = null

    private var resultEmitted: Boolean = false

    override fun create() {
        physicsWorld = PhysicsWorld().also { it.init() }
        renderer = GameRenderer().also { it.init(physicsWorld) }
        platformController = PlatformController(physicsWorld, sensorInput)
        platformController.setSensitivity(GameplayConfig.DEFAULT_SENSITIVITY)
        objectSpawner = ObjectSpawner(physicsWorld, renderer, onSpawn = ::onObjectSpawned)
        stateMachine = GameStateMachine(this)
        hud = GameHud().also { it.init() }
        stateMachine.transition(GameState.CALIBRATING)
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime.coerceAtMost(GameplayConfig.MAX_DELTA_TIME)
        stateMachine.update(delta)
        renderer.render(physicsWorld)
        hud.render(this)
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        super.resize(width, height)
        renderer.resize(width, height)
        hud.resize(width, height)
    }

    fun updateScoring(delta: Float) {
        elapsedTime += delta
        timeSinceLastLoss += delta

        val activeObjects = physicsWorld.activeObjectCount
        if (activeObjects > maxSimultaneous) maxSimultaneous = activeObjects

        if (activeObjects > 0) {
            val pointsPerSecond = activeObjects * (1f + activeObjects * GameplayConfig.POINTS_PER_OBJECT_FACTOR)
            score += pointsPerSecond * multiplier * delta
        }

        if (timeSinceLastLoss >= GameplayConfig.MULTIPLIER_GROWTH_INTERVAL) {
            multiplier += GameplayConfig.MULTIPLIER_GROWTH
            timeSinceLastLoss -= GameplayConfig.MULTIPLIER_GROWTH_INTERVAL
        }

        val chain = elapsedTime - currentChainStart
        if (chain > bestChainSeconds) bestChainSeconds = chain
    }

    fun onObjectsLost(count: Int) {
        objectsLost += count
        multiplier =
            (multiplier - GameplayConfig.MULTIPLIER_LOSS_PENALTY * count)
                .coerceAtLeast(GameplayConfig.MULTIPLIER_MINIMUM)
        timeSinceLastLoss = 0f
        currentChainStart = elapsedTime
    }

    fun onObjectSpawned() {
        objectsSpawned++
    }

    fun resetGameState() {
        score = 0f
        multiplier = 1f
        elapsedTime = 0f
        objectsLost = 0
        objectsSpawned = 0
        maxSimultaneous = 0
        bestChainSeconds = 0f
        timeSinceLastLoss = 0f
        currentChainStart = 0f
        resultEmitted = false
        physicsWorld.reset()
        objectSpawner.reset()
    }

    /**
     * Called by GameStateMachine when entering GAME_OVER. Builds final result and
     * dispatches to listener (the GameActivity) which finishes with ActivityResult.
     */
    fun emitGameResult() {
        if (resultEmitted) return
        resultEmitted = true

        val shards = (score / GameplayConfig.SHARDS_DIVISOR).toInt().coerceAtLeast(0)
        val result =
            GameResult(
                score = score.toInt(),
                bestScore = 0,
                timeSeconds = elapsedTime,
                objectsBalanced = objectsSpawned,
                maxSimultaneous = maxSimultaneous,
                bestChainSeconds = bestChainSeconds,
                shardsEarned = shards,
                isNewBest = false,
                mode = mode,
                achievementsUnlocked = emptyList(),
            )
        onGameEnded?.invoke(result)
    }

    override fun dispose() {
        hud.dispose()
        renderer.dispose()
        physicsWorld.dispose()
    }
}
