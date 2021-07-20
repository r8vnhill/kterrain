package cl.ravenhill.kterrain

import org.joml.Vector3f
import java.util.*
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min

class Heightmap(detail: Int) {
    private val size: Int = (Math.pow(2.0, detail.toDouble()) + 1).toInt()
    private val map = Array(size) { FloatArray(size) { 0f } }
    private val r = Random()
    private val triangles = mutableListOf<Triangle>()
    /** Map that links a vertex with the triangles it is asociated whith.    */
    private val adjacencyMap = hashMapOf<Vector3f, MutableList<Triangle>>()
    /** Map that links a vertex id with a 3D vector.    */
    private val vertexMap = hashMapOf<String, Vector3f>()
    private val floorScale = log(size.toFloat(), 4f)
    private val upScale = 2

    /** List of the vertices forming the heightmap mesh.    */
    val vertices = mutableListOf<Float>()

    /** Represents a triangle and its surface normal.   */
    private inner class Triangle(v1: Vector3f, v2: Vector3f, v3: Vector3f) {
        /** Normal vector to the triangle.  */
        val normal = Vector3f()
        /** Vertices of the triangle.   */
        val vertices = listOf(v1, v2, v3)

        init {
            // Checks if the vertices has been used before.
            if (!adjacencyMap.containsKey(v1))
                adjacencyMap[v1] = mutableListOf()
            if (!adjacencyMap.containsKey(v2))
                adjacencyMap[v2] = mutableListOf()
            if (!adjacencyMap.containsKey(v3))
                adjacencyMap[v3] = mutableListOf()
            // Adds this triangle as adjacent to it's vertices.
            adjacencyMap[v1]!!.add(this)
            adjacencyMap[v2]!!.add(this)
            adjacencyMap[v3]!!.add(this)
            // Calculates triangle normal
            val edge1 = Vector3f()
            val edge2 = Vector3f()
            v2.sub(v1, edge1)
            v3.sub(v1, edge2)
            edge1.cross(edge2, normal)
            normal.normalize()
        }
    }

    fun create() {
        println("Generating random com.google.islaterm.terrain...")
        println("Heightmap dimensions: $size x $size (${size * size} vertices).")
        // Sets initial corner values
        map[0][0] = r.nextFloat()
        map[size - 1][0] = r.nextFloat()
        map[size - 1][size - 1] = r.nextFloat()
        map[0][size - 1] = r.nextFloat()

        divide(0, 0, size - 1, size - 1)
        mapToTriangles()
        setVertices()
    }

    private fun divide(x0: Int, y0: Int, x1: Int, y1: Int, spread: Float = 1f) {
        val midX = (x0 + x1) / 2
        val midY = (y0 + y1) / 2
        if ((x1 - x0) <= 1 || (y1 - y0) <= 1) return

        map[midX][y0] = bound((map[x0][y0] + map[x1][y0]) / 2 + (r.nextFloat() - 0.5f) * spread)
        map[x0][midY] = bound((map[x0][y0] + map[x0][y1]) / 2 + (r.nextFloat() - 0.5f) * spread)
        map[midX][y1] = bound((map[x0][y1] + map[x1][y1]) / 2 + (r.nextFloat() - 0.5f) * spread)
        map[x1][midY] = bound((map[x1][y0] + map[x1][y1]) / 2 + (r.nextFloat() - 0.5f) * spread)
        map[midX][midY] = bound((map[x0][y0] + map[x0][y1] + map[x1][y0] + map[x1][y1]) / 4 + (r.nextFloat() - 0.5f) * spread)

        divide(x0, y0, midX, midY, spread * 0.75f)
        divide(midX, y0, x1, midY, spread * 0.75f)
        divide(x0, midY, midX, y1, spread * 0.75f)
        divide(midX, midY, x1, y1, spread * 0.75f)
    }

    /** Creates an array of triangles from the map values.  */
    private fun mapToTriangles() {
        println("Generating triangles...")
        for (i in 0 until map.size - 1)
            for (j in 0 until map.size - 1) {
                // Set or get the vertices of the triangles.
                if (!vertexMap.containsKey("v($i,$j)"))
                    vertexMap["v($i,$j)"] = Vector3f(i / floorScale, map[i][j] * upScale, j / floorScale)
                val v1 = vertexMap["v($i,$j)"]
                if (!vertexMap.containsKey("v($i,${j + 1})"))
                    vertexMap["v($i,${j + 1})"] = Vector3f(i / floorScale, map[i][j + 1] * upScale,
                            (j + 1) / floorScale)
                val v2 = vertexMap["v($i,${j + 1})"]
                if (!vertexMap.containsKey("v(${i + 1},$j)"))
                    vertexMap["v(${i + 1},$j)"] = Vector3f((i + 1) / floorScale, map[i + 1][j] * upScale,
                            j / floorScale)
                val v3 = vertexMap["v(${i + 1},$j)"]
                if (!vertexMap.containsKey("v(${i + 1},${j + 1})"))
                    vertexMap["v(${i + 1},${j + 1})"] = Vector3f((i + 1) / floorScale,
                            map[i + 1][j + 1] * upScale, (j + 1) / floorScale)
                val v4 = vertexMap["v(${i + 1},${j + 1})"]
                // Creates and adds triangles to the mesh
                triangles.add(Triangle(v1!!, v2!!, v3!!))
                triangles.add(Triangle(v3, v2, v4!!))
            }
    }

    private fun bound(n: Float, lowerBound: Float = 0f, upperBound: Float = 1f) = max(lowerBound, min(n, upperBound))

    /** Set up vertex.  */
    private fun setVertices() {
        println("Setting vertex info...")
        for (triangle in triangles) {
            for (vertex in triangle.vertices) {
                // Computes the vertex normal from it's adjacent faces.
                val vertexNormal = Vector3f()
                var i = 0f
                for (face in adjacencyMap[vertex]!!) {
                    vertexNormal.add(face.normal)
                    i++
                }
                vertexNormal.div(i)

                vertices.add(vertex.x)
                vertices.add(vertex.y)
                vertices.add(vertex.z)
                vertices.add(vertexNormal.x)
                vertices.add(vertexNormal.y)
                vertices.add(vertexNormal.z)
            }
        }
    }
}