package cl.ravenhill.kterrain.utils

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW

class Controller(private val window: WindowGLFW) {

  private var matrixBuffer = BufferUtils.createFloatBuffer(16)
  private var modelMatrix = Matrix4f()
  private var viewMatrix = Matrix4f()
  private var projMatrix = Matrix4f()

  private val lightPos = Vector3f()

  private val cameraPos = Vector3f()
  private val cameraFront = Vector3f()
  private val cameraSideways = Vector3f()
  private val cameraUp = Vector3f()

  private var deltaTime = 0f  // Time between current and last frame.
  private var lastFrame = 0f  // Time of last frame.
  private var lastX = 400f
  private var lastY = 300f
  private var pitch = 0.0

  private var yaw = 0.0
  private var fov = 45f

  init {
    cameraFront.cross(cameraUp, cameraSideways)
    cameraSideways.normalize()
  }

  fun setupKeyCallbacks() {
    GLFW.glfwSetKeyCallback(window.id) { window, key, _, _, _ ->
      val cameraSpeed = 20f * deltaTime
      when (key) {
        GLFW.GLFW_KEY_ESCAPE -> GLFW.glfwSetWindowShouldClose(window, true)
        GLFW.GLFW_KEY_W -> moveForward(cameraSpeed)
        GLFW.GLFW_KEY_S -> moveBackwards(cameraSpeed)
        GLFW.GLFW_KEY_A -> moveLeft(cameraSpeed)
        GLFW.GLFW_KEY_D -> moveRight(cameraSpeed)
      }
    }
  }

  private fun moveRight(speed: Float) {
    move(speed, 1, cameraSideways)
  }

  private fun moveLeft(speed: Float) {
    move(speed, -1, cameraSideways)
  }

  private fun moveBackwards(speed: Float) {
    move(speed, -1, cameraFront)
  }

  private fun moveForward(speed: Float) {
    move(speed, 1, cameraFront)
  }

  private fun move(speed: Float, direction: Int, axis: Vector3f) {
    val translation = direction * speed
    cameraPos.x += translation * axis.x
    cameraPos.y += translation * axis.y
    cameraPos.z += translation * axis.z
  }
}