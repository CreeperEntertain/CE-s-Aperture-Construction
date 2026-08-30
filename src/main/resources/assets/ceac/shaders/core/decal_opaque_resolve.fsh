#version 430

const float MAX_ANGLE = radians(45.0);
const float FADE_ANGLE = radians(35.0);
const float MIN_DOT = cos(MAX_ANGLE);
const float FADE_DOT = cos(FADE_ANGLE);

struct DecalData {
    vec4 origin;
    vec4 normal;
    vec4 volumeAndRotation;
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

bool findCell(ivec3 cell, out uint offset, out uint count) {
    int low = 0;
    int high = int(CellCount) - 1;

    while (low <= high) {
        int middle = (low + high) >> 1;
        CellData e = cells[middle];

        if (e.cell.x == cell.x && e.cell.y == cell.y && e.cell.z == cell.z) {
            offset = uint(e.data.x);
            count = uint(e.data.y);
            return true;
        }

        if (
            e.cell.x < cell.x ||
            (e.cell.x == cell.x && e.cell.y < cell.y) ||
            (e.cell.x == cell.x && e.cell.y == cell.y && e.cell.z < cell.z)
        )
            low = middle + 1;
        else
            high = middle - 1;
    }
    return false;
}

vec3 worldPos(vec2 uv, float depth) {
    vec4 p = InvProjMat * vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);

    p /= p.w;

    return IViewRotMat * p.xyz + CameraPosition;
}

float angleFade(vec3 surfaceNormal, vec3 decalNormal) {
    float d = dot(surfaceNormal, decalNormal);

    if (d <= MIN_DOT)
        return 0.0;

    return smoothstep(MIN_DOT, FADE_DOT, d);
}

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
    vec2 uv = gl_FragCoord.xy / ScreenSize.xy;

    if (texture(Coverage, uv).r >= 1.0)
        discard;

    float depth = texture(Sampler1, uv).r;

    if (depth >= 1.0)
        discard;

    vec3 surfaceWorld = worldPos(uv, depth);

    vec3 surfaceNormal = normalize(cross(dFdx(surfaceWorld), dFdy(surfaceWorld)));

    ivec3 cell = ivec3(floor(surfaceWorld / CellSize));

    uint decalOffset;
    uint decalCount;

    if (!findCell(cell, decalOffset, decalCount))
        discard;

    for (uint j = 0u; j < decalCount; ++j) {
        DecalData decal = decals[decalIndices[decalOffset + j]];

        vec3 decalNormal = normalize(decal.normal.xyz);

        float fade = angleFade(surfaceNormal, decalNormal);

        if (fade <= 0.0)
            continue;

        vec3 tangent;
        vec3 bitangent;

        getDecalBasis(
                decalNormal,
                decal.volumeAndRotation.w,
                tangent,
                bitangent
        );

        vec3 r = surfaceWorld - decal.origin.xyz;

        vec3 local = vec3(
                dot(r, tangent),
                dot(r, bitangent),
                dot(r, decalNormal)
        );

        vec3 halfVolume = decal.volumeAndRotation.xyz * 0.5;

        if (abs(local.x) > halfVolume.x ||
            abs(local.y) > halfVolume.y ||
            abs(local.z) > halfVolume.z)
            continue;

        float u = local.x / decal.volumeAndRotation.x + 0.5;
        float v = 0.5 - local.y / decal.volumeAndRotation.y;

        if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0)
            continue;

        vec4 color = texture(Sampler0, vec2(u, v));

        if (color.a <= 0.01)
            continue;

        color.a *= fade;

        vec2 lightCoords = texture(LightmapCoords, uv).rg;

        vec2 lightUV = clamp(lightCoords, vec2(0.5 / 16.0), vec2(15.5 / 16.0));

        color.rgb *= texture(Lightmap, lightUV).rgb;

        fragColor = color;
        return;
    }
    discard;
}