public class Main {
    public static void main(String[] args){
        System.out.println("ppap");
        Display display = new Display(1000,600,"asdf");
        RenderContext target = (RenderContext) display.GetFrameBuffer();
        Stars3D stars = new Stars3D(4096,64.0f,20.0f);
        Vertex minYvert = new Vertex(new Vector4f(-1,-1,0, 1),new Vector4f(1.0f, 0.0f, 0.0f, 0.0f));
        Vertex midYvert = new Vertex(new Vector4f(0,1,0, 1),new Vector4f(0.0f, 1.0f, 0.0f, 0.0f));
        Vertex maxYvert = new Vertex(new Vector4f(1,-1,0, 1),new Vector4f(0.0f, 0.0f, 1.0f, 0.0f));

        Matrix4f projection = new Matrix4f().InitPerspective((float)Math.toRadians(70.f),
                (float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f);

        float rotCounter = 0.0f;
        long previousTime = System.nanoTime();
        while(true)
        {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime)/1000000000.0);
            previousTime = currentTime;

            //stars.UpdateAndRender(target,delta);
            rotCounter += delta;
            Matrix4f translation = new Matrix4f().InitTranslation(0.0f,0.0f,3.0f);
            Matrix4f rotation = new Matrix4f().InitRotation(0.0f, rotCounter , 0.0f);
            Matrix4f transform = projection.Mul(translation.Mul(rotation));
            target.Clear((byte)0x00);

//            for(int j = 100 ; j < 200 ; j++){
//                target.DrawScanBuffer(j, 300 -j, 300+ j);
//            }
            target.FillTriangle(maxYvert.Transform(transform), midYvert.Transform(transform) , minYvert.Transform(transform));
            display.SwapBuffers();
        }
    }
}

