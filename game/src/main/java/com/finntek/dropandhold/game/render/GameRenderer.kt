package com.finntek.dropandhold.game.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Disposable
import com.finntek.dropandhold.game.config.PhysicsConfig
import com.finntek.dropandhold.game.config.RenderConfig
import com.finntek.dropandhold.game.physics.PhysicsObject
import com.finntek.dropandhold.game.physics.PhysicsWorld

/**
 * Minimal 3D renderer for physics visualization.
 * Will be replaced by gdx-gltf PBR renderer later.
 */
class GameRenderer : Disposable {
    private lateinit var camera: PerspectiveCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment
    private lateinit var modelBuilder: ModelBuilder

    private lateinit var platformModel: Model
    private lateinit var platformInstance: ModelInstance

    private val objectInstances = mutableMapOf<PhysicsObject, ModelInstance>()
    private val objectModels = mutableListOf<Model>()

    private lateinit var platformMaterial: Material
    private lateinit var woodMaterial: Material
    private lateinit var metalMaterial: Material
    private lateinit var stoneMaterial: Material

    fun init(physicsWorld: PhysicsWorld) {
        modelBatch = ModelBatch()
        modelBuilder = ModelBuilder()
        setupCamera()
        setupEnvironment()
        setupMaterials()
        createPlatformVisual()
    }

    private fun setupCamera() {
        camera = PerspectiveCamera(RenderConfig.CAMERA_FOV, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(RenderConfig.CAMERA_X, RenderConfig.CAMERA_Y, RenderConfig.CAMERA_Z)
        camera.lookAt(RenderConfig.CAMERA_LOOK_X, RenderConfig.CAMERA_LOOK_Y, RenderConfig.CAMERA_LOOK_Z)
        camera.near = RenderConfig.CAMERA_NEAR
        camera.far = RenderConfig.CAMERA_FAR
        camera.update()
    }

    private fun setupEnvironment() {
        environment =
            Environment().apply {
                set(ColorAttribute(ColorAttribute.AmbientLight, RenderConfig.AMBIENT_COLOR))
                add(
                    DirectionalLight().set(
                        RenderConfig.MainLight.R,
                        RenderConfig.MainLight.G,
                        RenderConfig.MainLight.B,
                        RenderConfig.MainLight.DIR_X,
                        RenderConfig.MainLight.DIR_Y,
                        RenderConfig.MainLight.DIR_Z,
                    ),
                )
                add(
                    DirectionalLight().set(
                        RenderConfig.FillLight.R,
                        RenderConfig.FillLight.G,
                        RenderConfig.FillLight.B,
                        RenderConfig.FillLight.DIR_X,
                        RenderConfig.FillLight.DIR_Y,
                        RenderConfig.FillLight.DIR_Z,
                    ),
                )
            }
    }

    private fun setupMaterials() {
        platformMaterial = Material(ColorAttribute.createDiffuse(RenderConfig.PLATFORM_COLOR))
        woodMaterial = Material(ColorAttribute.createDiffuse(RenderConfig.WOOD_COLOR))
        metalMaterial = Material(ColorAttribute.createDiffuse(RenderConfig.METAL_COLOR))
        stoneMaterial = Material(ColorAttribute.createDiffuse(RenderConfig.STONE_COLOR))
    }

    private fun createPlatformVisual() {
        val attrs = (Usage.Position or Usage.Normal).toLong()
        val d = PhysicsConfig.PLATFORM_RADIUS * 2f
        val h = PhysicsConfig.PLATFORM_HALF_HEIGHT * 2f
        platformModel = modelBuilder.createCylinder(d, h, d, RenderConfig.PLATFORM_SEGMENTS, platformMaterial, attrs)
        platformInstance = ModelInstance(platformModel)
    }

    fun addObjectVisual(
        obj: PhysicsObject,
        shapeType: ObjectShapeType,
    ) {
        val attrs = (Usage.Position or Usage.Normal).toLong()
        val material =
            when {
                shapeType.name.contains("WOOD") -> woodMaterial
                shapeType.name.contains("METAL") -> metalMaterial
                else -> stoneMaterial
            }
        val size = shapeType.visualSize
        val model =
            if (shapeType.name.contains("SPHERE")) {
                modelBuilder.createSphere(size, size, size, RenderConfig.SPHERE_DIVISIONS, RenderConfig.SPHERE_DIVISIONS, material, attrs)
            } else {
                modelBuilder.createBox(size, size, size, material, attrs)
            }
        objectModels.add(model)
        objectInstances[obj] = ModelInstance(model)
    }

    fun removeObjectVisual(obj: PhysicsObject) {
        objectInstances.remove(obj)
    }

    fun render(physicsWorld: PhysicsWorld) {
        val sky = RenderConfig.SKY_COLOR
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(sky.r, sky.g, sky.b, sky.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        platformInstance.transform.set(physicsWorld.platformBody.worldTransform)
        for ((obj, instance) in objectInstances) {
            instance.transform.set(obj.motionState.transform)
        }

        camera.update()
        modelBatch.begin(camera)
        modelBatch.render(platformInstance, environment)
        for ((_, instance) in objectInstances) {
            modelBatch.render(instance, environment)
        }
        modelBatch.end()
    }

    override fun dispose() {
        modelBatch.dispose()
        platformModel.dispose()
        objectModels.forEach { it.dispose() }
        objectModels.clear()
        objectInstances.clear()
    }
}

enum class ObjectShapeType(
    val visualSize: Float,
) {
    WOOD_SPHERE(RenderConfig.WOOD_SPHERE_SIZE),
    WOOD_BOX(RenderConfig.WOOD_BOX_SIZE),
    METAL_SPHERE(RenderConfig.METAL_SPHERE_SIZE),
    METAL_BOX(RenderConfig.METAL_BOX_SIZE),
    STONE_SPHERE(RenderConfig.STONE_SPHERE_SIZE),
    STONE_BOX(RenderConfig.STONE_BOX_SIZE),
}
