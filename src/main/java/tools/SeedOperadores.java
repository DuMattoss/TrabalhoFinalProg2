/*package tools;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class SeedOperadores {

    public static void main(String[] args) {
        // Você pode sobrescrever via -Dmongo.uri e -Dmongo.db ao rodar
        String uri   = System.getProperty("mongo.uri", "mongodb://localhost:27017");
        String db    = System.getProperty("mongo.db",  "carrosuenp");

        try (MongoClient client = MongoClients.create(uri)) {
            MongoDatabase database = client.getDatabase(db);
            MongoCollection<Document> col = database.getCollection("operadores");

            // 1) Índices úteis (idempotentes)
            criarIndices(col);

            // 2) Registros de exemplo (upsert = cria ou atualiza pelo login)
            upsertOperador(col, "copasxz", "123", "OP-123", "Operador Chefe", true);
            // Você pode adicionar mais linhas aqui:
            // upsertOperador(col, "maria",   "senha", "OP-777", "Maria Operadora", true);
            // upsertOperador(col, "joao",    "abc",   "OP-999", "João Operador",   false);

            System.out.println("Seed concluído com sucesso ");
        }
    }

    private static void criarIndices(MongoCollection<Document> col) {
        // Único por login (um operador por login)
        try {
            col.createIndex(new Document("login", 1), new IndexOptions().unique(true));
            System.out.println("Índice único em 'login' OK");
        } catch (Exception e) {
            System.out.println("Aviso: não foi possível criar índice único em 'login' (talvez já exista): " + e.getMessage());
        }

        // Não-único por código (se quiser tornar único, mude para unique(true))
        try {
            col.createIndex(new Document("codigo", 1), new IndexOptions().unique(false));
            System.out.println("Índice em 'codigo' OK");
        } catch (Exception e) {
            System.out.println("Aviso: não foi possível criar índice em 'codigo': " + e.getMessage());
        }
    }

    private static void upsertOperador(MongoCollection<Document> col,
                                       String login, String senha, String codigo, String nome, boolean ativo) {
        Document data = new Document("login", login)
                .append("senha",  senha)
                .append("codigo", codigo)
                .append("nome",   nome)
                .append("ativo",  ativo);

        col.updateOne(eq("login", login),
                new Document("$set", data),
                new UpdateOptions().upsert(true));

        System.out.printf("Upsert operador: login=%s, codigo=%s, ativo=%s%n", login, codigo, ativo);
    }
}
*/