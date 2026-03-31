package com.hale.stats;

import com.hale.jpool.stats.PoolStats;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
public class StatsServer {

    private final PoolStats poolStats;
    private final Gson gson = new Gson();

    public StatsServer(PoolStats poolStats) {
        this.poolStats = poolStats;
    }

    public void server() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8000), 0);

            server.createContext("/stats", new StatsHandler(poolStats, gson));
            server.start();

            System.out.println("Server started on port 8000");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Handler for /stats endpoint
    static class StatsHandler implements HttpHandler {

        private final PoolStats poolStats;
        private final Gson gson;

        public StatsHandler(PoolStats poolStats, Gson gson) {
            this.poolStats = poolStats;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Convert PoolStats to DTO and then to JSON
            PoolStatsDTO dto = new PoolStatsDTO(poolStats);
            String jsonResponse = gson.toJson(dto);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
        }
    }
}