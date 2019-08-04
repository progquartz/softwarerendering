public class RenderContext extends Bitmap
{

    public RenderContext(int width, int height){
        super(width, height);
    }

    public void FillTriangle(Vertex v1 , Vertex v2, Vertex v3, Bitmap texture){
        Matrix4f screenSpaceTransform = new Matrix4f().InitScreenSpaceTransform(GetWidth()/2 , GetHeight()/2);
        Vertex minYvert = v1.Transform(screenSpaceTransform).PerspectiveDivide();
        Vertex midYvert = v2.Transform(screenSpaceTransform).PerspectiveDivide();
        Vertex maxYvert = v3.Transform(screenSpaceTransform).PerspectiveDivide();

        if(maxYvert.GetY() < midYvert.GetY()){
            Vertex temp = maxYvert;
            maxYvert = midYvert;
            midYvert = temp;
        }

        if(midYvert.GetY() < minYvert.GetY()){
            Vertex temp = midYvert;
            midYvert = minYvert;
            minYvert = temp;
        }

        if(maxYvert.GetY() < midYvert.GetY()){
            Vertex temp = maxYvert;
            maxYvert = midYvert;
            midYvert = temp;
        }
        ScanTriangle(minYvert, midYvert, maxYvert, minYvert.TriangleAreaTimesTwo(maxYvert, midYvert) >= 0, texture);

//        float area = minYvert.TriangleAreaTimesTwo(maxYvert,midYvert);
//        int handedness = area >= 0 ? 1 : 0;
//
//        ScanConvertTriangle(minYvert , midYvert, maxYvert , handedness);
//        FillShape((int)Math.ceil(minYvert.GetY()),(int)Math.ceil(maxYvert.GetY()));
    }

    private void ScanTriangle(Vertex minYVert, Vertex midYVert, Vertex maxYVert, boolean handedness, Bitmap texture)
    {
        Gradients gradients = new Gradients(minYVert, midYVert, maxYVert);
        Edge topToBottom    = new Edge(gradients, minYVert, maxYVert, 0);
        Edge topToMiddle    = new Edge(gradients, minYVert, midYVert, 0);
        Edge middleToBottom = new Edge(gradients, midYVert, maxYVert, 1);

        ScanEdges(gradients, topToBottom, topToMiddle, handedness, texture);
        ScanEdges(gradients, topToBottom, middleToBottom, handedness, texture);;
    }

    private void ScanEdges(Gradients gradients, Edge a, Edge b, boolean handedness, Bitmap texture)
    {
        // if the handedness is 0, then the top to the bottom is left edge.
        Edge left = a;
        Edge right = b;
        if(handedness)
        {
            Edge temp = left;
            left = right;
            right = temp;
        }

        int yStart = b.GetYStart();
        int yEnd   = b.GetYEnd();
        for(int j = yStart; j < yEnd; j++)
        {
            DrawScanLine(gradients, left, right, j, texture);
            left.Step();
            right.Step();
        }
    }

    private void DrawScanLine(Gradients gradients, Edge left, Edge right, int j , Bitmap texture)
    {
        int xMin = (int)Math.ceil(left.GetX());
        int xMax = (int)Math.ceil(right.GetX());
        float xPrestep = xMin - left.GetX();

        float texCoordX = left.GetTexCoordX() + gradients.GetTexCoordXXStep() * xPrestep;
        float texCoordY = left.GetTexCoordY() + gradients.GetTexCoordYXStep() * xPrestep;
        //Vector4f color = left.GetColor().Add(gradients.GetColorXStep().Mul(xPrestep));

        for(int i = xMin; i < xMax; i++)
        {
//            byte r = (byte)(color.GetX() * 255.0f + 0.5f);
//            byte g = (byte)(color.GetY() * 255.0f + 0.5f);
//            byte b = (byte)(color.GetZ() * 255.0f + 0.5f);
//
//            DrawPixel(i, j, (byte)0xFF, b, g, r);

            int srcX = (int)(texCoordX * ((texture.GetWidth() - 1) + 0.5f));
            int srcY = (int)(texCoordY * ((texture.GetWidth() - 1) + 0.5f));

            CopyPixel(i, j, srcX, srcY, texture);
            texCoordX += gradients.GetTexCoordXXStep();
            texCoordY += gradients.GetTexCoordYXStep();
            //color = color.Add(gradients.GetColorXStep());
        }
    }
}
