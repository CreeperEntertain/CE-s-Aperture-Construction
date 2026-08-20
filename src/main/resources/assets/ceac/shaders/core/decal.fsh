#version 150

uniform sampler2D Sampler2;

in vec4 vertexColor;
in vec2 lightmapUV;

out vec4 fragColor;

void main() {
    vec4 light = texture(Sampler2, lightmapUV);

    fragColor = vertexColor * light;
}