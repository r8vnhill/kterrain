#version 330 core
layout (location = 0) in vec3 aPos; // the position variable has attribute position 0
layout (location = 1) in vec3 aNormal;  // the color variable has attribute position 1

out vec3 vertexPos; // specify a position output to the fragment shader
out vec3 normal;
out vec3 fragPos;
// out vec3 vertexPosition;

uniform mat4 projMatrix;    // projection matrix
uniform mat4 modelMatrix;   // model matrix
uniform mat4 viewMatrix;    // view matrix

void main() {
    gl_Position = projMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0f);
    normal = aNormal;
    fragPos = vec3(modelMatrix * vec4(aPos, 1.0));

    // COLORES UNIFORMES
    vertexPos = aPos;

    // GRADIENTE DE COLOR
    /*if (aPos.y < 2 * 1000 / 6000.0) {
        vertexPos = vec3(0.0, 100.0 / 255, 0.0);
    } else if (aPos.y < 2 * 2000 / 6000.0) {
        vertexPos = vec3(0.0, 1.0, 0.0);
    } else if (aPos.y < 2 * 3000 / 6000.0) {
        vertexPos = vec3(1.0, 1.0, 0.0);
    } else if (aPos.y < 2 * 4000 / 6000.0) {
        vertexPos = vec3(139.0 / 255,69.0 / 255, 19.0 / 255);
    } else if (aPos.y < 2 * 5000 / 6000.0) {
        vertexPos = vec3(128.0 / 255, 128.0 / 255, 128.0 / 255);
    } else {
        vertexPos = vec3(1.0, 1.0, 1.0);
    }*/
}