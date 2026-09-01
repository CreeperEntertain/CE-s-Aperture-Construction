#version 430

#moj_import <light.glsl>

const uint LAYERS = 4u;

const float MAX_ANGLE = radians(45.0);
const float FADE_ANGLE = radians(35.0);
const float MIN_DOT = cos(MAX_ANGLE);
const float FADE_DOT = cos(FADE_ANGLE);

struct DecalData {
    vec4 origin;
    vec4 normal;
    vec4 volumeAndRotation;
    vec4 textureBounds;
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
uniform sampler2D Lightmap;

uniform float DecalCount;
uniform float CellSize;
uniform float CellCount;
uniform vec4 ScreenSize;

uniform vec3 CameraPosition;

uniform mat4 InvProjMat;
uniform mat3 IViewRotMat;

out vec4 fragColor;

bool findCell(ivec3 cell, out uint offset, out uint count)
{
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

vec4 over(vec4 f, vec4 b) {
    return vec4(f.rgb * f.a + b.rgb * (1.0 - f.a), f.a + b.a * (1.0 - f.a));
}

vec3 worldPos(vec2 uv, float depth){
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
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint width = uint(ScreenSize.x);
    uint pixelCount = uint(ScreenSize.x * ScreenSize.y);

    uint pixelIndex = uint(pixel.y) * width + uint(pixel.x);
    uint layerStride = pixelCount * 3u;

    uint count = locks[pixelIndex];

    if (count == 0u)
        discard;

    if (count > LAYERS)
        count = LAYERS;

    vec2 uv = gl_FragCoord.xy / ScreenSize.xy;
    float opaqueDepth = texture(Sampler1, uv).r;

    vec4 layers[LAYERS];
    bool decalApplied = false;

    for (uint i = 0u; i < count; ++i) {
        uint offset = i * layerStride + pixelIndex * 3u;

        float depth = uintBitsToFloat(fragments[offset]);
        layers[i] = unpackUnorm4x8(fragments[offset + 1u]);

        if (decalApplied || depth >= opaqueDepth)
            continue;

        vec3 surfaceWorld = worldPos(uv, depth);

        vec3 surfaceNormal = normalize(cross(dFdx(surfaceWorld), dFdy(surfaceWorld)));

        if (dot(surfaceNormal, CameraPosition - surfaceWorld) < 0.0)
            surfaceNormal = -surfaceNormal;

        ivec3 cell = ivec3(floor(surfaceWorld / CellSize));

        uint decalOffset;
        uint decalCount;

        if (!findCell(cell, decalOffset, decalCount))
            continue;

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

            vec3 local = vec3(dot(r, tangent), dot(r, bitangent), dot(r, decalNormal));

            vec3 halfVolume = decal.volumeAndRotation.xyz * 0.5;

            if (abs(local.x) > halfVolume.x || abs(local.y) > halfVolume.y || abs(local.z) > halfVolume.z)
                continue;

            float u = local.x / decal.volumeAndRotation.x + 0.5;
            float v = 0.5 - local.y / decal.volumeAndRotation.y;

            if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0)
                continue;

            vec2 textureUV = mix(decal.textureBounds.xy, decal.textureBounds.zw, vec2(u, v));

            vec4 color = texture(Sampler0, textureUV);

            if (color.a <= 0.01)
                continue;

            color.a *= fade;

            uint lightPacked = fragments[offset + 2u];

            ivec2 lightCoords = ivec2(
                    int(lightPacked & 0xFFu),
                    int((lightPacked >> 8u) & 0xFFu)
            );

            color.rgb *= minecraft_sample_lightmap(Lightmap, lightCoords).rgb;

            layers[i] = over(color, layers[i]);
            decalApplied = true;
            break;
        }
    }
    if (!decalApplied)
        discard;

    vec4 result = vec4(0.0);

    for (int i = int(count) - 1; i >= 0; --i)
        result = over(layers[i], result);

    fragColor = result;
}