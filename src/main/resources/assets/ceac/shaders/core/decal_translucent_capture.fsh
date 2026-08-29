#version 430

#moj_import <fog.glsl>

const uint LAYERS = 4u;

layout(std430, binding = 0) buffer FragmentBuffer {
    uint fragments[];
};

layout(std430, binding = 1) buffer LockBuffer {
    uint locks[];
};

uniform vec4 ScreenSize;
uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord;
in vec2 lightCoord;

out vec4 fragColor;

void main() {
    if (!gl_FrontFacing)
    return;

    vec4 color = texture(Sampler0, texCoord) * vertexColor * ColorModulator;

    if (color.a <= 0.01)
        discard;

    color = clamp(color, 0.0, 1.0);
    color = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);

    fragColor = color;

    uint width = uint(ScreenSize.x);
    uint pixelCount = uint(ScreenSize.x * ScreenSize.y);

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint pixelIndex = uint(pixel.y) * width + uint(pixel.x);

    uint layer = atomicAdd(locks[pixelIndex], 1u);

    if (layer >= LAYERS)
        return;

    uint depth = floatBitsToUint(gl_FragCoord.z);

    uint lightX = uint(clamp(lightCoord.x * 256.0, 0.0, 255.0));
    uint lightY = uint(clamp(lightCoord.y * 256.0, 0.0, 255.0));

    uint packedLightmap = (lightX & 0xFFu) | ((lightY & 0xFFu) << 8u);

    uint layerStride = pixelCount * 3u;
    uint offset = layer * layerStride + pixelIndex * 3u;

    fragments[offset] = depth;
    fragments[offset + 1u] = packUnorm4x8(color);
    fragments[offset + 2u] = packedLightmap;
}