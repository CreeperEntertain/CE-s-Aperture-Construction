#version 150

uniform sampler2D Sampler0;

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / 16.0;

    vec4 decal = texture(Sampler0, uv);

    if (decal.a <= 0.01)
        discard;

    fragColor = decal * vertexColor;
}