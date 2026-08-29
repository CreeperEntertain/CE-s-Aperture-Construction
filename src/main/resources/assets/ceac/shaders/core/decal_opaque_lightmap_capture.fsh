#version 430

#moj_import <fog.glsl>

layout(rgba8, binding = 0) uniform writeonly image2D LightmapCoords;

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

    imageStore(LightmapCoords, pixel, vec4(lightCoords / 256.0, 0.0, 1.0));

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}