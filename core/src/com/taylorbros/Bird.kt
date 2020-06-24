package com.taylorbros

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class Bird(
        override val position: Vector2,
        override val velocity: Vector2,
        override val localDistance: Float,
        override val flockingPower: Float,
        private val maxSpeed: Float,
        private val maxAcceleration: Float
) : Boid {

    private val acceleration = Vector2()

    override fun update(otherLocalBoids: Set<Boid>) {
        acceleration.setZero()
        if (otherLocalBoids.isNotEmpty()) {
            val separationForce = separationForce(otherLocalBoids, localDistance)
            val alignmentForce = alignmentForce(otherLocalBoids, localDistance)
            val cohesionForce = cohesionForce(otherLocalBoids, localDistance)
            val separationForceMagnitude = separationForce.len()
            val alignmentForceMagnitude = alignmentForce.len()
            val cohesionForceMagnitude = cohesionForce.len()
            acceleration.add(separationForce)
            acceleration.add(alignmentForce)
            acceleration.add(cohesionForce)
        }
        if (acceleration.len() > maxAcceleration) {
            acceleration.setLength(maxAcceleration)
        }
        velocity.add(acceleration)
        if (velocity.len() > maxSpeed) {
            velocity.setLength(maxSpeed)
        }
        position.add(velocity)
        wrapEdges()
    }

    private fun wrapEdges() {
        if (position.x > Gdx.graphics.width) {
            position.x = 0f
        }
        if (position.y > Gdx.graphics.height) {
            position.y = 0f
        }
        if (position.x < 0) {
            position.x = Gdx.graphics.width.toFloat()
        }
        if (position.y < 0) {
            position.y = Gdx.graphics.height.toFloat()
        }
    }

    private fun separationForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        val separationForce = Vector2()
        otherLocalBoids.forEach { other ->
            val vectorAwayFromOther = this.position.cpy().sub(other.position)
            val distance = vectorAwayFromOther.len()
            val proportionOfLocalDistance = distance / localDistance
            val inverseProportionOfLocalDistance = 1 - proportionOfLocalDistance
            vectorAwayFromOther.setLength(inverseProportionOfLocalDistance)
            separationForce.add(vectorAwayFromOther)
        }
        return separationForce.scl(flockingPower / otherLocalBoids.count())
    }

    private fun alignmentForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        val alignmentForce = Vector2()
        return alignmentForce
    }

    private fun cohesionForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        val vectorToCenterOfMass = Vector2()
        otherLocalBoids.forEach { other ->
            val vectorToOther = other.position.cpy().sub(this.position)
            vectorToCenterOfMass.add(vectorToOther)
        }
        vectorToCenterOfMass.scl(1f / otherLocalBoids.count())
        val distance = vectorToCenterOfMass.len()
        val proportionOfLocalDistance = distance / localDistance
        val cohesionForce = vectorToCenterOfMass.setLength(proportionOfLocalDistance)
        return cohesionForce.scl(flockingPower)
    }

    override fun render(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f)
        shapeRenderer.circle(position.x, position.y, 5f)
        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f)
        shapeRenderer.line(position, position.cpy().add(velocity.cpy().scl(5f)))
    }
}