package com.taylorbros

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class BoidCars : ApplicationAdapter() {

    val boidCount = 300
    val maxSpeed = 5f
    val maxAcceleration = 1f
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
            var velocity = Vector2().setToRandomDirection().setLength(MathUtils.random() * maxSpeed)
            boids.add(Bird(position, velocity, localDistance, flockingPower, maxSpeed, maxAcceleration))
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