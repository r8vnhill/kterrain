/**
 * "KTerrain" (c) by Ignacio Slater M.
 * "KTerrain" is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package cl.ravenhill.kterrain.controller

import cl.ravenhill.kterrain.view.Window
import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import kotlin.math.cos
import kotlin.math.sin

internal fun bindKeyCallback(window: Window, camera: Camera) {
  // Setup key callback. It will be called every time GeometryShaderTest20 key is pressed, repeated
  // or released.
  glfwSetKeyCallback(window.id) { win, key, _, _, _ ->
    val cameraSpeed = 160f * GLFWController.updateRate
    when (key) {
      GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(win, true)
      GLFW_KEY_W -> {
        camera.position.x += cameraSpeed * camera.front.x
        camera.position.y += cameraSpeed * camera.front.y
        camera.position.z += cameraSpeed * camera.front.z
      }
      GLFW_KEY_S -> {
        camera.position.x -= cameraSpeed * camera.front.x
        camera.position.y -= cameraSpeed * camera.front.y
        camera.position.z -= cameraSpeed * camera.front.z
      }
      GLFW_KEY_A -> {
        val auxVec = Vector3f()
        camera.front.cross(camera.up, auxVec)
        auxVec.normalize()
        camera.position.x -= cameraSpeed * auxVec.x
        camera.position.y -= cameraSpeed * auxVec.y
        camera.position.z -= cameraSpeed * auxVec.z
      }
      GLFW_KEY_D -> {
        val auxVec = Vector3f()
        camera.front.cross(camera.up, auxVec)
        auxVec.normalize()
        camera.position.x += cameraSpeed * auxVec.x
        camera.position.y += cameraSpeed * auxVec.y
        camera.position.z += cameraSpeed * auxVec.z
      }
    }
  }
}

internal fun bindCursorPosCallback(window: Window, camera: Camera) {
  // Captures and hides the cursor
  glfwSetInputMode(window.id, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
  // Set up cursor position callback,
  glfwSetCursorPosCallback(window.id) { _, xpos, ypos ->
    var xoffset = xpos - camera.lastX
    var yoffset = ypos - camera.lastY
    camera.lastX = xpos.toFloat()
    camera.lastY = ypos.toFloat()

    val sensitivity = 0.05f
    xoffset *= sensitivity
    yoffset *= sensitivity

    camera.yaw += xoffset
    camera.pitch += yoffset

    if (camera.pitch > 89.0)
      camera.pitch = 89.0
    if (camera.pitch < -89.0)
      camera.pitch = -89.0

    val front = Vector3f(
      (cos(Math.toRadians(camera.pitch)) * cos(Math.toRadians(camera.yaw))).toFloat(),
      -sin(Math.toRadians(camera.pitch)).toFloat(),
      (cos(Math.toRadians(camera.pitch)) * sin(Math.toRadians(camera.yaw))).toFloat()
    )
    front.normalize()
    camera.front.set(front)
  }
}

internal fun bindMouseWheelCallback(window: Window, camera: Camera) {
  // Set up mouse wheel callback
  glfwSetScrollCallback(window.id) { _, _, yoffset ->
    when {
      camera.fov in 1f..45f -> camera.fov -= yoffset.toFloat()
      camera.fov <= 1f -> camera.fov = 1f
      else -> camera.fov = 45f
    }
  }
}

internal fun createErrorCallback() {
  glfwSetErrorCallback(object : GLFWErrorCallback() {
    private val delegate = createPrint(System.err)

    override fun invoke(error: Int, description: Long) {
      if (error == GLFW_VERSION_UNAVAILABLE)
        System.err.println("This demo requires OpenGL 3.0 or higher.")
      delegate.invoke(error, description)
    }

    override fun free() {
      delegate.free()
    }
  })
}