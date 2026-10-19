#version 430

uniform sampler2D SceneDepth;
uniform sampler2D VisibleDepth;
uniform usampler2D Crossings;
uniform sampler2D PropDepth;
uniform usampler2D ClipMask;

out vec4 fragColor;

const float textureScale = 1.5;

int classifyPixel(ivec2 pixel) {
    float worldDepth = texelFetch(SceneDepth, pixel, 0).r;
    float propDepth = texelFetch(PropDepth, pixel, 0).r;

    if (worldDepth >= 1.0 || propDepth >= 1.0)
        return 0;

    if (propDepth > worldDepth + 0.00001) {
        if (texelFetch(ClipMask, pixel, 0).r != 0u)
            return 2;
        return 3;
    }

    uint crossings = texelFetch(Crossings, pixel, 0).r;

    if ((crossings & 1u) != 0u)
        return 1;
    return 0;
}

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);
    ivec2 size = textureSize(SceneDepth, 0);

    int effectClass = classifyPixel(pixel);
    bool boundary = false;
    bool higherPriorityNeighbor = false;

    if (effectClass != 0) {
        for (int y = -1; y <= 1; y++)
        for (int x = -1; x <= 1; x++) {
            if (x == 0 && y == 0)
            continue;

            ivec2 neighborPixel = clamp(
                    pixel + ivec2(x, y),
                    ivec2(0),
                    size - ivec2(1)
            );

            int neighborClass = classifyPixel(neighborPixel);

            if (neighborClass == effectClass)
                continue;

            if (neighborClass == 0 || effectClass < neighborClass)
                boundary = true;
            else
                higherPriorityNeighbor = true;
        }
    }

    if (boundary && !higherPriorityNeighbor) {
        if (effectClass == 1) {
            fragColor = vec4(1.0, 0.78, 0.0, 1.0);
            return;
        }
        if (effectClass == 2) {
            fragColor = vec4(1.0, 0.35, 0.02, 1.0);
            return;
        }
        if (effectClass == 3) {
            fragColor = vec4(0.2, 0.2, 0.2, 1.0);
            return;
        }
    }

    float worldDepth = texelFetch(SceneDepth, pixel, 0).r;
    float propDepth = texelFetch(PropDepth, pixel, 0).r;

    if (worldDepth >= 1.0 || propDepth >= 1.0)
        discard;

    uint crossings = texelFetch(Crossings, pixel, 0).r;

    if (propDepth > worldDepth + 0.00001) {
        float stripe = mod(gl_FragCoord.x + gl_FragCoord.y, 10.0 * textureScale);
        float cuts = mod(gl_FragCoord.x - gl_FragCoord.y, 15.0 * textureScale);
        if (stripe > 1.5 * textureScale || cuts < 4.0 * textureScale)
            discard;

        if (texelFetch(ClipMask, pixel, 0).r != 0u)
            fragColor = vec4(1.0, 0.35, 0.02, 0.5);
        else
            fragColor = vec4(0.5, 0.5, 0.5, 0.5);
        return;
    }

    if ((crossings & 1u) == 0u)
        discard;

    float stripe = mod(gl_FragCoord.x + gl_FragCoord.y, 10.0 * textureScale);
    float cuts = mod(gl_FragCoord.x - gl_FragCoord.y, 15.0 * textureScale);
    if (stripe > 1.5 * textureScale || cuts < 4.0 * textureScale)
        discard;

    fragColor = vec4(1.0, 0.78, 0.0, 0.5);
}