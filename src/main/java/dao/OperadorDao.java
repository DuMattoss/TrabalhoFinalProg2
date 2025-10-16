package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.example.carrosuenp.Operador;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

public class OperadorDao {
    private final MongoCollection<Operador> collection;

    public OperadorDao() {
        MongoDatabase db = MongoConn.db();
        this.collection = db.getCollection("operadores", Operador.class);
        System.out.println("[OperadorDao] Conectado ao DB '" + db.getName() + "', coleção '"
                + collection.getNamespace().getCollectionName() + "'");
    }

    public void salvar(Operador operador) {
        collection.insertOne(operador);
    }

    public Operador buscarPorLogin(String login) {
        String quoted = java.util.regex.Pattern.quote(login);
        Bson filtro = regex("login", "^" + quoted + "$", "i");
        return collection.find(filtro).first();
    }

    public List<Operador> listarTodos() {
        return collection.find()
                .sort(Sorts.ascending("nome"))
                .into(new ArrayList<>());
    }

    public void atualizar(Operador operador) {
        if (operador == null || operador.getLogin() == null) return;
        collection.replaceOne(eq("login", operador.getLogin()), operador);
    }

    public void remover(String login) {
        if (login == null) return;
        collection.deleteOne(eq("login", login));
    }
}
