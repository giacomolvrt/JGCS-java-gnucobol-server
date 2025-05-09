package server;


import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class JGCServer {
	
    private static final int PORT = 8006;
    private static final Logger logger = Logger.getLogger(JGCServer.class);

    public static void main(String[] args) {
        // Configura Log4j
        PropertyConfigurator.configure("src/main/resources/log4j.properties");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server multithread in ascolto sulla porta " + PORT + "...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Connessione accettata da " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            logger.error("Errore nell'avvio del server: " + e.getMessage(), e);
        }
    }

    // Classe interna per gestire ogni client in un thread separato
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private static final Logger logger = Logger.getLogger(ClientHandler.class);
        private static final String MESSAGE_DUMP_FILE = "logs/messages.txt";

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())
            ) {
                Object received = in.readObject();

                if (received instanceof byte[]) {
                    byte[] data = (byte[]) received;
                    String message = new String(data);
                    int length = data.length;
                    /*    message    contiene la stringona del FE */
                    
                    logger.info("Lunghezza: " + length);
                    logger.info("Messaggio ricevuto: " + message);
                    
                    
                    
                    InputCobolMessage icm = new InputCobolMessage(message);

                    final CobolCaller caller = new CobolCaller(icm, "param1", "param2", "param3", true);
                    
                    OuputCobolMessage ocm = caller.getOutbound();
                    
                    
                    
                    // Aggiunge timestamp
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String responseMessage = "[" + timestamp + "] " + ocm.getOutMessage();
                    byte[] responseData = responseMessage.getBytes();

                    // Invia risposta
                    out.writeObject(responseData);
                    out.flush();

                    logger.info("Risposta inviata: " + responseMessage);

                    // Salva il messaggio su file
                    saveMessageToFile(responseMessage);
                } else {
                    logger.warn("Oggetto ricevuto non è un byte array.");
                }

            } catch (IOException | ClassNotFoundException e) {
                logger.error("Errore nella comunicazione con il client: " + e.getMessage(), e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    logger.error("Errore nella chiusura del socket: " + e.getMessage(), e);
                }
            }
        }

        private void saveMessageToFile(String message) {
            try (FileWriter fw = new FileWriter(MESSAGE_DUMP_FILE, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(message);
            } catch (IOException e) {
                logger.error("Errore nel salvataggio del messaggio su file: " + e.getMessage(), e);
            }
        }
    }
}