package cl.ravenhill.kterrain.utils

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils

class Controller(private val window: WindowGLFW) {
  private var matrixBuffer = BufferUtils.createFloatBuffer(16)
  private var modelMatrix = Matrix4f()
  private var viewMatrix = Matrix4f()
  private var projMatrix = Matrix4f()

  private val lightPos = Vector3f()

  private val cameraPos = Vector3f()
  private val cameraFront = Vector3f()
  private val cameraUp = Vector3f()

  private var deltaTime = 0f  // Time between current and last frame.
  private var lastFrame = 0f  // Time of last frame.
  private var lastX = 400f
  private var lastY = 300f
  private var pitch = 0.0

  private var yaw = 0.0
  private var fov = 45f
}