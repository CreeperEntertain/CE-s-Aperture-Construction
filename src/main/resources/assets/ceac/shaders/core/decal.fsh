#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec3 DecalNormal;
uniform vec3 DecalOriginRelative;
uniform vec4 ScreenSize;

uniform mat4 InvProjMat;
uniform mat3 IViewRotMat;

in vec3 decalPosition;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // Scene surface reconstruction

    vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;
    float sceneDepth = texture2D(Sampler1, screenUV).r;
    vec4 clipPosition = vec4(
            screenUV * 2.0 - 1.0,
            sceneDepth * 2.0 - 1.0,
            1.0
    );

    vec4 viewPosition = InvProjMat * clipPosition;
    viewPosition /= viewPosition.w;

    vec3 surfaceView = viewPosition.xyz;
    vec3 surfaceCameraRelative = IViewRotMat * surfaceView;
    vec3 surfaceDecalRelative = surfaceCameraRelative - DecalOriginRelative;


    // Volume content checks

    const float halfVolume = 1.1 / 2.0;

    if (abs(surfaceDecalRelative.x) > halfVolume ||
        abs(surfaceDecalRelative.y) > halfVolume ||
        abs(surfaceDecalRelative.z) > halfVolume)
        discard;


    // Projection plane tangent math

    vec3 normal = normalize(DecalNormal);
    vec3 reference;

    if (abs(normal.y) < 0.999)
        reference = vec3(0.0, 1.0, 0.0);
    else
        reference = vec3(1.0, 0.0, 0.0);

    vec3 tangent = normalize(cross(reference, normal));
    vec3 bitangent = normalize(cross(normal, tangent));


    // Decal plane projection

    float u = dot(surfaceDecalRelative, tangent);
    float v = dot(surfaceDecalRelative, bitangent);
    u = u / 1.1 + 0.5;
    v = 1.0 - (v / 1.1 + 0.5);

    if (u < 0.0 || u > 1.0 ||
        v < 0.0 || v > 1.0)
        discard;


    // Decal sampling

    vec4 decal = texture(Sampler0, vec2(u, v));

    if (decal.a <= 0.01)
        discard;

    fragColor = decal * vertexColor;
}
