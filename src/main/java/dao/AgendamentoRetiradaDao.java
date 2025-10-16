package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.example.carrosuenp.AgendamentoRetirada;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;

public class AgendamentoRetiradaDao {

    private final MongoCollection<AgendamentoRetirada> collection;

    public AgendamentoRetiradaDao() {
        MongoDatabase db = MongoConn.db();
        this.collection = db.getCollection("AgendamentosRetirada", AgendamentoRetirada.class);
    }


    public void inserir(AgendamentoRetirada ag) {
        if (ag == null) return;
        collection.insertOne(ag);
    }


    public List<AgendamentoRetirada> listarTodos() {
        return collection
                .find()
                .sort(Sorts.ascending("data", "placa"))
                .into(new ArrayList<>());
    }

    public List<AgendamentoRetirada> listarPorData(String dataISO) {
        if (dataISO == null) return List.of();
        return collection
                .find(eq("data", dataISO))
                .sort(Sorts.ascending("placa"))
                .into(new ArrayList<>());
    }

    public boolean existeReserva(String placa, String dataISO) {
        if (placa == null || dataISO == null) return false;
        long qtd = collection.countDocuments(and(eq("placa", placa), eq("data", dataISO)));
        return qtd > 0;
    }

    }

