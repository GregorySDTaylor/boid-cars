package com.taylorbros

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

interface Boid {
    val position: Vector2
    val velocity: Vector2
    val localDistance: Float
    val flockingPower: Float
    fun update(otherLocalBoids: Set<Boid>, obstacles: Set<Obstacle>)
    fun render(shapeRenderer: ShapeRenderer)
}