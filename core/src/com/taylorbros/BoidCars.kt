package com.taylorbros

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class BoidCars : ApplicationAdapter() {

    var shapeRenderer: ShapeRenderer? = null

    val boidCount = 300
    val boidSize = 5f
    val maxSpeed = 3f
    val maxAcceleration = 0.5f
    val localDistance = 100f
    val flockingPower = 1f
    val boids = mutableSetOf<Boid>()

    val obstacleCount = 3
    val minObstacleSize = 50f
    val maxObstacleSize = 200f

    val entities = mutableSetOf<Any>()

    override fun create() {
        shapeRenderer = ShapeRenderer()
        shapeRenderer!!.setAutoShapeType(true)
        repeat(obstacleCount) {
            var position = Vector2(
                    MathUtils.random() * Gdx.graphics.width,
                    MathUtils.random() * Gdx.graphics.height
            )
            val variableSize = MathUtils.random() * (maxObstacleSize - minObstacleSize) + minObstacleSize
            val obstacle = Obstacle(position, variableSize)
            entities.add(obstacle)
        }
        repeat(boidCount) {
            var position = Vector2(
                    MathUtils.random() * Gdx.graphics.width,
                    MathUtils.random() * Gdx.graphics.height
            )
            val variableFlockingPower = (MathUtils.random() * flockingPower * 2 + 0.5 * flockingPower).toFloat()
            val variableMaxSpeed = (MathUtils.random() * maxSpeed * 2 + 0.5 * maxSpeed).toFloat()
            val variableMaxAcceleration = (MathUtils.random() * maxAcceleration * 2 + 0.5 * maxAcceleration).toFloat()
            val velocity = Vector2().setToRandomDirection().setLength(MathUtils.random() * variableMaxSpeed)
            val bird = Bird(
                    boidSize,
                    position,
                    velocity,
                    localDistance,
                    variableFlockingPower,
                    variableMaxSpeed,
                    variableMaxAcceleration
            )
            boids.add(bird)
            entities.add(bird)
        }
    }

    override fun render() {
        entities.forEach { if (it is Updateable) it.update(entities) }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer!!.begin();
        entities.forEach { if (it is ShapeRenderable) it.shapeRender(shapeRenderer!!) }
        shapeRenderer!!.end();
    }

    fun findOtherLocalBoids(principal : Boid) : Set<Boid> {
        val otherLocalBoids = mutableSetOf<Boid>()
        boids.forEach { target ->
            if (principal != target && principal.position.dst(target.position) < localDistance) {
                otherLocalBoids.add(target)
            }
        }
        return otherLocalBoids
    }

    override fun dispose() {
        shapeRenderer!!.dispose()
    }
}