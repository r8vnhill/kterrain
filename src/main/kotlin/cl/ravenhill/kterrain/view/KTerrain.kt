package cl.ravenhill.kterrain.view

import cl.ravenhill.kterrain.controller.GLFWController
import cl.ravenhill.kterrain.Heightmap
import cl.ravenhill.kterrain.Shader
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack.stackPush
import kotlin.math.cos
import kotlin.math.sin


class KTerrain(private val detail: Int = 5) {
  // Window parameter definitions
  /** GLFW window.    */
  private var window = 0L

  /** GLFW window width.   */
  private var height = 768

  /** GLFW window heigth. */
  private var width = 1024

  /** Debug callback. */
  private lateinit var debugProc: Callback

  /** Vertex array object id. */
  private var vaoId: Int = 0

  /** Vertices to be drawn.   */
  private var vertices = mutableListOf<Float>()


  private var matrixBuffer = BufferUtils.createFloatBuffer(16)
  private var modelMatrix = Matrix4f()
  private var viewMatrix = Matrix4f()
  private var projMatrix = Matrix4f()

  private val lightPos = Vector3f()

  private var cameraPos = Vector3f()
  private var cameraFront = Vector3f()
  private val cameraUp = Vector3f()

  /** Shader program. */
  private lateinit var shader: Shader

  /** Time between current and last frame.    */
  private var deltaTime = 0f

  /** Time of last frame. */
  private var lastFrame = 0f

  fun run() {
    try {
      init()
      loop()

      // Free the window callbacks and destroy the window
      glfwFreeCallbacks(window)
      glfwDestroyWindow(window)
    } catch (t: Throwable) {
      t.printStackTrace()
    } finally {
      // Terminate GLFW and free the error callback
      glfwTerminate()
      glfwSetErrorCallback(null)?.free()
    }
  }

  private var fov = 45f

  /** Sets up initial configuration.  */
  private fun init() {
    GLFWController.createWindow("KTR", width, height)
    window = GLFWController.window.id
    cameraPos = GLFWController.camera.position
    cameraFront = GLFWController.camera.front

    GL.createCapabilities()
    debugProc = GLUtil.setupDebugMessageCallback()!!

    GL11.glClearColor(0.55f, 0.75f, 0.95f, 1.0f)
    GL11.glEnable(GL11.GL_DEPTH_TEST)
    GL11.glEnable(GL11.GL_CULL_FACE)

    // Creates all needed GL resources
    createVao()
    initProgram()
    println("Controls:")
    println("   Move: WASD")
    println("   Camera: Mouse")
    println("   Zoom/Unzoom: Mouse wheel")
    println("   Exit: ESC")
  }

  /** Creates the vertex array object.    */
  private fun createVao() {
    vaoId = glGenVertexArrays()
    glBindVertexArray(vaoId)
    val heightmap = Heightmap(detail)
    heightmap.create()
    vertices = heightmap.vertices

    println()
    println("Loading vertices to OpenGl context...")

    val pb = BufferUtils.createFloatBuffer(vertices.size)

    pb.put(vertices.toFloatArray())
    pb.flip()

    // setup vertices buffer
    val vbo = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, pb, GL_STATIC_DRAW)
    // position attribute
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0L)
    glEnableVertexAttribArray(0)
    // normal attribute
    GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * 4, 3 * 4)
    GL20.glEnableVertexAttribArray(1)

    // setup vertex visibility buffer
    val visVbo = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, visVbo)
    glBindVertexArray(0)
  }

  private fun initProgram() {
    shader = Shader("resources/terrain_vs.glsl", "resources/terrain_fs.glsl")

    // Rotates the world space around Y
    modelMatrix.rotateY(toRadians(-35.0).toFloat())
    // Sets up the camera and view matrix
    cameraPos.set(0f, 2f, 0f)
    cameraFront.set(0f, 0f, 1f)
    cameraUp.set(0f, 1f, 0f)
    viewMatrix.setLookAt(
      cameraPos.x, cameraPos.y, cameraPos.z,
      cameraPos.x + cameraFront.x, cameraPos.y + cameraFront.y, cameraPos.z + cameraFront.z,
      cameraUp.x, cameraUp.y, cameraUp.z
    )
    // Sets a perspective projection
    projMatrix.setPerspective(
      Math.toRadians(45.0).toFloat(), width.toFloat() / height,
      0.01f, 100.0f
    )
    // Sets light position
    lightPos.set(0f, 5f, 0f)
  }

  /** Draws the com.google.islaterm.terrain.  */
  private fun render() {
    // Starts using the shader program
    shader.use()

    // Sets the model, view and projection matrices
    shader.setMatrix("modelMatrix", false, modelMatrix.get(matrixBuffer))
    shader.setMatrix("viewMatrix", false, viewMatrix.get(matrixBuffer))
    shader.setMatrix("projMatrix", false, projMatrix.get(matrixBuffer))
    // Sets the light position
    shader.setVec3("lightPos", lightPos.x, lightPos.y, lightPos.z)

    glBindVertexArray(vaoId)
    glDrawArrays(GL11.GL_TRIANGLES, 0, vertices.size)
    glBindVertexArray(0)
    // Stops using the shader program
    shader.close()
  }

  private fun update() {
    val currentFrame = glfwGetTime().toFloat()
    deltaTime = currentFrame - lastFrame
    lastFrame = currentFrame
    viewMatrix.setLookAt(
      cameraPos.x, cameraPos.y, cameraPos.z,
      cameraPos.x + cameraFront.x, cameraPos.y + cameraFront.y, cameraPos.z + cameraFront.z,
      cameraUp.x, cameraUp.y, cameraUp.z
    )
    projMatrix.setPerspective(
      toRadians(fov.toDouble()).toFloat(), width.toFloat() / height,
      0.01f, 100.0f
    )
  }

  private fun loop() {
    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window)) {
      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents()
      glViewport(0, 0, width, height)
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

      update()
      render()

      glfwSwapBuffers(window) // swap the color buffers
    }
  }
}

fun main(args: Array<String>) {
  KTerrain(5).run()
}