#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;

uniform vec3 DecalOriginRelative;

out vec3 decalPosition;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(DecalOriginRelative + Position, 1.0);
    decalPosition = IViewRotMat * Position;
    vertexColor = Color;
}