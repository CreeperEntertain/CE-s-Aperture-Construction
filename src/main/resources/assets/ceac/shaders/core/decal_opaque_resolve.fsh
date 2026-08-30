#version 430

const float VOLUME_SIZE = 1.1;
const float HALF_VOLUME = VOLUME_SIZE / 2.0;
const float INV_VOLUME_SIZE = 1.0 / VOLUME_SIZE;

const float MAX_ANGLE = radians(45.0);
const float FADE_ANGLE = radians(35.0);
const float MIN_DOT = cos(MAX_ANGLE);
const float FADE_DOT = cos(FADE_ANGLE);

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

        vec3 decalNormal = normalize(cross(decal.tangent.xyz, decal.bitangent.xyz));

        float fade = angleFade(surfaceNormal, decalNormal);

        if (fade <= 0.0)
            continue;

        vec3 r = surfaceWorld - decal.origin.xyz;

        if (abs(r.x) > HALF_VOLUME ||
            abs(r.y) > HALF_VOLUME ||
            abs(r.z) > HALF_VOLUME)
            continue;

        float u = dot(r, decal.tangent.xyz) * INV_VOLUME_SIZE + 0.5;
        float v = 0.5 - dot(r, decal.bitangent.xyz) * INV_VOLUME_SIZE;

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