package cl.ravenhill.kterrain.utils

import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import kotlin.math.cos
import kotlin.math.sin

private var deltaTime = 0f  // Time between current and last frame.
private var lastFrame = 0f  // Time of last frame.

/**
 * Setup for the app's error callback
 */
val errorCallback = GLFW.glfwSetErrorCallback(object : GLFWErrorCallback() {
  private val delegate = createPrint(System.err)

  override fun invoke(error: Int, description: Long) {
    if (error == GLFW.GLFW_VERSION_UNAVAILABLE)
      System.err.println("This demo requires OpenGL 3.0 or higher.")
    delegate.invoke(error, description)
  }

  override fun free() {
    delegate.free()
  }
})

fun Controller.keyCallbackConfig() {
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

private fun Controller.moveRight(speed: Float) {
  move(speed, 1, cameraSideways)
}

private fun Controller.moveLeft(speed: Float) {
  move(speed, -1, cameraSideways)
}

private fun Controller.moveBackwards(speed: Float) {
  move(speed, -1, cameraFront)
}

private fun Controller.moveForward(speed: Float) {
  move(speed, 1, cameraFront)
}

private fun Controller.move(speed: Float, direction: Int, axis: Vector3f) {
  val translation = direction * speed
  cameraPos.x += translation * axis.x
  cameraPos.y += translation * axis.y
  cameraPos.z += translation * axis.z
}

fun Controller.cursorConfig() {
  // Captures and hides the cursor
  GLFW.glfwSetInputMode(window.id, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
  // Set up cursor position callback,
  GLFW.glfwSetCursorPosCallback(window.id) { _, xpos, ypos ->
    var xoffset = xpos - lastX
    var yoffset = ypos - lastY
    lastX = xpos.toFloat()
    lastY = ypos.toFloat()

    val sensitivity = 0.05f
    xoffset *= sensitivity
    yoffset *= sensitivity

    yaw += xoffset
    pitch += yoffset

    if (pitch > 89.0)
      pitch = 89.0
    if (pitch < -89.0)
      pitch = -89.0

    val front = Vector3f(
      (cos(Math.toRadians(pitch)) * cos(Math.toRadians(yaw))).toFloat(),
      -sin(Math.toRadians(pitch)).toFloat(),
      (cos(Math.toRadians(pitch)) * sin(Math.toRadians(yaw))).toFloat()
    )
    front.normalize()
    cameraFront.set(front)
  }

  // Set up mouse wheel callback
  GLFW.glfwSetScrollCallback(window.id) { _, _, yoffset ->
    when {
      fov in 1f..45f -> fov -= yoffset.toFloat()
      fov <= 1f -> fov = 1f
      else -> fov = 45f
    }
  }
}