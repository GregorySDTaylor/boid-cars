package com.taylorbros

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class Obstacle(val position: Vector2, val size: Float) : ShapeRenderable {
    // TODO add blocking behavior

    override fun shapeRender(shapeRenderer: ShapeRenderer, pixelsPerMeter: Float) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.5f, 0.2f, 0.2f, 1f)
        shapeRenderer.circle(position.x * pixelsPerMeter, position.y * pixelsPerMeter, size * pixelsPerMeter)
    }
}
