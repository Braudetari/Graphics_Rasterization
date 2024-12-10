package app_interface;

public enum ExerciseEnum  {
    EX_0___Starting_point                      ("Exercise 0  - Starting point"),
    EX_1___Lines_rasterization                 ("Exercise 1  - Lines rasterization"),
    EX_2___Triangles_rasterization             ("Exercise 2  - Triangles rasterization"),
    EX_3_1_Object_transformation___translation ("Exercise 3.1- Object transformation - translation"),
    EX_3_2_Object_transformation___scale       ("Exercise 3.2- Object transformation - scale"),
    EX_3_3_Object_transformation___4_objects   ("Exercise 3.3- Object transformation - 4 objects"),
    EX_4___Orthograpic_projection_and_viewport ("Exercise 4  - Orthograpic projection and viewport"),
    EX_5___lookat                              ("Exercise 5  - lookat"),
    EX_6___Perspective_projection              ("Exercise 6  - Perspective projection"),
    EX_7___Vertex_color_interpolation          ("Exercise 7  - Vertex color interpolation"),
    EX_8___Z_buffer                            ("Exercise 8  - Z buffer"),
    EX_9___Lighting                            ("Exercise 9  - Lighting"),
    EX_10__Texture                             ("Exercise 10 - Texture");

    private final String description;

    private ExerciseEnum(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
