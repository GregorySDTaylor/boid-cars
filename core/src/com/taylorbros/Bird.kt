package com.taylorbros

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import kotlin.math.pow

class Bird(
        override val position: Vector2,
        override val velocity: Vector2,
        override val localDistance: Float,
        override val flockingPower: Float,
        private val maxSpeed: Float,
        private val maxAcceleration: Float
) : Boid {

    val acceleration = Vector2()

    override fun update(otherLocalBoids: Set<Boid>) {
        acceleration.setZero()
        acceleration.add(separationForce(otherLocalBoids, localDistance))
        acceleration.add(alignmentForce(otherLocalBoids, localDistance))
        acceleration.add(cohesionForce(otherLocalBoids, localDistance))
        if (acceleration.len() > maxAcceleration) { acceleration.setLength(maxAcceleration)}
        velocity.add(acceleration)
        if (velocity.len() > maxSpeed) { velocity.setLength(maxSpeed)}
        position.add(velocity)
        if (position.x > Gdx.graphics.width) {position.x = 0f}
        if (position.y > Gdx.graphics.height) {position.y = 0f}
        if (position.x < 0) {position.x = Gdx.graphics.width.toFloat()}
        if (position.y < 0) {position.y = Gdx.graphics.height.toFloat()}
    }

    private fun separationForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        return if (otherLocalBoids.isEmpty()) {
            Vector2()
        }
        else {
            val separationForce = Vector2()
            otherLocalBoids.forEach { other ->
                val vectorAwayFromOther = this.position.cpy().sub(other.position)
                val distance = vectorAwayFromOther.len()
                val proportionOfLocalDistance = distance / localDistance
                val inverseProportionOfLocalDistance = 1 - proportionOfLocalDistance
                vectorAwayFromOther.setLength(inverseProportionOfLocalDistance)
                separationForce.add(vectorAwayFromOther)
            }
            separationForce.scl(flockingPower / otherLocalBoids.count())
        }
    }

    private fun alignmentForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        val alignmentForce = Vector2()
        return alignmentForce
    }

    private fun cohesionForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        val cohesionForce = Vector2()
        return cohesionForce
    }

    override fun render(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.circle(position.x, position.y, 5f);
        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f);
        shapeRenderer.line(position, position.cpy().add(velocity.cpy().scl(5f)))
    }
}