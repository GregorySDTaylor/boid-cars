package com.taylorbros

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.chain
import ktx.box2d.circle

class BoundingWalls(stageWidth: Float, stageHeight: Float, world: World) {

    // set up static box2d physics body
    private val body = world.body {
        type = BodyDef.BodyType.StaticBody
        position.set(0f, 0f)
        chain(Vector2(0f, 0f),
                Vector2(0f, stageHeight),
                Vector2(stageWidth, stageHeight),
                Vector2(stageWidth, 0f),
                Vector2(0f, 0f))
    }
}