import logger.Logger;
import logger.MessageLogger;
import logger.ServerLogger;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {
    private static final Map<String, String> connectedClients = new HashMap<>();
    private static final Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        //  Занимаем порт, определяя серверный сокет
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 8081));
        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        ServerLogger.log("Server start");
        try {
            while (true) {
                selector.select(); // Blocking call, but only one for everything
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isValid()) {
                        try {
                            if (key.isAcceptable()) {
                                SocketChannel socketChannel = serverChannel.accept(); // Non blocking, never null
                                socketChannel.configureBlocking(false);

                                ServerLogger.log("Connected " + socketChannel.getRemoteAddress());
                                sockets.put(socketChannel, ByteBuffer.allocate(1000)); // Allocating buffer for socket channel
                                socketChannel.register(selector, SelectionKey.OP_READ);
                            } else if (key.isReadable()) {
                                SocketChannel socketChannel = (SocketChannel) key.channel();
                                ByteBuffer buffer = sockets.get(socketChannel);
                                int bytesRead = 0;
                                try {
                                    bytesRead = socketChannel.read(buffer); // Reading, non-blocking call
                                } catch (IOException e) {
                                    ServerLogger.log("Error: " + e.getMessage());
                                    bytesRead = -1;
                                }

                                // Detecting connection closed from client side
                                if (bytesRead == -1) {
                                    ServerLogger.log("Connection closed " + socketChannel.getRemoteAddress());
                                    connectedClients.remove(socketChannel.getRemoteAddress().toString().substring(1));
                                    sockets.remove(socketChannel);
                                    socketChannel.close();
                                } else {
                                    String clientMessage = new String(buffer.array(), 0, bytesRead, UTF_8);
                                    System.out.println("Client Message: " + clientMessage);
                                    if (isJSONValid(clientMessage)) {
                                        JSONObject jo = new JSONObject(clientMessage);
                                        try {
                                            String ehlo = jo.getString("ehlo");
                                            long countClient = connectedClients.entrySet()
                                                    .stream()
                                                    .filter(f -> {
                                                        try {
                                                            return f.getKey().equals(ehlo) &&
                                                                    f.getValue().equals(socketChannel.getRemoteAddress().toString());
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        return false;
                                                    })
                                                    .count();
                                            if (countClient <= 0) {
                                                connectedClients.put(socketChannel.getRemoteAddress().toString().substring(1), ehlo);
                                            }

                                        } catch (JSONException ignored) { }

                                        try {
                                            MessageLogger.log(jo.getString("nick"), jo.getString("message"));
                                        } catch (JSONException ignored) { }

                                        for (Map.Entry<String, String> map : connectedClients.entrySet()) {
                                            System.out.println(map.getKey() + " | " + map.getValue());
                                        }
                                    }
                                    /*log("Reading from " + socketChannel.getRemoteAddress() + ", bytes read = " + bytesRead + ", client input: " +
                                            clientInNum);*/
                                }

                                // Detecting end of the message
                                if (bytesRead > 0 && buffer.get(buffer.position() - 1) == '\n') {
                                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                                }
                            } else if (key.isWritable()) {
                                SocketChannel socketChannel = (SocketChannel) key.channel();
                                ByteBuffer buffer = sockets.get(socketChannel);

                                // Reading client message from buffer
                                buffer.flip();
                                String clientMessage = new String(buffer.array(), buffer.position(), buffer.limit());

                                // Writing response to buffer
                                buffer.clear();
                                buffer.put(ByteBuffer.wrap(clientMessage.getBytes()));
                                buffer.flip();

                                int bytesWritten = socketChannel.write(buffer); // woun't always write anything
                                /*log("Writing to " + socketChannel.getRemoteAddress() + ", bytes writteb = " + bytesWritten);*/
                                if (!buffer.hasRemaining()) {
                                    buffer.compact();
                                    socketChannel.register(selector, SelectionKey.OP_READ);
                                }
                            }
                        } catch (IOException e) {
                            ServerLogger.log("Error: " + e.getMessage());
                        }
                    }
                }
                selector.selectedKeys().clear();
            }
        } catch (IOException err) {
            System.out.println(err.getMessage());
        } finally {
            serverChannel.close();
        }
    }

    public void syncMessagesServerToClient() {

    }

    public static boolean isJSONValid(String s) {
        try {
            new JSONObject(s);
        } catch (JSONException e) {
            try {
                new JSONArray(s);
            } catch (JSONException e1) {
                return false;
            }
        }
        return true;
    }
}