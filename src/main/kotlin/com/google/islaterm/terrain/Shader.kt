package com.google.islaterm.terrain

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*
import java.io.File
import java.nio.FloatBuffer

/**
 *  Stores information for both, vertex and fragment shader programs.
 *
 *  @param vertexPath
 *      Location of the vertex shader glsl file.
 *  @param fragmentPath
 *      Location of the fragment shader glsl file.
 *  @constructor
 *      Creates a vertex and fragment shaders, and links them to a shader program.
 *  @author Ignacio Slater Muñoz [ignacio.slater@ug.uchile.cl]
 */
class Shader(vertexPath: String, fragmentPath: String) {
    /** Id of the program linked with the shaders */
    var id: Int
        private set

    init {
        val vshaderCode = File(vertexPath).readText()
        val fshaderCode = File(fragmentPath).readText()
        // Creates the vertex shader
        val vshader = createShader(vshaderCode, GL_VERTEX_SHADER)
        // Creates the fragment shader
        val fshader = createShader(fshaderCode, GL_FRAGMENT_SHADER)
        // Creates shader program
        id = glCreateProgram()
        glAttachShader(id, vshader)
        glAttachShader(id, fshader)
        glLinkProgram(id)
        if (glGetProgrami(id, GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader Program Error: Linking failed.")
            System.err.println(glGetProgramInfoLog(id))
        }
        // delete the shaders as they're linked into our program now and no longer necessery
        glDeleteShader(vshader)
        glDeleteShader(fshader)
    }

    /** Use the shader. */
    fun use() {
        glUseProgram(id)
    }

    /** Closes the shader.  */
    fun close() {
        glUseProgram(0)
    }

    /**
     *  Specifies the value of a `mat4` uniform variable.
     *  This requires to first call the shader use function.
     *
     *  @param name
     *      Name of the uniform variable to be modified.
     *  @param transpose
     *      Whether to transpose the matrix as the values are loaded.
     *  @param value
     *      Values that will be used to update the variable.
     */
    fun setMatrix(name: String, transpose: Boolean, value: FloatBuffer) {
        if (GL11.glGetInteger(GL_CURRENT_PROGRAM) != id)
            throw Exception("Shader Error: Shader id does not match with current program id.")
        glUniformMatrix4fv(getUniformLocation(name), transpose, value)
    }

    /**
     *  Specifies the value of a `vec3` uniform variable.
     *  This requires to first call the shader use function.
     *
     *  @param name
     *      Name of the uniform variable to be modified.
     */
    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        if (GL11.glGetInteger(GL_CURRENT_PROGRAM) != id)
            throw Exception("Shader Error: Shader id does not match with current program id.")
        glUniform3fv(getUniformLocation(name), floatArrayOf(x, y, z))
    }

    /**
     *  Returns the location of a uniform variable.
     *
     *  @param name Name of the uniform variable.
     */
    private fun getUniformLocation(name: String) = glGetUniformLocation(id, name)

    /**
     *  Creates a shader from glsl source code.
     *  @param shaderCode
     *      String containing the shader source code.
     *  @param type
     *      The type of the shader to be created.
     *  @return
     *      The id of the created shader.
     */
    private fun createShader(shaderCode: String, type: Int): Int {
        val shaderId = glCreateShader(type)
        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader Error: Compilation failed.")
            System.err.println(glGetShaderInfoLog(shaderId))
        }
        return shaderId
    }
}