#version 430

uniform sampler2D SceneDepth;
uniform sampler2D VisibleDepth;
uniform usampler2D Crossings;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    float worldDepth = texelFetch(SceneDepth, pixel, 0).r;
    float visibleDepth = texelFetch(VisibleDepth, pixel, 0).r;

    if (worldDepth >= 1.0)
        discard;
    if (abs(visibleDepth - worldDepth) > 0.00001)
        discard;

    uint crossings = texelFetch(Crossings, pixel, 0).r;
    if ((crossings & 1u) == 0u)
        discard;

    float stripe = mod(gl_FragCoord.x + gl_FragCoord.y, 8.0);
    if (stripe > 1.5)
        discard;

    fragColor = vec4(1.0, 0.78, 0.0, 0.92);
}