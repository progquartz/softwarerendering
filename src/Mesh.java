

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private List<Vertex> m_Vertices;
    private List<Integer> m_indices;

    public Vertex GetVertex (int i){ return m_Vertices.get(i);}
    public int GetIndex(int i ) { return m_indices.get(i);}
    public int GetNumIndices(){ return m_indices.size();}

    public Mesh(String filename) throws IOException {
        IndexedModel model = new OBJModel(filename).ToIndexedModel();

        m_Vertices = new ArrayList<Vertex>();
        for(int i = 0 ; i < model.GetPositions().size() ; i++){
            m_Vertices.add(new Vertex( model.GetPositions().get(i),model.GetTexCoords().get(i)));
        }

        m_indices = model.GetIndices();

    }
    public void Draw(RenderContext context, Matrix4f transform, Bitmap texture)
    {
        for(int i = 0; i < m_indices.size(); i += 3)
        {
            context.DrawTriangle(
                    m_Vertices.get(m_indices.get(i)).Transform(transform),
                    m_Vertices.get(m_indices.get(i + 1)).Transform(transform),
                    m_Vertices.get(m_indices.get(i + 2)).Transform(transform),
                    texture);
        }
    }
}
