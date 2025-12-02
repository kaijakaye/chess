package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Collection<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        /*Collection<Session> sessions = connections.get(gameID);
        if (sessions == null) {
            sessions = new CopyOnWriteArrayList<>();
            connections.put(gameID, sessions);
        }
        sessions.add(session);
*/
        connections.putIfAbsent(gameID, new CopyOnWriteArrayList<>());
        connections.get(gameID).add(session);
    }

    public void remove(Integer gameID, Session session) {
        Collection<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcastToSelf(Integer gameID, Session session, ServerMessage message) throws IOException {
        if (session == null) return;
        String msg = new Gson().toJson(message);
        session.getRemote().sendString(msg);
    }

    public void broadcastToOthers(Integer gameID, Session excludeSession, ServerMessage message) throws IOException {
        Collection<Session> sessions = connections.get(gameID);
        if (sessions == null) return;

        String msg = new Gson().toJson(message);

        for (Session s : sessions) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                s.getRemote().sendString(msg);
            }
        }
    }

    public void broadcastToAll(Integer gameID, Session excludeSession, ServerMessage message) throws IOException {
        Collection<Session> sessions = connections.get(gameID);
        if (sessions == null) return;

        String msg = new Gson().toJson(message);

        for (Session s : sessions) {
            s.getRemote().sendString(msg);
        }
    }
}
