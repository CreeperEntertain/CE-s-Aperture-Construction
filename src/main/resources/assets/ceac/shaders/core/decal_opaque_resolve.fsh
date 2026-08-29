#version 430

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
uniform sampler2D Coverage;
uniform sampler2D LightmapCoords;
uniform sampler2D Lightmap;

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

void main() {
    vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;

    if (texture(Coverage, screenUV).r >= 1.0)
        discard;

    float sceneDepth = texture(Sampler1, screenUV).r;

    if (sceneDepth >= 1.0)
        discard;

    vec4 viewPosition = InvProjMat * vec4(
            screenUV * 2.0 - 1.0,
            sceneDepth * 2.0 - 1.0,
            1.0
    );

    viewPosition /= viewPosition.w;

    vec3 surfaceCameraRelative = IViewRotMat * viewPosition.xyz;
    vec3 surfaceWorld = surfaceCameraRelative + CameraPosition;

    ivec3 cell = ivec3(floor(surfaceWorld / CellSize));

    uint decalOffset;
    uint decalCount;

    if (!findCell(cell, decalOffset, decalCount))
        discard;

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

        vec2 lightCoords = texture(LightmapCoords, screenUV).rg;

        vec2 lightUV = clamp(lightCoords, vec2(0.5 / 16.0), vec2(15.5 / 16.0));

        vec4 lightColor = texture(Lightmap, lightUV);

        decalColor.rgb *= lightColor.rgb;

        fragColor = decalColor;
        return;
    }
    discard;
}