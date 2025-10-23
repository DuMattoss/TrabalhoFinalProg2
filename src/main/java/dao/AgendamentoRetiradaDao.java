package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.example.carrosuenp.AgendamentoRetirada;
import org.example.carrosuenp.Veiculo;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class AgendamentoRetiradaDao {

    private final MongoCollection<AgendamentoRetirada> collection;

    public AgendamentoRetiradaDao() {
        this(MongoConn.db());
    }

    public AgendamentoRetiradaDao(MongoDatabase db) {
        this.collection = db.getCollection("AgendamentosRetirada", AgendamentoRetirada.class);
    }


    public void inserir(AgendamentoRetirada ag) {
        if (ag.getDataRetirada() == null) {
            throw new IllegalArgumentException("dataRetirada não pode ser null");
        }
        if (ag.getDataDevolucao() == null) {
            ag.setDataDevolucao(ag.getDataRetirada());
        }
        collection.insertOne(ag);
    }


    public List<AgendamentoRetirada> listarPorData(String dataISO) {
        if (dataISO == null) return List.of();
        Bson cobreODia = and(lte("dataRetirada", dataISO), gte("dataDevolucao", dataISO));
        return collection.find(cobreODia)
                .sort(Sorts.ascending("veiculo.placa", "dataRetirada"))
                .into(new ArrayList<>());
    }


    public List<AgendamentoRetirada> listarConflitantes(String inicioISO, String fimISO) {
        if (inicioISO == null || fimISO == null) return List.of();
        Bson conflito = and(lte("dataRetirada", fimISO), gte("dataDevolucao", inicioISO));
        return collection.find(conflito)
                .sort(Sorts.ascending("veiculo.placa", "dataRetirada"))
                .into(new ArrayList<>());
    }

    public List<AgendamentoRetirada> listarPeriodo(String inicioISO, String fimISO) {
        if (inicioISO == null || fimISO == null) return List.of();
        Bson cobrePeriodo = and(lte("dataRetirada", fimISO), gte("dataDevolucao", inicioISO));
        return collection.find(cobrePeriodo)
                .sort(Sorts.ascending("dataRetirada", "veiculo.placa"))
                .into(new ArrayList<>());
    }


    public boolean existeConflitoReserva(String placa, String inicioISO, String fimISO) {
        if (placa == null || inicioISO == null || fimISO == null) return false;
        Bson mesmoCarro = eq("veiculo.placa", placa);
        Bson overlap = and(lte("dataRetirada", fimISO), gte("dataDevolucao", inicioISO));
        long qtd = collection.countDocuments(and(mesmoCarro, overlap));
        return qtd > 0;
    }


    public long atualizarPeriodo(String placa, String dataRetOld, String dataDevOld, String novaRet, String novaDev) {
        Bson filtro = and(
                eq("veiculo.placa", placa),
                eq("dataRetirada", dataRetOld),
                eq("dataDevolucao", dataDevOld)
        );
        return collection.updateOne(
                filtro,
                combine(
                        set("dataRetirada", novaRet),
                        set("dataDevolucao", novaDev)
                )
        ).getModifiedCount();
    }


    public long atualizarPlaca(String placaOld, String dataRet, String dataDev, String placaNew) {
        Bson filtro = and(
                eq("veiculo.placa", placaOld),
                eq("dataRetirada", dataRet),
                eq("dataDevolucao", dataDev)
        );
        return collection.updateOne(filtro, set("veiculo.placa", placaNew)).getModifiedCount();
    }



    public long atualizarVeiculo(String placaOld, String dataRet, String dataDev, Veiculo novoVeiculo) {
        Bson filtro = and(
                eq("veiculo.placa", placaOld),
                eq("dataRetirada", dataRet),
                eq("dataDevolucao", dataDev)
        );
        return collection.updateOne(filtro, set("veiculo", novoVeiculo)).getModifiedCount();
    }


    public long remover(String placa, String dataRet, String dataDev) {
        Bson filtro = and(
                eq("veiculo.placa", placa),
                eq("dataRetirada", dataRet),
                eq("dataDevolucao", dataDev)
        );
        return collection.deleteOne(filtro).getDeletedCount();
    }
}
