package cl.ravenhill.kterrain.utils

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback

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