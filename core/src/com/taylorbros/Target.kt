package com.taylorbros

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class Target(val position: Vector2) : ShapeRenderable {

    override fun shapeRender(shapeRenderer: ShapeRenderer, pixelsPerMeter: Float) {
        shapeRenderer.identity()
        shapeRenderer.translate(position.x, position.y, 0f)
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.9f, 0.2f, 1f)
        shapeRenderer.circle(0f, 0f, 0.1f, 36)
    }

}