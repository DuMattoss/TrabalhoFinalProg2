package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.example.carrosuenp.Motorista;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MotoristaDao {
    private final MongoCollection<Motorista> collection;

    public MotoristaDao() {
        MongoDatabase db = MongoConn.db();
        this.collection = db.getCollection("Motoristas", Motorista.class);
        System.out.println("[MotoristaDao] Banco: " + db.getName());
        System.out.println("[MotoristaDao] Coleção: " + collection.getNamespace().getCollectionName());
        System.out.println("[MotoristaDao] Total de documentos: " + collection.countDocuments());
    }


    public void salvar(Motorista m) {
        collection.insertOne(m);
    }


    public List<Motorista> listarTodos() {
        return collection.find()
                .sort(Sorts.ascending("nome"))
                .into(new ArrayList<>());
    }

    public void atualizar(Motorista m) {
        if (m == null || m.getCodigo() == null) return;
        collection.replaceOne(eq("codigo", m.getCodigo()), m);
    }

    public void remover(String codigo) {
        if (codigo == null) return;
        collection.deleteOne(eq("codigo", codigo));
        System.out.println("[MotoristaDao] Motorista removido: " + codigo);
    }
}
