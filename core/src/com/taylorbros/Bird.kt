package com.taylorbros

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import kotlin.math.absoluteValue
import kotlin.math.pow

class Bird(
        override val position: Vector2,
        override val velocity: Vector2,
        override val localDistance: Float,
        override val flockingPower: Float,
        private val maxSpeed: Float,
        private val maxAcceleration: Float,
        private val size: Float
) : Boid, ShapeRenderable {

    private val acceleration = Vector2()

    override fun update(otherLocalBoids: Set<Boid>, obstacles: Set<Obstacle>) {
        acceleration.setZero()
        if (otherLocalBoids.isNotEmpty()) {
            val separationForce = separationForce(otherLocalBoids, localDistance)
            val alignmentForce = alignmentForce(otherLocalBoids)
            val cohesionForce = cohesionForce(otherLocalBoids, localDistance)
            val separationForceMagnitude = separationForce.len()
            val alignmentForceMagnitude = alignmentForce.len()
            val cohesionForceMagnitude = cohesionForce.len()
            acceleration.add(separationForce)
            acceleration.add(alignmentForce)
            acceleration.add(cohesionForce)
        }
        val obstacleAvoidanceForce = obstacleAvoidanceForce(obstacles, localDistance)
        acceleration.add(obstacleAvoidanceForce)
        if (acceleration.len() > maxAcceleration) {
            acceleration.setLength(maxAcceleration)
        }
        velocity.add(acceleration)
        if (velocity.len() > maxSpeed) {
            velocity.setLength(maxSpeed)
        }
        position.add(velocity)
        reflectEdges()
        reflectObstacles(obstacles)
    }

    private fun reflectEdges() {
        if (position.x > Gdx.graphics.width) {
            velocity.x = - velocity.x.absoluteValue
        }
        if (position.y > Gdx.graphics.height) {
            velocity.y = - velocity.y.absoluteValue
        }
        if (position.x < 0) {
            velocity.x = velocity.x.absoluteValue
        }
        if (position.y < 0) {
            velocity.y = velocity.y.absoluteValue
        }
    }

    private fun reflectObstacles(obstacles: Set<Obstacle>) {
        obstacles.forEach {
            if (this.position.dst(it.position) < (it.size + this.size)) {
                this.velocity.setAngle(this.position.cpy().sub(it.position).angle())
                this.position.set(it.position.cpy().add(this.position.cpy().sub(it.position).setLength(it.size + this.size)))
            }
        }
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
            val squareProportionOfLocalDistance = proportionOfLocalDistance.pow(2)
            val inverseProportionOfLocalDistance = 1 - squareProportionOfLocalDistance
            vectorAwayFromOther.setLength(inverseProportionOfLocalDistance)
            separationForce.add(vectorAwayFromOther)
        }
        return separationForce.scl(flockingPower)
    }

    private fun alignmentForce(otherLocalBoids: Set<Boid>): Vector2 {
        val averageOtherVelocity = Vector2()
        otherLocalBoids.forEach { other ->
            averageOtherVelocity.add(other.velocity)
        }
        val velocityDifference = averageOtherVelocity.sub(this.velocity)
        val velocityDifferenceMagnitude = velocityDifference.len()
        val averageOtherVelocityMagnitude = averageOtherVelocity.len()
        val thisVelocityMagnitude = this.velocity.len()
        val normalizedMagnitude = velocityDifferenceMagnitude / (averageOtherVelocityMagnitude + thisVelocityMagnitude)
        val normalizedVelocityDifference = velocityDifference.setLength(normalizedMagnitude)
        return normalizedVelocityDifference.scl(flockingPower)
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
        val rootProportionOfLocalDistance = proportionOfLocalDistance.pow(1/2)
        val cohesionForce = vectorToCenterOfMass.setLength(rootProportionOfLocalDistance)
        return cohesionForce.scl(flockingPower)
    }

    private fun obstacleAvoidanceForce(obstacles: Set<Obstacle>, localDistance: Float): Vector2 {
        val avoidForce = Vector2()
        obstacles.forEach {
            val distance = this.position.dst(it.position)
            if (distance < (this.size + it.size + localDistance)) {
                val avoidMagnitude = 1 - (localDistance / (distance - this.size - it.size))
                val avoidVector = this.position.cpy().sub(it.position).setLength(avoidMagnitude)
                avoidForce.add(avoidVector)
            }
        }
        return avoidForce
    }

    override fun shapeRender(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f)
        shapeRenderer.circle(position.x, position.y, size)
        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f)
        shapeRenderer.rectLine(position, position.cpy().add(velocity.cpy().scl(2f)), 3f)
    }
}