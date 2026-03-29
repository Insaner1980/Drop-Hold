package com.finntek.dropandhold.game.spawn

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.finntek.dropandhold.game.config.GameplayConfig
import com.finntek.dropandhold.game.config.PhysicsConfig
import com.finntek.dropandhold.game.physics.PhysicsObject
import com.finntek.dropandhold.game.physics.PhysicsWorld
import com.finntek.dropandhold.game.render.GameRenderer
import com.finntek.dropandhold.game.render.ObjectShapeType

/**
 * Spawns objects on a timer with increasing frequency and weight.
 */
class ObjectSpawner(
    private val physicsWorld: PhysicsWorld,
    private val renderer: GameRenderer,
) {
    private var timeSinceLastDrop = 0f
    private var dropInterval = GameplayConfig.INITIAL_DROP_INTERVAL
    private var elapsedTime = 0f
    private val spawnedObjects = mutableListOf<PhysicsObject>()

    val objectCount: Int get() = spawnedObjects.size

    fun update(delta: Float): SpawnEvent? {
        elapsedTime += delta
        timeSinceLastDrop += delta

        dropInterval =
            (
                GameplayConfig.INITIAL_DROP_INTERVAL -
                    (elapsedTime / GameplayConfig.DROP_REDUCTION_EVERY_SECONDS).toInt() * GameplayConfig.DROP_INTERVAL_REDUCTION
            ).coerceAtLeast(GameplayConfig.MIN_DROP_INTERVAL)

        if (timeSinceLastDrop >= dropInterval) {
            timeSinceLastDrop = 0f
            return spawnRandomObject()
        }
        return null
    }

    private fun spawnRandomObject(): SpawnEvent {
        val types = ObjectShapeType.entries
        val type = types[(Math.random() * types.size).toInt()]

        val (mass, friction, restitution) =
            when {
                type.name.contains("WOOD") -> Triple(PhysicsConfig.Wood.MASS, PhysicsConfig.Wood.FRICTION, PhysicsConfig.Wood.RESTITUTION)

                type.name.contains(
                    "METAL",
                ) -> Triple(PhysicsConfig.Metal.MASS, PhysicsConfig.Metal.FRICTION, PhysicsConfig.Metal.RESTITUTION)

                else -> Triple(PhysicsConfig.Stone.MASS, PhysicsConfig.Stone.FRICTION, PhysicsConfig.Stone.RESTITUTION)
            }

        val weightMultiplier = 1f + (elapsedTime / PhysicsConfig.WEIGHT_RAMP_SECONDS).coerceAtMost(PhysicsConfig.WEIGHT_RAMP_MAX)

        val shape: btCollisionShape =
            if (type.name.contains("SPHERE")) {
                btSphereShape(type.visualSize / 2f)
            } else {
                val half = type.visualSize / 2f
                btBoxShape(Vector3(half, half, half))
            }

        val obj = physicsWorld.spawnObject(shape, mass * weightMultiplier, friction, restitution)
        renderer.addObjectVisual(obj, type)
        spawnedObjects.add(obj)
        return SpawnEvent(obj, type)
    }

    fun cleanupFallen(): Int {
        val fallenCount = physicsWorld.cleanupFallenObjects()
        if (fallenCount > 0) {
            val iter = spawnedObjects.iterator()
            while (iter.hasNext()) {
                val obj = iter.next()
                if (!obj.body.isInWorld) {
                    renderer.removeObjectVisual(obj)
                    iter.remove()
                }
            }
        }
        return fallenCount
    }

    fun reset() {
        spawnedObjects.clear()
        timeSinceLastDrop = 0f
        dropInterval = GameplayConfig.INITIAL_DROP_INTERVAL
        elapsedTime = 0f
    }
}

data class SpawnEvent(
    val obj: PhysicsObject,
    val type: ObjectShapeType,
)
