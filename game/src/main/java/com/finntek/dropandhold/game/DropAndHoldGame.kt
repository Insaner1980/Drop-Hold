package com.finntek.dropandhold.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.finntek.dropandhold.game.config.GameplayConfig
import com.finntek.dropandhold.game.input.SensorInputManager
import com.finntek.dropandhold.game.physics.PhysicsWorld
import com.finntek.dropandhold.game.physics.PlatformController
import com.finntek.dropandhold.game.render.GameRenderer
import com.finntek.dropandhold.game.spawn.ObjectSpawner
import com.finntek.dropandhold.game.state.GameState
import com.finntek.dropandhold.game.state.GameStateMachine

class DropAndHoldGame(
    internal val sensorInput: SensorInputManager,
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

    var score: Float = 0f
        private set
    var multiplier: Float = 1f
        private set
    var elapsedTime: Float = 0f
        private set
    var objectsLost: Int = 0
        private set
    private var timeSinceLastLoss: Float = 0f

    override fun create() {
        physicsWorld = PhysicsWorld().also { it.init() }
        renderer = GameRenderer().also { it.init(physicsWorld) }
        platformController = PlatformController(physicsWorld, sensorInput)
        platformController.setSensitivity(GameplayConfig.DEFAULT_SENSITIVITY)
        objectSpawner = ObjectSpawner(physicsWorld, renderer)
        stateMachine = GameStateMachine(this)
        stateMachine.transition(GameState.CALIBRATING)
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime.coerceAtMost(GameplayConfig.MAX_DELTA_TIME)
        stateMachine.update(delta)
        renderer.render(physicsWorld)
    }

    fun updateScoring(delta: Float) {
        elapsedTime += delta
        timeSinceLastLoss += delta

        val activeObjects = physicsWorld.activeObjectCount
        if (activeObjects > 0) {
            val pointsPerSecond = activeObjects * (1f + activeObjects * GameplayConfig.POINTS_PER_OBJECT_FACTOR)
            score += pointsPerSecond * multiplier * delta
        }

        if (timeSinceLastLoss >= GameplayConfig.MULTIPLIER_GROWTH_INTERVAL) {
            multiplier += GameplayConfig.MULTIPLIER_GROWTH
            timeSinceLastLoss -= GameplayConfig.MULTIPLIER_GROWTH_INTERVAL
        }
    }

    fun onObjectsLost(count: Int) {
        objectsLost += count
        multiplier =
            (multiplier - GameplayConfig.MULTIPLIER_LOSS_PENALTY * count)
                .coerceAtLeast(GameplayConfig.MULTIPLIER_MINIMUM)
        timeSinceLastLoss = 0f
    }

    fun resetGameState() {
        score = 0f
        multiplier = 1f
        elapsedTime = 0f
        objectsLost = 0
        timeSinceLastLoss = 0f
        physicsWorld.reset()
        objectSpawner.reset()
    }

    override fun dispose() {
        renderer.dispose()
        physicsWorld.dispose()
    }
}
