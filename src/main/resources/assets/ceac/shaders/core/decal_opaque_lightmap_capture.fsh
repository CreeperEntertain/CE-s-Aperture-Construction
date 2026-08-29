#version 430

#extension GL_ARB_fragment_shader_interlock : require

#moj_import <fog.glsl>

layout(pixel_interlock_unordered) in;

layout(r32ui, binding = 0) uniform coherent uimage2D LightmapDepth;
layout(rgba8, binding = 1) uniform writeonly image2D LightmapCoords;

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float AlphaCutoff;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec2 lightCoords;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    if (AlphaCutoff > 0.0 && color.a < AlphaCutoff)
        discard;

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint depthBits = floatBitsToUint(gl_FragCoord.z);

    beginInvocationInterlockARB();

    uint previousDepth = imageLoad(LightmapDepth, pixel).r;

    if (depthBits < previousDepth) {
        imageStore(LightmapDepth, pixel, uvec4(depthBits, 0u, 0u, 0u));
        imageStore(LightmapCoords, pixel, vec4(lightCoords / 256.0, 0.0, 1.0));
    }

    endInvocationInterlockARB();

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}