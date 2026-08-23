#version 430

const uint LAYERS = 4u;
const uint EMPTY = 0xFFFFFFFFu;

layout(std430, binding = 0) readonly buffer FragmentBuffer {
    uint depths[];
};

uniform vec4 ScreenSize;

out vec4 fragColor;

void main() {
    uint width = uint(ScreenSize.x);

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint pixelIndex =
    uint(pixel.y) * width +
    uint(pixel.x);

    uint base = pixelIndex * LAYERS;

    uint d0 = depths[base + 0u];
    uint d1 = depths[base + 1u];
    uint d2 = depths[base + 2u];
    uint d3 = depths[base + 3u];

    uint nearest = EMPTY;
    uint layer = 0u;

    if (d0 != EMPTY) {
        nearest = d0;
        layer = 0u;
    }

    if (d1 != EMPTY && (nearest == EMPTY || d1 < nearest)) {
        nearest = d1;
        layer = 1u;
    }

    if (d2 != EMPTY && (nearest == EMPTY || d2 < nearest)) {
        nearest = d2;
        layer = 2u;
    }

    if (d3 != EMPTY && (nearest == EMPTY || d3 < nearest)) {
        nearest = d3;
        layer = 3u;
    }

    if (nearest == EMPTY) {
        fragColor = vec4(0.0);
        return;
    }

    if (layer == 0u)
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    else if (layer == 1u)
        fragColor = vec4(0.0, 1.0, 0.0, 1.0);
    else if (layer == 2u)
        fragColor = vec4(0.0, 0.0, 1.0, 1.0);
    else
        fragColor = vec4(1.0, 1.0, 0.0, 1.0);
}