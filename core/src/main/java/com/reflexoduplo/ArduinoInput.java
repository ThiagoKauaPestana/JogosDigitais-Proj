package com.reflexoduplo;

import com.fazecast.jSerialComm.SerialPort;

/**
 * ArduinoInput — Leitura dos botões físicos via porta Serial (USB).
 *
 * PROTOCOLO SERIAL ESPERADO DO ARDUINO:
 *   - Arduino envia '1' quando o Botão 1 é pressionado
 *   - Arduino envia '2' quando o Botão 2 é pressionado
 *   - Baud rate: 9600
 *
 * SKETCH ARDUINO (cole no Arduino IDE):
 * -------------------------------------------------------
 *   const int PINO_BTN1 = 2;
 *   const int PINO_BTN2 = 3;
 *   bool estado1 = HIGH;
 *   bool estado2 = HIGH;
 *
 *   void setup() {
 *     Serial.begin(9600);
 *     pinMode(PINO_BTN1, INPUT_PULLUP);
 *     pinMode(PINO_BTN2, INPUT_PULLUP);
 *   }
 *
 *   void loop() {
 *     bool atual1 = digitalRead(PINO_BTN1);
 *     bool atual2 = digitalRead(PINO_BTN2);
 *     if (atual1 == LOW && estado1 == HIGH) Serial.print('1');
 *     if (atual2 == LOW && estado2 == HIGH) Serial.print('2');
 *     estado1 = atual1;
 *     estado2 = atual2;
 *     delay(20);
 *   }
 * -------------------------------------------------------
 *
 * DEPENDÊNCIA (adicionar ao build.gradle do core):
 *   implementation 'com.fazecast:jSerialComm:2.10.4'
 */
public class ArduinoInput {

    private SerialPort porta;
    private boolean botao1Pressionado = false;
    private boolean botao2Pressionado = false;
    private boolean conectado         = false;

    // Nome da porta — ajuste conforme seu sistema:
    //   Windows: "COM3", "COM4", ...
    //   Linux:   "/dev/ttyUSB0", "/dev/ttyACM0"
    //   macOS:   "/dev/cu.usbmodem..."
    private static final String PORTA_PADRAO = "/dev/ttyUSB0";
    private static final int    BAUD_RATE    = 9600;

    public ArduinoInput() {
        conectar(PORTA_PADRAO);
    }

    public ArduinoInput(String nomePorta) {
        conectar(nomePorta);
    }

    private void conectar(String nomePorta) {
        try {
            porta = SerialPort.getCommPort(nomePorta);
            porta.setBaudRate(BAUD_RATE);
            porta.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

            if (porta.openPort()) {
                conectado = true;
                System.out.println("[Arduino] Conectado em: " + nomePorta);
            } else {
                System.out.println("[Arduino] Falha ao abrir porta: " + nomePorta);
                listarPortas();
            }
        } catch (Exception e) {
            System.out.println("[Arduino] Erro ao conectar: " + e.getMessage());
            listarPortas();
        }
    }

    /**
     * Lê os bytes disponíveis na porta serial e atualiza os estados dos botões.
     * Deve ser chamado uma vez por frame em GameScreen.render().
     */
    public void poll() {
        botao1Pressionado = false;
        botao2Pressionado = false;

        if (!conectado || porta == null) return;

        int disponivel = porta.bytesAvailable();
        if (disponivel <= 0) return;

        byte[] buffer = new byte[disponivel];
        porta.readBytes(buffer, disponivel);

        for (byte b : buffer) {
            char c = (char) b;
            if (c == '1') botao1Pressionado = true;
            if (c == '2') botao2Pressionado = true;
        }
    }

    public boolean isBotao1Pressionado() { return botao1Pressionado; }
    public boolean isBotao2Pressionado() { return botao2Pressionado; }
    public boolean isConectado()         { return conectado; }

    /** Lista as portas disponíveis no console — útil para descobrir o nome correto. */
    public static void listarPortas() {
        System.out.println("[Arduino] Portas disponíveis:");
        for (SerialPort p : SerialPort.getCommPorts()) {
            System.out.println("  -> " + p.getSystemPortName()
                + " | " + p.getDescriptivePortName());
        }
    }

    public void dispose() {
        if (porta != null && porta.isOpen()) {
            porta.closePort();
            System.out.println("[Arduino] Porta fechada.");
        }
    }
}