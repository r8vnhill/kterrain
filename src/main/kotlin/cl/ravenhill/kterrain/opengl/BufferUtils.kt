package cl.ravenhill.kterrain.opengl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils as glBufferUtils

object BufferUtils {
  fun bindVertexArray(): Int {
    val id = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(id)
    return id
  }

  fun initVertexBuffer(vertices: List<Float>) {
    val pb = glBufferUtils.createFloatBuffer(vertices.size)

    pb.put(vertices.toFloatArray())
    pb.flip()

    // setup vertices buffer
    val vbo = GL30.glGenBuffers()
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)
    GL30.glBufferData(GL30.GL_ARRAY_BUFFER, pb, GL30.GL_STATIC_DRAW)
    // position attribute
    GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 6 * 4, 0L)
    GL30.glEnableVertexAttribArray(0)
    // normal attribute
    GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * 4, 3 * 4)
    GL20.glEnableVertexAttribArray(1)
  }

  fun initVisibilityBuffer() {
    // setup vertex visibility buffer
    val visVbo = GL30.glGenBuffers()
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, visVbo)
    GL30.glBindVertexArray(0)
  }

  var matrixBuffer: FloatBuffer = glBufferUtils.createFloatBuffer(16)
}
