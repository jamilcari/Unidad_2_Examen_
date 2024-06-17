
package view;

import controller.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TresEnRayaUI extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private JButton btnIniciar;
    private JButton btnAnular;
    private JLabel lblTurno;
    private JLabel lblPuntajeJugador1;
    private JLabel lblPuntajeJugador2;
    private JTextField txtJugador1;
    private JTextField txtJugador2;
    private JTable tblResultados;
    private DefaultTableModel tblModel;

    private String currentPlayer = "X";
    private String nombrePartida;
    private String nombreJugador1;
    private String nombreJugador2;
    private boolean gameActive = false;

    public TresEnRayaUI() {
        setTitle("EVALUACIÓN UNIDAD 2");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Título y subtítulo
        JPanel pnlTitulo = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("EVALUACIÓN UNIDAD 2", JLabel.LEFT);
        JLabel lblSubtitulo = new JLabel("Jamil Neftali Ccari Cari", JLabel.LEFT);
        pnlTitulo.add(lblTitulo, BorderLayout.NORTH);
        pnlTitulo.add(lblSubtitulo, BorderLayout.SOUTH);

        // Cuadro 3x3 del juego
        JPanel pnlJuego = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setEnabled(false);
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                pnlJuego.add(buttons[i][j]);
            }
        }

        // Panel de información
        JPanel pnlInfo = new JPanel(new GridLayout(3, 2));
        lblTurno = new JLabel("Turno: ");
        lblPuntajeJugador1 = new JLabel("Puntaje Jugador 1: 0");
        lblPuntajeJugador2 = new JLabel("Puntaje Jugador 2: 0");
        pnlInfo.add(lblTurno);
        pnlInfo.add(new JLabel());  // Filler
        pnlInfo.add(lblPuntajeJugador1);
        pnlInfo.add(lblPuntajeJugador2);

        // Panel de control
        JPanel pnlControl = new JPanel(new GridLayout(3, 2));
        pnlControl.add(new JLabel("Nombre Jugador 1:"));
        txtJugador1 = new JTextField("Jamil");
        pnlControl.add(txtJugador1);
        pnlControl.add(new JLabel("Nombre Jugador 2:"));
        txtJugador2 = new JTextField("Royer");
        pnlControl.add(txtJugador2);
        btnIniciar = new JButton("Iniciar Juego");
        btnAnular = new JButton("Anular");
        btnAnular.setEnabled(false);
        pnlControl.add(btnIniciar);
        pnlControl.add(btnAnular);

        // Tabla de puntajes
        tblModel = new DefaultTableModel(new String[]{"Nombre Partido", "Nombre Jugador 1", "Nombre Jugador 2", "Ganador", "Estado"}, 0);
        tblResultados = new JTable(tblModel);
        JScrollPane scrollPane = new JScrollPane(tblResultados);

        // Añadir paneles al JFrame
        add(pnlTitulo, BorderLayout.NORTH);
        add(pnlJuego, BorderLayout.CENTER);
        add(pnlInfo, BorderLayout.SOUTH);
        add(pnlControl, BorderLayout.EAST);
        add(scrollPane, BorderLayout.WEST);

        // Añadir eventos a los botones
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });

        btnAnular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anularJuego();
            }
        });

        setVisible(true);
    }

    private void iniciarJuego() {
        btnIniciar.setEnabled(false);
        btnAnular.setEnabled(true);
        gameActive = true;
        currentPlayer = "X"; // Restablece al jugador "X" al inicio del juego
        lblTurno.setText("Turno: " + currentPlayer);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(true);
                buttons[i][j].setText("");
                for (ActionListener al : buttons[i][j].getActionListeners()) {
                    buttons[i][j].removeActionListener(al);
                }
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (gameActive) {
                            JButton buttonClicked = (JButton) e.getSource();
                            buttonClicked.setText(currentPlayer);
                            buttonClicked.setEnabled(false);
                            if (checkForWin()) {
                                gameActive = false;
                                String ganador = currentPlayer.equals("X") ? nombreJugador1 : nombreJugador2;
                                DatabaseManager.actualizarResultado(nombrePartida, ganador, 1, "Terminado");
                                actualizarTablaResultados(nombrePartida, ganador, "Terminado");
                                actualizarPuntajes(ganador);
                                JOptionPane.showMessageDialog(null, "El ganador es: " + ganador);
                                anularJuego();
                            } else if (isBoardFull()) {
                                gameActive = false;
                                DatabaseManager.actualizarResultado(nombrePartida, "Empate", 0, "Terminado");
                                actualizarTablaResultados(nombrePartida, "Empate", "Terminado");
                                JOptionPane.showMessageDialog(null, "El juego terminó en empate.");
                                anularJuego();
                            } else {
                                switchPlayer();
                            }
                        }
                    }
                });
            }
        }

        nombrePartida = "Partida " + (tblModel.getRowCount() + 1);
        nombreJugador1 = txtJugador1.getText();
        nombreJugador2 = txtJugador2.getText();
        DatabaseManager.insertarResultado(nombrePartida, nombreJugador1, nombreJugador2, "", 0, "Jugando");
        tblModel.addRow(new Object[]{nombrePartida, nombreJugador1, nombreJugador2, "", "Jugando"});
    }

    private void anularJuego() {
        btnIniciar.setEnabled(true);
        btnAnular.setEnabled(false);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
        if (gameActive) {
            DatabaseManager.actualizarResultado(nombrePartida, "", 0, "Anulado");
            actualizarTablaResultados(nombrePartida, "", "Anulado");
        }
        gameActive = false;
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
        lblTurno.setText("Turno: " + currentPlayer);
    }

    private boolean checkForWin() {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(currentPlayer) &&
                buttons[i][1].getText().equals(currentPlayer) &&
                buttons[i][2].getText().equals(currentPlayer)) {
                return true;
            }
            if (buttons[0][i].getText().equals(currentPlayer) &&
                buttons[1][i].getText().equals(currentPlayer) &&
                buttons[2][i].getText().equals(currentPlayer)) {
                return true;
            }
        }
        if (buttons[0][0].getText().equals(currentPlayer) &&
            buttons[1][1].getText().equals(currentPlayer) &&
            buttons[2][2].getText().equals(currentPlayer)) {
            return true;
        }
        if (buttons[0][2].getText().equals(currentPlayer) &&
            buttons[1][1].getText().equals(currentPlayer) &&
            buttons[2][0].getText().equals(currentPlayer)) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void actualizarTablaResultados(String nombrePartida, String ganador, String estado) {
        for (int i = 0; i < tblModel.getRowCount(); i++) {
            if (tblModel.getValueAt(i, 0).equals(nombrePartida)) {
                tblModel.setValueAt(ganador, i, 3);
                tblModel.setValueAt(estado, i, 4);
                break;
            }
        }
    }

    private void actualizarPuntajes(String ganador) {
        if (ganador.equals(nombreJugador1)) {
            int puntaje = Integer.parseInt(lblPuntajeJugador1.getText().split(": ")[1]) + 1;
            lblPuntajeJugador1.setText("Puntaje Jugador 1: " + puntaje);
        } else if (ganador.equals(nombreJugador2)) {
            int puntaje = Integer.parseInt(lblPuntajeJugador2.getText().split(": ")[1]) + 1;
            lblPuntajeJugador2.setText("Puntaje Jugador 2: " + puntaje);
        }
    }

    
}
