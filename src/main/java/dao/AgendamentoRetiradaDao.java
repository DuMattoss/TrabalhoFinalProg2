package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.example.carrosuenp.AgendamentoRetirada;

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
        if (ag.getDataRetirada() == null && ag.getData() != null) {
            ag.setDataRetirada(ag.getData());
        }
        if (ag.getDataDevolucao() == null) {
            ag.setDataDevolucao(ag.getDataRetirada());
        }
        collection.insertOne(ag);
    }

    public List<AgendamentoRetirada> listarPorData(String dataISO) {
        if (dataISO == null) dataISO = "";
        Bson cobreODiaNovo = and(lte("dataRetirada", dataISO), gte("dataDevolucao", dataISO));
        Bson legadoMesmoDia = eq("data", dataISO);
        return collection.find(or(cobreODiaNovo, legadoMesmoDia))
                .sort(Sorts.ascending("placa"))
                .into(new ArrayList<>());
    }

    public List<AgendamentoRetirada> listarConflitantes(String inicioISO, String fimISO) {
        if (inicioISO == null || fimISO == null) return List.of();
        Bson conflitoNovos = and(lte("dataRetirada", fimISO), gte("dataDevolucao", inicioISO));
        Bson conflitoLegado = and(gte("data", inicioISO), lte("data", fimISO));
        return collection.find(or(conflitoNovos, conflitoLegado))
                .sort(Sorts.ascending("placa"))
                .into(new ArrayList<>());
    }

    public List<AgendamentoRetirada> listarPeriodo(String inicioISO, String fimISO) {
        if (inicioISO == null || fimISO == null) return List.of();
        Bson cobrePeriodoNovos = and(lte("dataRetirada", fimISO), gte("dataDevolucao", inicioISO));
        Bson legadoDentro = and(gte("data", inicioISO), lte("data", fimISO));
        return collection.find(or(cobrePeriodoNovos, legadoDentro))
                .sort(Sorts.ascending("dataRetirada"))
                .into(new ArrayList<>());
    }

    public boolean existeConflitoReserva(String placa, String inicioISO, String fimISO) {
        if (placa == null || inicioISO == null || fimISO == null) return false;
        Bson conflitoNovos = and(eq("placa", placa), lte("dataRetirada", fimISO), gte("dataDevolucao", inicioISO));
        Bson conflitoLegado = and(eq("placa", placa), gte("data", inicioISO), lte("data", fimISO));
        long qtd = collection.countDocuments(or(conflitoNovos, conflitoLegado));
        return qtd > 0;
    }

    public long atualizarPeriodo(String placa, String dataRetOld, String dataDevOld, String novaRet, String novaDev) {
        Bson filtroNovos = and(eq("placa", placa), eq("dataRetirada", dataRetOld), eq("dataDevolucao", dataDevOld));
        long m1 = collection.updateOne(filtroNovos, combine(
                set("dataRetirada", novaRet),
                set("dataDevolucao", novaDev),
                set("data", novaRet)
        )).getModifiedCount();
        if (m1 > 0) return m1;

        Bson filtroLegado = and(eq("placa", placa), eq("data", dataRetOld));
        long m2 = collection.updateOne(filtroLegado, combine(
                set("dataRetirada", novaRet),
                set("dataDevolucao", novaDev),
                set("data", novaRet)
        )).getModifiedCount();
        return m2;
    }

    public long atualizarPlaca(String placaOld, String dataRet, String dataDev, String placaNew) {
        Bson filtroNovos = and(eq("placa", placaOld), eq("dataRetirada", dataRet), eq("dataDevolucao", dataDev));
        long m1 = collection.updateOne(filtroNovos, set("placa", placaNew)).getModifiedCount();
        if (m1 > 0) return m1;

        Bson filtroLegado = and(eq("placa", placaOld), eq("data", dataRet));
        long m2 = collection.updateOne(filtroLegado, set("placa", placaNew)).getModifiedCount();
        return m2;
    }

    public long remover(String placa, String dataRet, String dataDev) {
        Bson filtroNovos = and(eq("placa", placa), eq("dataRetirada", dataRet), eq("dataDevolucao", dataDev));
        long d1 = collection.deleteOne(filtroNovos).getDeletedCount();
        if (d1 > 0) return d1;

        Bson filtroLegado = and(eq("placa", placa), eq("data", dataRet));
        long d2 = collection.deleteOne(filtroLegado).getDeletedCount();
        return d2;
    }

    public void marcarProcessado(String placa, String dataISO, boolean processado) {
        long mod = collection.updateMany(and(eq("placa", placa), eq("dataRetirada", dataISO)), set("processado", processado)).getModifiedCount();
        if (mod == 0) {
            collection.updateMany(and(eq("placa", placa), eq("data", dataISO)), set("processado", processado));
        }
    }
}
