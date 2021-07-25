package cl.ravenhill.kterrain.view

import cl.ravenhill.kterrain.Shader
import cl.ravenhill.kterrain.controller.GLFWController
import cl.ravenhill.kterrain.controller.GLFWException
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil

/**
 * @author <a href=mailto:ignacio.slater@ug.uchile.cl>Ignacio Slater Muñoz</a>
 */
data class Window(private val name: String, var height: Int, var width: Int) {
  lateinit var shader: Shader
  val id = GLFW.glfwCreateWindow(width, height, name, MemoryUtil.NULL, MemoryUtil.NULL)

  /** Time of last frame. */
  private var lastFrame = 0f
  private var viewMatrix = Matrix4f()
  private var projMatrix = Matrix4f()

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

  fun free() {
    // Free the window callbacks and destroy the window
    Callbacks.glfwFreeCallbacks(id)
    GLFW.glfwDestroyWindow(id)
  }

  fun shouldClose() = GLFW.glfwWindowShouldClose(id)

  fun swapBuffers() {
    GLFW.glfwSwapBuffers(id)
  }
}
