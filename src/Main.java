import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("ppap");
        Display display = new Display(1000,600,"asdf");
        RenderContext target = (RenderContext) display.GetFrameBuffer();

        Bitmap texture = new Bitmap("./res/simpbricks.png");
        Mesh mesh = new Mesh("./res/monkey0.obj");

        Vertex minYVert = new Vertex(new Vector4f(-1, -1, 0, 1),
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
        Vertex midYVert = new Vertex(new Vector4f(0, 1, 0, 1),
                new Vector4f(0.5f, 1.0f, 0.0f, 0.0f));
        Vertex maxYVert = new Vertex(new Vector4f(1, -1, 0, 1),
                new Vector4f(1.0f, 0.0f, 0.0f, 0.0f));

        Matrix4f projection = new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
                (float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f);

        float rotCounter = 0.0f;
        long previousTime = System.nanoTime();
        while(true)
        {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime)/1000000000.0);
            previousTime = currentTime;

            //stars.UpdateAndRender(target, delta);

            rotCounter += delta;
            Matrix4f translation = new Matrix4f().InitTranslation(0.0f, 0.0f, 3.0f);
            Matrix4f rotation = new Matrix4f().InitRotation(rotCounter, rotCounter, rotCounter);
            Matrix4f transform = projection.Mul(translation.Mul(rotation));

            target.Clear((byte)0x00);
            target.DrawMesh(mesh,transform,texture);
//            target.FillTriangle(maxYVert.Transform(transform),
//                    midYVert.Transform(transform), minYVert.Transform(transform),
//                    texture);

            display.SwapBuffers();
        }
    }
}

