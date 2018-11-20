#version 330 core
out vec4 FragColor;
  
in vec3 vertexPos;  // the input variable from the vertex shader (same name and same type)
in vec3 normal;
in vec3 fragPos;

uniform vec3 lightPos;

void main() {
    // Ambient lightning
    vec3 lightColor = vec3(1.0, 1.0, 1.0);
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;
    // Diffuse lightning
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec3 objectColor;
    // COLORES UNIFORME
    if (vertexPos.y < 2 * 1000 / 6000.0) {
        objectColor =  vec3(0.0, 100.0 / 255, 0.0);
    } else if (vertexPos.y < 2 * 2000 / 6000.0) {
        objectColor =  vec3(0.0, 1.0, 0.0);
    } else if (vertexPos.y < 2 * 3000 / 6000.0) {
        objectColor =  vec3(1.0, 1.0, 0.0);
    } else if (vertexPos.y < 2 * 4000 / 6000.0) {
        objectColor =  vec3(139.0 / 255,69.0 / 255, 19.0 / 255);
    } else if (vertexPos.y < 2 * 5000 / 6000.0) {
        objectColor =  vec3(128.0 / 255, 128.0 / 255, 128.0 / 255);
    } else {
        objectColor =  vec3(1.0, 1.0, 1.0);
    }

    // GRADIENTE DE COLOR
    //objectColor = vertexPos;

    vec3 result =  (ambient + diffuse) * objectColor;
    FragColor = vec4(result, 1.0);
} 