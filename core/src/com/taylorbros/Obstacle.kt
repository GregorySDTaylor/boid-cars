package com.taylorbros

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.chain
import ktx.box2d.circle

class Obstacle(initialPosition: Vector2, val size: Float, world: World) : ShapeRenderable {

    private val body = world.body {
        type = BodyDef.BodyType.StaticBody
        position.set(initialPosition)
        circle(radius = size)
    }

    val position: Vector2
        get() = this.body.position

    override fun shapeRender(shapeRenderer: ShapeRenderer, pixelsPerMeter: Float) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.5f, 0.2f, 0.2f, 1f)
        shapeRenderer.circle(body.position.x * pixelsPerMeter,
                body.position.y * pixelsPerMeter,
                size * pixelsPerMeter)
    }
}
