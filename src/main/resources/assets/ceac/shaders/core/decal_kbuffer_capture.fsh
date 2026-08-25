#version 430

const uint LAYERS = 4u;

const float VOLUME_SIZE = 1.1;
const float HALF_VOLUME = VOLUME_SIZE / 2.0;

struct DecalData {
    vec4 origin;
    vec4 tangent;
    vec4 bitangent;
};

layout(std430, binding = 0) readonly buffer FragmentBuffer {
    uint fragments[];
};

layout(std430, binding = 1) readonly buffer LockBuffer {
    uint locks[];
};

layout(std430, binding = 2) readonly buffer DecalBuffer {
    DecalData decals[];
};

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float DecalCount;
uniform vec4 ScreenSize;

uniform mat4 InvProjMat;
uniform mat3 IViewRotMat;

out vec4 fragColor;

void main() {
    fragColor = vec4(0.0);

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint width = uint(ScreenSize.x);
    uint pixelCount = uint(ScreenSize.x * ScreenSize.y);

    uint pixelIndex = uint(pixel.y) * width + uint(pixel.x);

    uint count = locks[pixelIndex];

    if (count == 0u)
        return;

    if (count > LAYERS)
        count = LAYERS;

    vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;

    float opaqueDepth = texture(Sampler1, screenUV).r;

    for (uint i = 0u; i < count; ++i) {
        uint offset = i * pixelCount * 2u + pixelIndex * 2u;

        uint depthBits = fragments[offset];
        float depth = uintBitsToFloat(depthBits);

        if (depth >= opaqueDepth)
            continue;

        vec4 clipPosition = vec4(
                screenUV * 2.0 - 1.0,
                depth * 2.0 - 1.0,
                1.0
        );

        vec4 viewPosition = InvProjMat * clipPosition;
        viewPosition /= viewPosition.w;

        vec3 surfaceView = viewPosition.xyz;
        vec3 surfaceCameraRelative = IViewRotMat * surfaceView;

        for (uint decalIndex = 0u; decalIndex < uint(DecalCount);++decalIndex) {

            DecalData decal = decals[decalIndex];
            vec3 surfaceDecalRelative = surfaceCameraRelative - decal.origin.xyz;

            if (
                abs(surfaceDecalRelative.x) > HALF_VOLUME ||
                abs(surfaceDecalRelative.y) > HALF_VOLUME ||
                abs(surfaceDecalRelative.z) > HALF_VOLUME
            )
                continue;

            float u = dot(surfaceDecalRelative, decal.tangent.xyz);
            float v = dot(surfaceDecalRelative, decal.bitangent.xyz);

            u = u / VOLUME_SIZE + 0.5;
            v = 1.0 - (v / VOLUME_SIZE + 0.5);

            if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0)
                continue;

            vec4 decalColor = texture(Sampler0, vec2(u, v));

            if (decalColor.a <= 0.01)
                continue;

            fragColor = decalColor;
            gl_FragDepth = max(0.0, depth - 1e-5);
            return;
        }
    }
    discard;
}