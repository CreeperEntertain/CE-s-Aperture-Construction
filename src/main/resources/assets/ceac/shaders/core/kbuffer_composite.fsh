#version 430

const uint LAYERS = 4u;
const uint EMPTY = 0xFFFFFFFFu;

layout(std430, binding = 0) readonly buffer FragmentBuffer {
    uint fragments[];
};

uniform vec4 ScreenSize;
uniform sampler2D Sampler0;

in vec2 screenUV;

out vec4 fragColor;

void main() {
    uint width = uint(ScreenSize.x);

    ivec2 pixel = ivec2(screenUV * ScreenSize.xy);

    uint pixelIndex =
    uint(pixel.y) * width +
    uint(pixel.x);

    uint base = pixelIndex * LAYERS * 2u;
    
    float opaqueDepth = texture(Sampler0, screenUV).r;
    vec4 result = vec4(0.0);

    for (int i = int(LAYERS) - 1; i >= 0; i--) {
        uint offset = base + uint(i) * 2u;
        if (fragments[offset] == EMPTY)
            continue;
        float translucentDepth = uintBitsToFloat(fragments[offset]);
        if (translucentDepth >= opaqueDepth)
            continue;
        vec4 color = unpackUnorm4x8(fragments[offset + 1u]);

        result.rgb = color.rgb * color.a + result.rgb * (1.0 - color.a);
        result.a = color.a + result.a * (1.0 - color.a);
    }
    fragColor = result;
}