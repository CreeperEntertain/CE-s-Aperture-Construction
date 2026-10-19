#version 430

uniform sampler2D SceneDepth;
uniform sampler2D VisibleDepth;
uniform usampler2D Crossings;
uniform sampler2D PropDepth;

out vec4 fragColor;

const float textureScale = 1.5;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

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