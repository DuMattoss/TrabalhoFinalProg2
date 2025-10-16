package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.example.carrosuenp.Operador;

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
        Operador op = collection.find(filtro).first();

        System.out.println("[OperadorDao] buscarPorLogin('" + login + "') -> "
                + (op == null ? "null" : "OK"));
        return op;
    }
}
