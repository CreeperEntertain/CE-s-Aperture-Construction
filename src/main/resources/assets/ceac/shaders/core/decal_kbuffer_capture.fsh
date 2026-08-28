#version 430

const uint LAYERS = 4u;

const float VOLUME_SIZE = 1.1;
const float HALF_VOLUME = VOLUME_SIZE / 2.0;
const float INV_VOLUME_SIZE = 1.0 / VOLUME_SIZE;

struct DecalData {
    vec4 origin;
    vec4 tangent;
    vec4 bitangent;
};

struct CellData {
    ivec4 cell;
    ivec4 data;
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

layout(std430, binding = 3) readonly buffer CellBuffer {
    CellData cells[];
};

layout(std430, binding = 4) readonly buffer DecalIndexBuffer {
    uint decalIndices[];
};

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float DecalCount;
uniform float CellSize;
uniform float CellCount;
uniform vec4 ScreenSize;

uniform vec3 CameraPosition;

uniform mat4 InvProjMat;
uniform mat3 IViewRotMat;

out vec4 fragColor;

bool findCell(
        ivec3 cell,
        out uint offset,
        out uint count
) {
    int low = 0;
    int high = int(CellCount) - 1;

    while (low <= high) {
        int middle = (low + high) >> 1;

        CellData entry = cells[middle];

        if (
            entry.cell.x == cell.x &&
            entry.cell.y == cell.y &&
            entry.cell.z == cell.z
        ) {
            offset = uint(entry.data.x);
            count = uint(entry.data.y);
            return true;
        }

        if (
            entry.cell.x < cell.x ||
            (
                entry.cell.x == cell.x &&
                entry.cell.y < cell.y
            ) ||
            (
                entry.cell.x == cell.x &&
                entry.cell.y == cell.y &&
                entry.cell.z < cell.z
            )
        ) {
            low = middle + 1;
        } else {
            high = middle - 1;
        }
    }

    return false;
}

vec4 over(
        vec4 foreground,
        vec4 background
) {
    return vec4(
            foreground.rgb * foreground.a + background.rgb * (1.0 - foreground.a),
            foreground.a + background.a * (1.0 - foreground.a)
    );
}

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint width = uint(ScreenSize.x);
    uint pixelCount = uint(ScreenSize.x * ScreenSize.y);

    uint pixelIndex = uint(pixel.y) * width + uint(pixel.x);
    uint layerStride = pixelCount * 2u;

    uint count = locks[pixelIndex];

    if (count == 0u)
        discard;

    if (count > LAYERS)
        count = LAYERS;

    vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;

    float opaqueDepth = texture(Sampler1, screenUV).r;

    vec4 layers[LAYERS];

    bool decalApplied = false;

    for (uint i = 0u; i < count; ++i) {
        uint offset = i * layerStride + pixelIndex * 2u;

        float depth = uintBitsToFloat(fragments[offset]);

        layers[i] = unpackUnorm4x8(fragments[offset + 1u]);

        if (decalApplied)
            continue;

        if (depth >= opaqueDepth)
            continue;

        vec2 clipXY = screenUV * 2.0 - 1.0;

        vec4 viewPosition = InvProjMat * vec4(
                clipXY,
                depth * 2.0 - 1.0,
                1.0
        );

        viewPosition /= viewPosition.w;

        vec3 surfaceCameraRelative = IViewRotMat * viewPosition.xyz;

        vec3 surfaceWorld = surfaceCameraRelative + CameraPosition;

        ivec3 cell = ivec3(floor(surfaceWorld / CellSize));

        uint decalOffset;
        uint decalCount;

        if (!findCell(cell, decalOffset, decalCount))
            continue;

        for (uint j = 0u; j < decalCount; ++j) {
            uint decalIndex = decalIndices[decalOffset + j];

            DecalData decal = decals[decalIndex];
            vec3 surfaceDecalRelative = surfaceWorld - decal.origin.xyz;

            if (
                abs(surfaceDecalRelative.x) > HALF_VOLUME ||
                abs(surfaceDecalRelative.y) > HALF_VOLUME ||
                abs(surfaceDecalRelative.z) > HALF_VOLUME
            )
                continue;

            float u = dot(surfaceDecalRelative, decal.tangent.xyz) * INV_VOLUME_SIZE + 0.5;

            float v = 0.5 - dot(surfaceDecalRelative, decal.bitangent.xyz) * INV_VOLUME_SIZE;

            if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0)
                continue;

            vec4 decalColor = texture(Sampler0, vec2(u, v));

            if (decalColor.a <= 0.01)
                continue;

            layers[i] = over(decalColor, layers[i]);

            decalApplied = true;
            break;
        }
    }
    if (!decalApplied)
        discard;

    vec4 result = vec4(0.0);

    // K-buffer is nearest > farthest
    // Thus, inverted composite
    for (int i = int(count) - 1; i >= 0; --i)
        result = over(layers[i], result);

    fragColor = result;
}