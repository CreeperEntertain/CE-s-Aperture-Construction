#version 430

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 DecalOriginRelative;
layout(location = 2) in vec3 DecalNormal;

uniform mat4 ProjMat;
uniform mat4 DecalPoseMat;

flat out vec3 decalOriginRelative;
flat out vec3 decalNormal;

void main() {
    vec3 origin = (DecalPoseMat * vec4(DecalOriginRelative, 1.0)).xyz;
    vec3 position = (DecalPoseMat * vec4(Position, 1.0)).xyz;

    gl_Position = ProjMat * vec4(origin + position, 1.0);

    decalOriginRelative = DecalOriginRelative;
    decalNormal = DecalNormal;
}