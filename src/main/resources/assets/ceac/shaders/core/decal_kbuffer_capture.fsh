#version 430

const uint LAYERS = 4u;
const uint EMPTY = 0xFFFFFFFFu;

const float VOLUME_SIZE = 1.1;
const float HALF_VOLUME = VOLUME_SIZE / 2.0;

layout(std430, binding = 0) readonly buffer FragmentBuffer {
    uint fragments[];
};

layout(std430, binding = 1) buffer LockBuffer {
    uint locks[];
};

uniform sampler2D Sampler0;

uniform vec3 DecalNormal;
uniform vec3 DecalOriginRelative;
uniform vec4 ScreenSize;

uniform mat4 InvProjMat;
uniform mat3 IViewRotMat;

out vec4 fragColor;

void main() {
    fragColor = vec4(0.0);

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint width = uint(ScreenSize.x);

    uint pixelIndex = uint(pixel.y) * width + uint(pixel.x);

    uint base = pixelIndex * LAYERS * 2u;

    vec3 normal = normalize(DecalNormal);
    vec3 reference;

    if (abs(normal.y) < 0.999)
        reference = vec3(0.0, 1.0, 0.0);
    else
        reference = vec3(1.0, 0.0, 0.0);

    vec3 tangent = normalize(cross(reference, normal));
    vec3 bitangent = normalize(cross(normal, tangent));

    for (uint i = 0u; i < LAYERS; i++) {
        uint offset = base + i * 2u;
        uint depthBits = fragments[offset];

        if (depthBits == EMPTY)
            continue;

        float depth = uintBitsToFloat(depthBits);

        // Reconstruct translucent surface position.
        vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;

        vec4 clipPosition = vec4(
                screenUV * 2.0 - 1.0,
                depth * 2.0 - 1.0,
                1.0
        );

        vec4 viewPosition = InvProjMat * clipPosition;

        viewPosition /= viewPosition.w;

        vec3 surfaceView = viewPosition.xyz;
        vec3 surfaceCameraRelative = IViewRotMat * surfaceView;
        vec3 surfaceDecalRelative = surfaceCameraRelative - DecalOriginRelative;

        if (abs(surfaceDecalRelative.x) > HALF_VOLUME ||
            abs(surfaceDecalRelative.y) > HALF_VOLUME ||
            abs(surfaceDecalRelative.z) > HALF_VOLUME)
            continue;

        float u = dot(surfaceDecalRelative, tangent);
        float v = dot(surfaceDecalRelative, bitangent);

        u = u / VOLUME_SIZE + 0.5;
        v = 1.0 - (v / VOLUME_SIZE + 0.5);

        if (u < 0.0 || u > 1.0 ||
            v < 0.0 || v > 1.0)
            continue;

        vec4 decal = texture(Sampler0, vec2(u, v));

        if (decal.a <= 0.01)
            continue;

        fragColor = decal;

        return;
    }
}