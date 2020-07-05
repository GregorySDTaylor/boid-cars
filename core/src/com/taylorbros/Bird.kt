package com.taylorbros

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.circle
import kotlin.math.absoluteValue
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody

class Bird(
        override val size: Float,
        initialDensity: Float,
        initialPosition: Vector2,
        initialVelocity: Vector2,
        override val localDistance: Float,
        override val flockingPower: Float,
        private val maxAcceleration: Float,
        world: World
) : Boid, ShapeRenderable {

    // set up box2d physics body
    private val body = world.body {
        type = DynamicBody
        position.set(initialPosition.x, initialPosition.y)
        circle(radius = size) {
            restitution = 0.2f
            density = initialDensity
        }
    }
    init {
        body.linearVelocity = velocity
    }

    override val position: Vector2
        get() = this.body.position

    override val velocity: Vector2
        get() = this.body.linearVelocity

    var desiredMovement = Vector2()

    override fun update(entities: Set<Any>) {
        desiredMovement = Vector2()
        val localBoids = localBoidsFrom(entities)
        if (localBoids.isNotEmpty()) {
            desiredMovement.add(separationForce(localBoids))
            desiredMovement.add(alignmentForce(localBoids))
            desiredMovement.add(cohesionForce(localBoids))
        }
        val localObstacles = localObstaclesFrom(entities)
        if (localObstacles.isNotEmpty()) {
            desiredMovement.add(obstacleAvoidanceForce(localObstacles))
        }
        if (desiredMovement.len() > maxAcceleration) {
            desiredMovement.setLength(maxAcceleration)
        }
        body.applyForceToCenter(desiredMovement, true)
    }

    private fun localObstaclesFrom(entities: Set<Any>): List<Obstacle> {
        return entities.filter {
            it is Obstacle
                    && this != it
                    && (this.position.dst(it.position) - this.size - it.size) < localDistance
        }.map { it as Obstacle }
    }

    private fun localBoidsFrom(entities: Set<Any>): List<Boid> {
        return entities.filter {
            it is Boid
                    && this != it
                    && (this.position.dst(it.position) - this.size - it.size) < localDistance
        }.map { it as Boid }
    }

    // steer to avoid crowding local flockmates
    private fun separationForce(otherLocalBoids: List<Boid>): Vector2 {
        val separationForce = Vector2()
        otherLocalBoids.forEach { other ->
            val vectorAwayFromOther = this.position.cpy().sub(other.position)

            // make separation force stronger for closer flockmates
            val distance = vectorAwayFromOther.len()
            val proportionOfLocalDistance = distance / localDistance
            val inverseProportionOfLocalDistance = 1 - proportionOfLocalDistance
            vectorAwayFromOther.setLength(inverseProportionOfLocalDistance)

            separationForce.add(vectorAwayFromOther)
        }

        // separation force should never exceed 1
        if (separationForce.len() > 1f) {
            separationForce.setLength(1f)
        }

        // scale separation force by the flockingPower
        return separationForce.scl(flockingPower)
    }

    // steer towards the average heading of local flockmates
    private fun alignmentForce(otherLocalBoids: List<Boid>): Vector2 {
        val sumOfOtherVelocities = Vector2()
        otherLocalBoids.forEach { other ->
            sumOfOtherVelocities.add(other.velocity)
        }
        val averageOfOtherVelocities = sumOfOtherVelocities.scl( 1f / otherLocalBoids.count() )

        // make alignment force stronger when this boids heading is more different than the average heading
        val velocityDifference = averageOfOtherVelocities.sub(this.velocity)
        val velocityDifferenceMagnitude = velocityDifference.len()
        val averageOtherVelocityMagnitude = averageOfOtherVelocities.len()
        val thisVelocityMagnitude = this.velocity.len()

        // alignment force should never be greater than 1
        val normalizedMagnitude = velocityDifferenceMagnitude / (averageOtherVelocityMagnitude + thisVelocityMagnitude)
        val normalizedVelocityDifference = velocityDifference.setLength(normalizedMagnitude)

        // scale alignment force by the flockingPower
        return normalizedVelocityDifference.scl(flockingPower)
    }

    // steer to move towards the average position of local flockmates
    private fun cohesionForce(otherLocalBoids: List<Boid>): Vector2 {
        val sumOfVectorsToOthers = Vector2()
        otherLocalBoids.forEach { other ->
            val vectorToOther = other.position.cpy().sub(this.position)
            sumOfVectorsToOthers.add(vectorToOther)
        }
        val vectorToAverageOtherCenters = sumOfVectorsToOthers.scl(1f / otherLocalBoids.count())

        // cohesion force should never be greater than 1
        val distance = vectorToAverageOtherCenters.len()
        val proportionOfLocalDistance = distance / localDistance
        val cohesionForce = vectorToAverageOtherCenters.setLength(proportionOfLocalDistance)

        // scale cohesion force by the flockingPower
        return cohesionForce.scl(flockingPower)
    }

    private fun obstacleAvoidanceForce(obstacles: List<Obstacle>): Vector2 {
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

    override fun shapeRender(shapeRenderer: ShapeRenderer, pixelsPerMeter: Float) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f)
        shapeRenderer.circle(body.position.x * pixelsPerMeter,
                body.position.y * pixelsPerMeter,
                size * pixelsPerMeter)
        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f)
        val desiredMovementPosition = body.position.cpy().add(desiredMovement.cpy().scl(0.1f))
        shapeRenderer.rectLine(body.position.x * pixelsPerMeter,
                body.position.y * pixelsPerMeter,
                desiredMovementPosition.x * pixelsPerMeter,
                desiredMovementPosition.y * pixelsPerMeter,
                3f)
    }
}