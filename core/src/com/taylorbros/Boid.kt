package com.taylorbros

import com.badlogic.gdx.math.Vector2

interface Boid : Updateable {
    val position: Vector2
    val velocity: Vector2
    val localDistance: Float
    val flockingPower: Float
    val size: Float
}