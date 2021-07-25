package cl.ravenhill.kterrain.controller

import cl.ravenhill.kterrain.Heightmap
import cl.ravenhill.kterrain.Shader
import cl.ravenhill.kterrain.opengl.BufferUtils
import cl.ravenhill.kterrain.view.Window
import org.joml.Math.*
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack

/**
 * @author <a href=mailto:ignacio.slater@ug.uchile.cl>Ignacio Slater Muñoz</a>
 */
object GLFWController {
  private var vaoId: Int = 0
  lateinit var camera: Camera
    private set
  lateinit var window: Window
    private set
  private lateinit var heightmap: Heightmap

  /** Time of last frame. */
  private var lastFrame = 0f

  /** Shader program. */
  private lateinit var shader: Shader

  /** Time between current and last frame.    */
  internal var updateRate = 0f

  /** Light position vector */
  private val lightPos = Vector3f()

  /** Debug callback. */
  private lateinit var debugProc: Callback

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
    bindMouseWheelCallback(window, camera)
    pushFrame()
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

  fun enableOpenGL() {
    GL.createCapabilities()
    debugProc = GLUtil.setupDebugMessageCallback()!!

    GL11.glClearColor(0.55f, 0.75f, 0.95f, 1.0f)
    GL11.glEnable(GL11.GL_DEPTH_TEST)
    GL11.glEnable(GL11.GL_CULL_FACE)
  }

  /** Creates the vertex array object.    */
  fun initHeightmap(detailLevel: Int) {
    vaoId = BufferUtils.bindVertexArray()
    heightmap = Heightmap(detailLevel)
    heightmap.create()
  }

  fun createBuffers() {
    BufferUtils.initVertexBuffer(heightmap.vertices)
    BufferUtils.initVisibilityBuffer()
  }

  fun start() {
    shader = Shader("resources/terrain_vs.glsl", "resources/terrain_fs.glsl")

    // Rotates the world space around Y
    camera.modelMatrix.rotateY(toRadians(-35.0).toFloat())
    // Sets up the camera and view matrix
    camera.position.set(0f, 2f, 0f)
    camera.front.set(0f, 0f, 1f)
    camera.up.set(0f, 1f, 0f)
    camera.updateView()
    // Sets a perspective projection
    camera.updateProjection(45.0)
    // Sets light position
    lightPos.set(0f, 5f, 0f)
  }

  fun render() {
    // Starts using the shader program
    shader.use()

    shader.setMVP(camera)
    // Sets the light position
    shader.setVec3("lightPos", lightPos.x, lightPos.y, lightPos.z)

    GL30.glBindVertexArray(vaoId)
    GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, heightmap.vertices.size)
    GL30.glBindVertexArray(0)
    // Stops using the shader program
    shader.close()
    // swap the color buffers
    window.swapBuffers()
  }

  fun update() {
    // Poll for window events. The key callback above will only be
    // invoked during this call.
    GLFW.glfwPollEvents()
    GL30.glViewport(0, 0, window.width, window.height)
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

    val currentFrame = GLFW.glfwGetTime().toFloat()
    updateRate = currentFrame - lastFrame
    lastFrame = currentFrame
    camera.updateView()
    camera.updateProjection(camera.fov.toDouble())
  }
}

class GLFWException(msg: String) : Exception(msg)

