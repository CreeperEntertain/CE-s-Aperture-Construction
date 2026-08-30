#version 430

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 DecalOriginRelative;
layout(location = 2) in vec3 DecalNormal;
layout(location = 3) in vec3 DecalVolume;
layout(location = 4) in float DecalRotation;

uniform mat4 ProjMat;
uniform mat4 DecalPoseMat;

void getDecalBasis(
        vec3 normal,
        float rotation,
        out vec3 tangent,
        out vec3 bitangent
) {
    normal = normalize(normal);

    vec3 reference = abs(normal.y) < 0.999
        ? vec3(0.0, 1.0, 0.0)
        : vec3(1.0, 0.0, 0.0);

    tangent = normalize(cross(reference, normal));
    bitangent = normalize(cross(normal, tangent));

    float angle = radians(22.5) * rotation;

    float c = cos(angle);
    float s = sin(angle);

    vec3 rotatedTangent = tangent * c + bitangent * s;
    vec3 rotatedBitangent = -tangent * s + bitangent * c;

    tangent = rotatedTangent;
    bitangent = rotatedBitangent;
}

void main() {
    vec3 tangent;
    vec3 bitangent;

    getDecalBasis(
            DecalNormal,
            DecalRotation,
            tangent,
            bitangent
    );

    vec3 normal = normalize(cross(tangent, bitangent));

    vec3 local = Position * DecalVolume;

    vec3 position = tangent * local.x + bitangent * local.y + normal * local.z;

    position = (DecalPoseMat * vec4(position, 0.0)).xyz;

    gl_Position = ProjMat * vec4(DecalOriginRelative + position, 1.0);
}