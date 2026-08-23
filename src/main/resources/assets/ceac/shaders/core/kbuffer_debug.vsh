#version 430

out vec2 texCoord;

void main() {
    vec2 positions[3] = vec2[](
            vec2(-1.0, -1.0),
            vec2( 3.0, -1.0),
            vec2(-1.0,  3.0)
    );

    vec2 pos = positions[gl_VertexID];

    gl_Position = vec4(pos, 0.0, 1.0);
    texCoord = pos * 0.5 + 0.5;
}