#version 430

uniform sampler2D SceneDepth;
uniform sampler2D VisibleDepth;
uniform usampler2D Crossings;
uniform sampler2D PropDepth;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    float worldDepth = texelFetch(SceneDepth, pixel, 0).r;
    float propDepth = texelFetch(PropDepth, pixel, 0).r;

    if (worldDepth >= 1.0 || propDepth >= 1.0)
        discard;

    uint crossings = texelFetch(Crossings, pixel, 0).r;

    if (propDepth > worldDepth + 0.00001) {
        fragColor = vec4(1.0, 0.35, 0.02, 0.85);
        return;
    }

    if ((crossings & 1u) == 0u)
        discard;

    float stripe = mod(gl_FragCoord.x + gl_FragCoord.y, 8.0);
    if (stripe > 1.5)
        discard;

    fragColor = vec4(1.0, 0.78, 0.0, 0.92);
}