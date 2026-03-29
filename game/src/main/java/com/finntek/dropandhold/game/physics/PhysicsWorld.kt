package com.finntek.dropandhold.game.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.*
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.badlogic.gdx.utils.Disposable
import com.finntek.dropandhold.game.config.GameplayConfig
import com.finntek.dropandhold.game.config.PhysicsConfig

/**
 * Encapsulates the Bullet physics simulation: dynamics world, platform body
 * with tilt constraint, object bodies, and flip/fall-off detection.
 */
class PhysicsWorld : Disposable {
    private lateinit var collisionConfig: btDefaultCollisionConfiguration
    private lateinit var dispatcher: btCollisionDispatcher
    private lateinit var broadphase: btDbvtBroadphase
    private lateinit var solver: btSequentialImpulseConstraintSolver
    lateinit var dynamicsWorld: btDiscreteDynamicsWorld
        private set

    lateinit var platformBody: btRigidBody
        private set
    private lateinit var platformShape: btCollisionShape
    private lateinit var platformMotionState: GameMotionState
    private lateinit var platformConstraint: btGeneric6DofSpring2Constraint

    private lateinit var groundBody: btRigidBody
    private lateinit var groundShape: btCollisionShape

    private lateinit var contactListener: PlatformContactListener

    private val objectBodies = mutableListOf<PhysicsObject>()
    private val bodiesToRemove = mutableListOf<PhysicsObject>()

    val platformTiltAngle: Float
        get() {
            val quat = platformBody.worldTransform.getRotation(tmpQuat)
            tmpVec.set(0f, 1f, 0f)
            quat.transform(tmpVec)
            val dot = tmpVec.dot(0f, 1f, 0f).toDouble().coerceIn(-1.0, 1.0)
            return Math.toDegrees(Math.acos(dot)).toFloat()
        }

    val activeObjectCount: Int get() = objectBodies.size

    private val tmpQuat = Quaternion()
    private val tmpVec = Vector3()

    fun init() {
        Bullet.init()

        collisionConfig = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfig)
        broadphase = btDbvtBroadphase()
        solver = btSequentialImpulseConstraintSolver()
        dynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig)
        dynamicsWorld.gravity = Vector3(0f, PhysicsConfig.GRAVITY, 0f)

        createPlatform()
        createGround()
        contactListener = PlatformContactListener()
    }

    private fun createPlatform() {
        val radius = PhysicsConfig.PLATFORM_RADIUS
        val halfHeight = PhysicsConfig.PLATFORM_HALF_HEIGHT
        platformShape = btCylinderShape(Vector3(radius, halfHeight, radius))

        val mass = PhysicsConfig.PLATFORM_MASS
        val localInertia = Vector3()
        platformShape.calculateLocalInertia(mass, localInertia)

        platformMotionState = GameMotionState()
        platformMotionState.transform.idt().translate(0f, PhysicsConfig.PLATFORM_Y, 0f)

        val ci = btRigidBody.btRigidBodyConstructionInfo(mass, platformMotionState, platformShape, localInertia)
        platformBody = btRigidBody(ci)
        platformBody.apply {
            friction = PhysicsConfig.PLATFORM_FRICTION
            restitution = PhysicsConfig.PLATFORM_RESTITUTION
            activationState = 4 // DISABLE_DEACTIVATION
            userValue = PhysicsConfig.PLATFORM_USER_VALUE
        }
        ci.dispose()
        dynamicsWorld.addRigidBody(platformBody)

        platformConstraint =
            btGeneric6DofSpring2Constraint(
                platformBody,
                Matrix4().idt().translate(0f, PhysicsConfig.PLATFORM_Y, 0f),
            ).apply {
                setLinearLowerLimit(Vector3.Zero)
                setLinearUpperLimit(Vector3.Zero)
                val maxTilt = Math.toRadians(PhysicsConfig.PLATFORM_MAX_CONSTRAINT_DEGREES.toDouble()).toFloat()
                setAngularLowerLimit(Vector3(-maxTilt, 0f, -maxTilt))
                setAngularUpperLimit(Vector3(maxTilt, 0f, maxTilt))
            }
        dynamicsWorld.addConstraint(platformConstraint, true)
    }

    private fun createGround() {
        val ext = PhysicsConfig.GROUND_HALF_EXTENT
        groundShape = btBoxShape(Vector3(ext, 1f, ext))

        val ci = btRigidBody.btRigidBodyConstructionInfo(0f, null, groundShape, Vector3.Zero)
        groundBody = btRigidBody(ci)
        groundBody.apply {
            worldTransform = Matrix4().idt().translate(0f, PhysicsConfig.GROUND_Y, 0f)
            userValue = PhysicsConfig.GROUND_USER_VALUE
        }
        ci.dispose()
        dynamicsWorld.addRigidBody(groundBody)
    }

    fun applyPlatformTorque(
        targetPitch: Float,
        targetRoll: Float,
        springK: Float,
        dampingD: Float,
    ) {
        val rotation = platformBody.worldTransform.getRotation(tmpQuat)

        val currentPitch =
            Math
                .toDegrees(
                    Math.atan2(
                        2.0 * (rotation.w * rotation.x + rotation.y * rotation.z),
                        1.0 - 2.0 * (rotation.x * rotation.x + rotation.y * rotation.y),
                    ),
                ).toFloat()

        val currentRoll =
            Math
                .toDegrees(
                    Math.atan2(
                        2.0 * (rotation.w * rotation.z + rotation.x * rotation.y),
                        1.0 - 2.0 * (rotation.y * rotation.y + rotation.z * rotation.z),
                    ),
                ).toFloat()

        val angVel = platformBody.angularVelocity
        val torqueX = (targetPitch - currentPitch) * springK - angVel.x * dampingD
        val torqueZ = (targetRoll - currentRoll) * springK - angVel.z * dampingD

        platformBody.applyTorque(Vector3(torqueX, 0f, torqueZ))
    }

    fun spawnObject(
        shape: btCollisionShape,
        mass: Float,
        friction: Float,
        restitution: Float,
    ): PhysicsObject {
        val localInertia = Vector3()
        shape.calculateLocalInertia(mass, localInertia)

        val motionState = GameMotionState()
        val offsetRange = PhysicsConfig.PLATFORM_RADIUS * PhysicsConfig.SPAWN_OFFSET_RANGE
        val xOff = (kotlin.random.Random.nextFloat() - 0.5f) * offsetRange
        val zOff = (kotlin.random.Random.nextFloat() - 0.5f) * offsetRange
        motionState.transform.idt().translate(xOff, PhysicsConfig.SPAWN_Y, zOff)

        val ci = btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia)
        val body = btRigidBody(ci)
        body.apply {
            this.friction = friction
            this.restitution = restitution
            activationState = 4
            userValue = nextObjectId++
        }
        ci.dispose()

        dynamicsWorld.addRigidBody(body)
        val obj = PhysicsObject(body, motionState, shape)
        objectBodies.add(obj)
        return obj
    }

    fun removeObject(obj: PhysicsObject) {
        if (objectBodies.remove(obj)) {
            dynamicsWorld.removeRigidBody(obj.body)
            obj.dispose()
        }
    }

    fun cleanupFallenObjects(): Int {
        bodiesToRemove.clear()
        for (obj in objectBodies) {
            val y =
                obj.motionState.transform
                    .getTranslation(tmpVec)
                    .y
            if (y < PhysicsConfig.FALL_THRESHOLD_Y) {
                bodiesToRemove.add(obj)
            }
        }
        for (obj in bodiesToRemove) {
            dynamicsWorld.removeRigidBody(obj.body)
            objectBodies.remove(obj)
            obj.dispose()
        }
        return bodiesToRemove.size
    }

    fun step(delta: Float) {
        dynamicsWorld.stepSimulation(delta, PhysicsConfig.MAX_SUBSTEPS, PhysicsConfig.FIXED_TIMESTEP)
    }

    fun isFlipped(thresholdDegrees: Float = GameplayConfig.FLIP_THRESHOLD_DEGREES): Boolean = platformTiltAngle > thresholdDegrees

    fun reset() {
        for (obj in objectBodies) {
            dynamicsWorld.removeRigidBody(obj.body)
            obj.dispose()
        }
        objectBodies.clear()
        nextObjectId = PhysicsConfig.FIRST_OBJECT_ID

        platformBody.apply {
            worldTransform = Matrix4().idt().translate(0f, PhysicsConfig.PLATFORM_Y, 0f)
            linearVelocity = Vector3.Zero
            angularVelocity = Vector3.Zero
            clearForces()
        }
        platformMotionState.transform.idt().translate(0f, PhysicsConfig.PLATFORM_Y, 0f)
    }

    override fun dispose() {
        for (obj in objectBodies) {
            dynamicsWorld.removeRigidBody(obj.body)
            obj.dispose()
        }
        objectBodies.clear()

        dynamicsWorld.removeConstraint(platformConstraint)
        dynamicsWorld.removeRigidBody(platformBody)
        dynamicsWorld.removeRigidBody(groundBody)

        platformConstraint.dispose()
        platformBody.dispose()
        platformMotionState.dispose()
        platformShape.dispose()
        groundBody.dispose()
        groundShape.dispose()

        dynamicsWorld.dispose()
        solver.dispose()
        broadphase.dispose()
        dispatcher.dispose()
        collisionConfig.dispose()
        contactListener.dispose()
    }

    companion object {
        private var nextObjectId = PhysicsConfig.FIRST_OBJECT_ID
    }
}

class PhysicsObject(
    val body: btRigidBody,
    val motionState: GameMotionState,
    private val shape: btCollisionShape,
) : Disposable {
    override fun dispose() {
        body.dispose()
        motionState.dispose()
        shape.dispose()
    }
}

class GameMotionState : btMotionState() {
    val transform = Matrix4().idt()

    override fun getWorldTransform(worldTrans: Matrix4) {
        worldTrans.set(transform)
    }

    override fun setWorldTransform(worldTrans: Matrix4) {
        transform.set(worldTrans)
    }
}

private class PlatformContactListener : ContactListener()
