package cl.ravenhill.kterrain.utils

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetWindowSize
import org.lwjgl.system.MemoryStack

object Controller {
  lateinit var window: WindowGLFW
    private set

  private var matrixBuffer = BufferUtils.createFloatBuffer(16)
  private var modelMatrix = Matrix4f()
  private var viewMatrix = Matrix4f()
  private var projMatrix = Matrix4f()

  private val lightPos = Vector3f()

  internal val cameraPos = Vector3f()
  internal val cameraFront = Vector3f()
  internal val cameraSideways = Vector3f()
  private val cameraUp = Vector3f()

  internal var lastX = 400f
  internal var lastY = 300f
  internal var pitch = 0.0

  internal var yaw = 0.0
  internal var fov = 45f

  init {
    cameraFront.cross(cameraUp, cameraSideways)
    cameraSideways.normalize()
  }

  fun start(window: WindowGLFW) {
    this.window = window
    setupCallbacks()
    setupFrameBuffer()
    pushFrame()
  }

  private fun setupFrameBuffer() {
    GLFW.glfwSetFramebufferSizeCallback(window.id) { _, width, height ->
      if (width > 0 && height > 0
        && (window.width != width || window.height != height)
      ) {
        window.width = width
        window.height = height
      }
    }
  }

  private fun setupCallbacks() {
    keyCallbackConfig()
    cursorConfig()
  }

  private fun pushFrame() {
    // Get the thread stack and push the shader's new frame
    MemoryStack.stackPush().use { stack ->
      val pWidth = stack.mallocInt(1) // int*
      val pHeight = stack.mallocInt(1) // int*
      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(window.id, pWidth, pHeight)

      // Get the resolution of the primary monitor
      val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

      window.width = pWidth.get(0)
      window.height = pHeight.get(0)

      // Center the window
      GLFW.glfwSetWindowPos(
        window.id, (vidmode!!.width() - window.width) / 2,
        (vidmode.height() - window.height) / 2
      )
    } // the stack frame is popped automatically
  }

  fun terminate() {
    // Free the window callbacks and destroy the window
    Callbacks.glfwFreeCallbacks(window.id)
    GLFW.glfwDestroyWindow(window.id)
    // Terminate GLFW and free the error callback
    GLFW.glfwTerminate()
    GLFW.glfwSetErrorCallback(null)?.free()
  }
}