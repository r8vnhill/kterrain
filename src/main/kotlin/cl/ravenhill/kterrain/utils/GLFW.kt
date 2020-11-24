package cl.ravenhill.kterrain.utils

import org.lwjgl.glfw.GLFW

/**
 * Initializes GLFW and configures it.
 */
fun initGLFW() {
  // Initialize GLFW. Most GLFW functions will not work before doing this.
  if (!GLFW.glfwInit()) {
    throw IllegalStateException("Unable to initialize GLFW")
  }

  // Configure GLFW
  // optional, the current window hints are already the default
  GLFW.glfwDefaultWindowHints()
  // Require version 3.3
  GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
  GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
  GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
  GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE)
  // the window will stay hidden after creation
  GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
  // the window will be resizable
  GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
}

/**
 * Data class to hold the information of a window in GLFW
 */
data class WindowGLFW(var id: Long = 0L, var height: Int = 768, var width: Int = 1024)