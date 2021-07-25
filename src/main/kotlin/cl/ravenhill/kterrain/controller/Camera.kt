/**
 * "KTerrain" (c) by Ignacio Slater M.
 * "KTerrain" is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package cl.ravenhill.kterrain.controller

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * @author [Ignacio Slater Muñoz](mailto:islaterm@gmail.com)
 */
data class Camera(
  var lastX: Float = 400f,
  var lastY: Float = 300f,
  var pitch: Double = 0.0,
  var yaw: Double = 0.0
) {
  val position = Vector3f()

  val front = Vector3f()
  val up = Vector3f()
  var fov = 45f

  // Model-View-Projection matrices
  val projMatrix = Matrix4f()
  val viewMatrix = Matrix4f()
  val modelMatrix = Matrix4f()

  fun updateView() {
    viewMatrix.setLookAt(
      position.x, position.y, position.z,
      position.x + front.x, position.y + front.y, position.z + front.z,
      up.x, up.y, up.z
    )
  }

  fun updateProjection(fieldOfView: Double) {
    projMatrix.setPerspective(
      Math.toRadians(fieldOfView).toFloat(),
      GLFWController.window.width.toFloat() / GLFWController.window.height,
      0.01f,
      100.0f
    )
  }
}