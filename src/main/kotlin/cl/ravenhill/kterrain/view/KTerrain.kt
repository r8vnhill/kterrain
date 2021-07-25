package cl.ravenhill.kterrain.view

import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate
import cl.ravenhill.kterrain.controller.GLFWController as controller


class KTerrain(private val detail: Int = 5) {
  // Window parameter definitions

  /** GLFW window width.   */
  private var height = 768

  /** GLFW window heigth. */
  private var width = 1024

  fun run() {
    try {
      init()
      println("Controls:")
      println("   Move: WASD")
      println("   Camera: Mouse")
      println("   Zoom/Unzoom: Mouse wheel")
      println("   Exit: ESC")

      loop()
      controller.window.free()
    } catch (t: Throwable) {
      t.printStackTrace()
    } finally {
      // Terminate GLFW and free the error callback
      glfwTerminate()
      glfwSetErrorCallback(null)?.free()
    }
  }

  /** Sets up initial configuration.  */
  private fun init() {
    controller.createWindow("KTR", width, height)
    controller.enableOpenGL()

    // Creates all needed GL resources
    controller.initHeightmap(detail)

    println()
    println("Loading vertices to OpenGl context...")
    controller.createBuffers()

    controller.start()
  }

  private fun loop() {
    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!controller.window.shouldClose()) {
      controller.update()
      controller.render()
    }
  }
}

fun main(args: Array<String>) {
  KTerrain(5).run()
}