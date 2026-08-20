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
    // ------------------------------------------------------------
    // Reconstruct the actual scene surface position from depth.
    // ------------------------------------------------------------

    vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;

    float sceneDepth = texture(Sampler1, screenUV).r;

    vec4 clipPosition = vec4(
            screenUV * 2.0 - 1.0,
            sceneDepth * 2.0 - 1.0,
            1.0
    );

    vec4 viewPosition = InvProjMat * clipPosition;
    viewPosition /= viewPosition.w;

    vec3 surfaceView = viewPosition.xyz;

    // Convert from view space back into camera-relative world space.
    vec3 surfaceCameraRelative =
    IViewRotMat * surfaceView;

    // Position of the actual surface relative to the decal origin.
    vec3 surfaceDecalRelative =
    surfaceCameraRelative - DecalOriginRelative;


    // ------------------------------------------------------------
    // Make sure the surface is actually inside the decal volume.
    // ------------------------------------------------------------

    const float halfVolume = 1.1 / 2.0;

    if (abs(surfaceDecalRelative.x) > halfVolume ||
        abs(surfaceDecalRelative.y) > halfVolume ||
        abs(surfaceDecalRelative.z) > halfVolume)
    discard;


    // ------------------------------------------------------------
    // Construct a tangent basis for the projection plane.
    // ------------------------------------------------------------

    vec3 normal = normalize(DecalNormal);

    vec3 reference;

    if (abs(normal.y) < 0.999)
    reference = vec3(0.0, 1.0, 0.0);
    else
    reference = vec3(1.0, 0.0, 0.0);

    vec3 tangent = normalize(cross(reference, normal));
    vec3 bitangent = normalize(cross(normal, tangent));


    // ------------------------------------------------------------
    // Project the actual surface position onto the decal plane.
    // ------------------------------------------------------------

    float u = dot(surfaceDecalRelative, tangent);
    float v = dot(surfaceDecalRelative, bitangent);

    u = u / 1.1 + 0.5;
    v = 1.0 - (v / 1.1 + 0.5);

    if (u < 0.0 || u > 1.0 ||
        v < 0.0 || v > 1.0)
    discard;


    // ------------------------------------------------------------
    // Sample the decal.
    // ------------------------------------------------------------

    vec4 decal = texture(Sampler0, vec2(u, v));

    if (decal.a <= 0.01)
    discard;

    fragColor = decal * vertexColor;
}

/*
#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec3 decalPosition;
in vec4 vertexColor;

uniform vec3 DecalNormal;

out vec4 fragColor;

void main() {
    vec3 normal = normalize(DecalNormal);

    vec3 reference;

    if (abs(normal.y) < 0.999)
    reference = vec3(0.0, 1.0, 0.0);
    else
    reference = vec3(1.0, 0.0, 0.0);

    vec3 tangent = normalize(cross(reference, normal));
    vec3 bitangent = normalize(cross(normal, tangent));

    float u = dot(decalPosition, tangent);
    float v = dot(decalPosition, bitangent);

    u = u / 1.1 + 0.5;
    v = 1.0 - (v / 1.1 + 0.5);

    if (u < 0.0 || u > 1.0 ||
        v < 0.0 || v > 1.0)
    discard;

    float depth = dot(decalPosition, normal);

    if (depth > 0.0)
    discard;

    vec4 decal = texture(Sampler0, vec2(u, v));

    if (decal.a <= 0.01)
    discard;

    fragColor = decal * vertexColor;
}
*/