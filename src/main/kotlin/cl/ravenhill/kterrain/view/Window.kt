package cl.ravenhill.kterrain.view

import cl.ravenhill.kterrain.controller.GLFWController
import cl.ravenhill.kterrain.controller.GLFWException
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryUtil

/**
 * @author <a href=mailto:ignacio.slater@ug.uchile.cl>Ignacio Slater Muñoz</a>
 */
data class Window(private val name: String, var height: Int, var width: Int) {
  val id = GLFW.glfwCreateWindow(width, height, name, MemoryUtil.NULL, MemoryUtil.NULL)

  init {
    if (id == MemoryUtil.NULL) {
      throw GLFWException("Failed to create the GLFW window")
    }
    setupFrameBufferSizeCallback()

    // Make the OpenGL context current
    GLFW.glfwMakeContextCurrent(id)
    // Enable v-sync
    GLFW.glfwSwapInterval(0)

    // Make the window visible
    GLFW.glfwShowWindow(id)
  }

  /**
   * This callback will be called each time the window is resized
   */
  private fun setupFrameBufferSizeCallback() {
    GLFW.glfwSetFramebufferSizeCallback(id) { _, w, h ->
      if (w > 0 && h > 0
        && (width != w || height != h)
      ) {
        width = w
        height = h
      }
    }
  }
}
