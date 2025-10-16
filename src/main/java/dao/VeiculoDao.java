package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.example.carrosuenp.Veiculo;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class VeiculoDao {
    private final MongoCollection<Veiculo> collection;

    public VeiculoDao() {
        MongoDatabase db = MongoConn.db();
        this.collection = db.getCollection("Veiculos", Veiculo.class);
        System.out.println("[VeiculoDao] Banco: " + db.getName());
        System.out.println("[VeiculoDao] Coleção: " + collection.getNamespace().getCollectionName());
        System.out.println("[VeiculoDao] Total de documentos: " + collection.countDocuments());
    }

    public void salvar(Veiculo v) {
        collection.insertOne(v);
    }


    public List<Veiculo> listarTodos() {
        return collection.find()
                .sort(Sorts.ascending("placa"))
                .into(new ArrayList<>());
    }


    public Veiculo buscarPorPlaca(String placa) {
        return collection.find(eq("placa", placa)).first();
    }


    public void atualizar(Veiculo v) {
        if (v == null || v.getPlaca() == null) return;
        collection.replaceOne(eq("placa", v.getPlaca()), v);
    }


    public void remover(String placa) {
        if (placa == null) return;
        collection.deleteOne(eq("placa", placa));
        System.out.println("[VeiculoDao] Veículo removido: " + placa);
    }
}
