public class Vertex {
    private Vector4f m_pos;
    private Vector4f m_texCoords;


    public float GetX(){return m_pos.GetX();}
    public float GetY(){return m_pos.GetY();}
    public Vector4f GetTexCoords() { return m_texCoords; }
    public Vector4f GetPosition() { return m_pos;}

    public Vertex(float x, float y , float z){
        m_pos = new Vector4f(x,y,z,1);
    }

    public Vertex(Vector4f pos, Vector4f texCoords){
        m_pos = pos;
        m_texCoords = texCoords;
    }

    public Vertex Transform(Matrix4f transform){
        return new Vertex(transform.Transform(m_pos) , m_texCoords);
    }

    public Vertex PerspectiveDivide (){
        return new Vertex(new Vector4f(m_pos.GetX()/m_pos.GetW(), m_pos.GetY()/m_pos.GetW(), m_pos.GetZ()/m_pos.GetW(), m_pos.GetW()), m_texCoords);
    }

    public float TriangleAreaTimesTwo(Vertex b, Vertex c){
        float x1 = b.GetX() - m_pos.GetX();
        float y1 = b.GetY() - m_pos.GetY();

        float x2 = c.GetX() - m_pos.GetX();
        float y2 = c.GetY() - m_pos.GetY();

        return (x1 * y2 - x2 * y1);
    }
}
