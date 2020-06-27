package com.taylorbros

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class Obstacle(val position: Vector2, val size: Float) {
    fun render(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.5f, 0.2f, 0.2f, 1f)
        shapeRenderer.circle(position.x, position.y, size)
    }
}