package com.taylorbros

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class BoidCars : ApplicationAdapter() {

    val boidCount = 200
    val maxSpeed = 10f

    var shapeRenderer: ShapeRenderer? = null
    val boids = mutableSetOf<Boid>()


    override fun create() {
        shapeRenderer = ShapeRenderer()
        repeat(boidCount) {
            var position = Vector2(
                    MathUtils.random() * Gdx.graphics.width,
                    MathUtils.random() * Gdx.graphics.height
            )
            var velocity = Vector2().setToRandomDirection().setLength(MathUtils.random() * maxSpeed)
            boids.add(Bird(position, velocity))
        }
    }

    override fun render() {
        boids.forEach { it.update(boids) }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        boids.forEach { it.render(shapeRenderer!!) }
    }

    override fun dispose() {
        shapeRenderer!!.dispose()
    }
}