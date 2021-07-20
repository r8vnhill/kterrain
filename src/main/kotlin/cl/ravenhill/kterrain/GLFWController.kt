package cl.ravenhill.kterrain

import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryUtil

/**
 * @author <a href=mailto:ignacio.slater@ug.uchile.cl>Ignacio Slater Muñoz</a>
 */
object GLFWController {
  fun createWindow(name: String, width: Int, height: Int): Long {
    return GLFW.glfwCreateWindow(
      width,
      height,
      name,
      MemoryUtil.NULL,
      MemoryUtil.NULL
    )
  }

  init {
    // Initialize GLFW library.
    if (!GLFW.glfwInit()) throw IllegalStateException("Unable to initialize GLFW")
    // Require OpenGL version 3.3
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
    // Explicitly use the core-profile (only a subset of OpenGL).
    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
    // the window will stay hidden after creation
    GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
    // the window will be resizable
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
  }


}