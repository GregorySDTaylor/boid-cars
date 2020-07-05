package com.taylorbros

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.box2d.createWorld

class BoidCars : ApplicationAdapter() {

    private var shapeRenderer: ShapeRenderer? = null

    private val pixelsPerMeter = 50f
    private var stageWidth: Float = 1f
    private var stageHeight: Float = 1f
    var timeStep = 1.0f / 60.0f // TODO figure out relationship between frame rate and physics simulation rate
    var velocityIterations = 8
    var positionIterations = 3

    private val world = createWorld()

    private val boidCount = 80
    private val boidSize = 0.1f
    private val boidDensity = 10f
    private val maxSpeed = 2f
    private val maxAcceleration = 5f
    private val localDistance = 1f
    private val flockingPower = 10f
    private val boids = mutableSetOf<Boid>()

    private val obstacleCount = 3
    private val minObstacleSize = 0.2f
    private val maxObstacleSize = 5f

    private val entities = mutableSetOf<Any>()

    override fun create() {
        shapeRenderer = ShapeRenderer()
        shapeRenderer!!.setAutoShapeType(true)

        stageWidth = Gdx.graphics.width / pixelsPerMeter
        stageHeight = Gdx.graphics.height / pixelsPerMeter

        repeat(obstacleCount) {
            val position = Vector2(
                    MathUtils.random() * stageWidth,
                    MathUtils.random() * stageHeight
            )
            val variableSize = MathUtils.random() * (maxObstacleSize - minObstacleSize) + minObstacleSize
            val obstacle = Obstacle(position, variableSize)
            entities.add(obstacle)
        }
        repeat(boidCount) {
            val position = Vector2(
                    MathUtils.random() * stageWidth,
                    MathUtils.random() * stageHeight
            )
            val variableFlockingPower = (MathUtils.random() * flockingPower * 2 + 0.5 * flockingPower).toFloat()
            val variableMaxSpeed = (MathUtils.random() * maxSpeed * 2 + 0.5 * maxSpeed).toFloat()
            val variableMaxAcceleration = (MathUtils.random() * maxAcceleration * 2 + 0.5 * maxAcceleration).toFloat()
            val initialImpulse = Vector2().setToRandomDirection().setLength(MathUtils.random() * variableMaxSpeed)
            val bird = Bird(
                    boidSize,
                    boidDensity,
                    position,
                    initialImpulse,
                    localDistance,
                    variableFlockingPower,
                    variableMaxAcceleration,
                    world
            )
            boids.add(bird)
            entities.add(bird)
        }
    }

    override fun render() {
        world.step(timeStep, velocityIterations, positionIterations)
        entities.forEach { if (it is Updateable) it.update(entities) }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer!!.begin()
        entities.forEach { if (it is ShapeRenderable) it.shapeRender(shapeRenderer!!, pixelsPerMeter) }
        shapeRenderer!!.end()
    }

    override fun dispose() {
        world.dispose()
        shapeRenderer!!.dispose()
    }
}