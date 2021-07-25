package cl.ravenhill.kterrain.controller

import cl.ravenhill.kterrain.view.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.system.MemoryStack

/**
 * @author <a href=mailto:ignacio.slater@ug.uchile.cl>Ignacio Slater Muñoz</a>
 */
object GLFWController {
  lateinit var camera: Camera
    private set
  lateinit var window: Window
    private set
  /** Time between current and last frame.    */
  internal var updateRate = 0f

  init {
    // Setup an error callback.
    createErrorCallback()
    // Initialize GLFW library.
    if (!GLFW.glfwInit()) throw GLFWException("Unable to initialize GLFW")
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

  @Throws(GLFWException::class)
  fun createWindow(name: String, height: Int, width: Int) {
    window = Window(name, width, height)
    camera = Camera()
    bindKeyCallback(window, camera)
    bindCursorPosCallback(window, camera)
    pushFrame()
  }

  private fun createErrorCallback() {
    GLFW.glfwSetErrorCallback(object : GLFWErrorCallback() {
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
  }

  private fun pushFrame() {
    // Get the thread stack and push GeometryShaderTest20 new frame
    MemoryStack.stackPush().use { stack ->
      val pWidth = stack.mallocInt(1) // int*
      val pHeight = stack.mallocInt(1) // int*
      // Get the window size passed to glfwCreateWindow
      GLFW.glfwGetWindowSize(window.id, pWidth, pHeight)

      // Get the resolution of the primary monitor
      val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

      window.width = pWidth.get(0)
      window.height = pHeight.get(0)

      // Center the window
      GLFW.glfwSetWindowPos(
        window.id,
        (vidmode!!.width() - window.width) / 2,
        (vidmode.height() - window.height) / 2
      )
    } // the stack frame is popped automatically
  }
}

class GLFWException(msg: String) : Exception(msg)