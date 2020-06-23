package com.taylorbros

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class Bird(
        override val position: Vector2,
        override val velocity: Vector2,
        override val localDistance: Float,
        override val flockingPower: Float
) : Boid {

    val acceleration = Vector2()

    override fun update(otherLocalBoids: Set<Boid>) {
        acceleration.setZero()
        acceleration.add(separationForce(otherLocalBoids, localDistance))
        acceleration.add(alignmentForce(otherLocalBoids, localDistance))
        acceleration.add(cohesionForce(otherLocalBoids, localDistance))
        position.add(velocity)
        if (position.x > Gdx.graphics.width) {position.x = 0f}
        if (position.y > Gdx.graphics.height) {position.y = 0f}
        if (position.x < 0) {position.x = Gdx.graphics.width.toFloat()}
        if (position.y < 0) {position.y = Gdx.graphics.height.toFloat()}
    }

    private fun separationForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        return Vector2()
    }

    private fun alignmentForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        return Vector2()
    }

    private fun cohesionForce(otherLocalBoids: Set<Boid>, localDistance: Float): Vector2 {
        return Vector2()
    }

    override fun render(shapeRenderer: ShapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.circle(position.x, position.y, 5f);
        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f);
        shapeRenderer.line(position, position.cpy().add(velocity.cpy().scl(5f)))
        shapeRenderer.end();
    }
}