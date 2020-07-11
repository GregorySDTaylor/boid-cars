package com.taylorbros

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.InputProcessor
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.mouseJointWith
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import ktx.box2d.polygon
import sun.security.acl.WorldGroupImpl
import kotlin.math.absoluteValue
import kotlin.math.pow

class BoidLord(
        override val size: Float,
        initialDensity: Float,
        initialPosition: Vector2,
        override val localDistance: Float,
        override val flockingPower: Float,
        private val maxAcceleration: Float,
        world: World,
        private val pixelsPerMeter: Float,
        private val stageWidth: Float,
        private val stageHeight: Float
) : Boid, InputProcessor {

    private val body = world.body {
        type = BodyDef.BodyType.DynamicBody
        position.set(initialPosition.x, initialPosition.y)
        circle(radius = size) {
            restitution = 0.2f
            density = initialDensity
        }
        polygon(Vector2(0f, size),
                Vector2(size * 2, 0f),
                Vector2(0f, -size))
    }

    private val ground = world.body {
        type = BodyDef.BodyType.StaticBody
    }

    private val mousejoint = ground.mouseJointWith(body) {
        maxForce = 1000f
    }

    override val position: Vector2
        get() = this.body.position

    override val velocity: Vector2
        get() = this.body.linearVelocity

    override fun update(entities: Set<Any>) {
//        TODO("Not yet implemented")
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        mousejoint.target = Vector2((screenX/pixelsPerMeter) - (stageWidth/2), ((screenY/pixelsPerMeter) - (stageHeight/2))*-1)
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }
}