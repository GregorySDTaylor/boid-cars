package com.taylorbros

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

interface Boid {
    val position: Vector2
    val velocity: Vector2
    fun update(otherBoids: Set<Boid>)
    fun render(shapeRenderer: ShapeRenderer)
}