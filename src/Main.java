public class Main {
    public static void main(String[] args){
        System.out.println("ppap");
        Display display = new Display(1000,600,"asdf");
        RenderContext target = (RenderContext) display.GetFrameBuffer();
        Bitmap texture = new Bitmap(32, 32);
        for(int j = 0; j < texture.GetHeight(); j++)
        {
            for(int i = 0; i < texture.GetWidth(); i++)
            {
                texture.DrawPixel(i, j,
                        (byte)(Math.random() * 255.0 + 0.5),
                        (byte)(Math.random() * 255.0 + 0.5),
                        (byte)(Math.random() * 255.0 + 0.5),
                        (byte)(Math.random() * 255.0 + 0.5));
            }
        }

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
            target.FillTriangle(maxYVert.Transform(transform),
                    midYVert.Transform(transform), minYVert.Transform(transform),
                    texture);

            display.SwapBuffers();
        }
    }
}

