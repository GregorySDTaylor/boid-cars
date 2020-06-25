package com.taylorbros

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class BoidCars : ApplicationAdapter() {

    val boidCount = 300
    val maxSpeed = 3f
    val maxAcceleration = 0.5f
    val localDistance = 100f
    val flockingPower = 1f

    var shapeRenderer: ShapeRenderer? = null
    val boids = mutableSetOf<Boid>()


    override fun create() {
        shapeRenderer = ShapeRenderer()
        shapeRenderer!!.setAutoShapeType(true)
        repeat(boidCount) {
            var position = Vector2(
                    MathUtils.random() * Gdx.graphics.width,
                    MathUtils.random() * Gdx.graphics.height
            )
            val variableFlockingPower = (MathUtils.random() * flockingPower * 2 + 0.5 * flockingPower).toFloat()
            val variableMaxSpeed = (MathUtils.random() * maxSpeed * 2 + 0.5 * maxSpeed).toFloat()
            val variableMaxAcceleration = (MathUtils.random() * maxAcceleration * 2 + 0.5 * maxAcceleration).toFloat()
            val velocity = Vector2().setToRandomDirection().setLength(MathUtils.random() * variableMaxSpeed)
            boids.add(Bird(position, velocity, localDistance, variableFlockingPower, variableMaxSpeed, variableMaxAcceleration))
        }
    }

    override fun render() {
        boids.forEach {
            val otherLocalBoids = findOtherLocalBoids(it)
            it.update(otherLocalBoids) }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer!!.begin();
        boids.forEach { it.render(shapeRenderer!!) }
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