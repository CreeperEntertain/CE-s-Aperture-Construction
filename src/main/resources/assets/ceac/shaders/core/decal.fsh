#version 150

uniform sampler2D Sampler1;

out vec4 fragColor;

void main() {
    float depth = texture(
            Sampler1,
            gl_FragCoord.xy / vec2(1920.0, 1080.0)
    ).r;

    fragColor = vec4(depth, depth, depth, 1.0);
}

/*
#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec3 decalPosition;
in vec4 vertexColor;

uniform vec3 DecalNormal;

out vec4 fragColor;

void main() {
    vec3 normal = normalize(DecalNormal);

    vec3 reference;

    if (abs(normal.y) < 0.999)
    reference = vec3(0.0, 1.0, 0.0);
    else
    reference = vec3(1.0, 0.0, 0.0);

    vec3 tangent = normalize(cross(reference, normal));
    vec3 bitangent = normalize(cross(normal, tangent));

    float u = dot(decalPosition, tangent);
    float v = dot(decalPosition, bitangent);

    u = u / 1.1 + 0.5;
    v = 1.0 - (v / 1.1 + 0.5);

    if (u < 0.0 || u > 1.0 ||
        v < 0.0 || v > 1.0)
    discard;

    float depth = dot(decalPosition, normal);

    if (depth > 0.0)
    discard;

    vec4 decal = texture(Sampler0, vec2(u, v));

    if (decal.a <= 0.01)
    discard;

    fragColor = decal * vertexColor;
}
*/