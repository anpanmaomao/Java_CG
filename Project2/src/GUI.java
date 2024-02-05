import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    private String filepath;

    JPanel panel, panel2;

    public GUI(String filepath) {
        super("3D Visualization Modes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        this.filepath = filepath;

        setLayout(new BorderLayout());

        // パネルを作成し、フレームに追加
        panel = new JPanel();
        add(panel, BorderLayout.NORTH);

        panel2 = new JPanel();
        add(panel2, BorderLayout.CENTER);


        // ボタンの作成と追加
        String[] buttonLabels = {"Point-cloud", "Wireframe", "Filled with Edges", "Flat Shading", "Smooth Shading"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ModeActionListener(label));
            panel.add(button);
        }
    }

    private class ModeActionListener implements ActionListener {
        private String mode;

        public ModeActionListener(String mode) {
            this.mode = mode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Container contentPane = getContentPane();
            // contentPane.removeAll();

            panel2.removeAll();

            switch (mode) {
                case "Point-cloud":
                    // Point-cloud表示モードに切り替えるコード
                    panel2.add(new Points(filepath));
                    //panel2.add(new JButton("AAA"));
                    break;
                case "Wireframe":
                    // Wireframe表示モードに切り替えるコード
                    panel2.add(new Wireframe(filepath));
                    break;
                case "Filled with Edges":
                    // 塗りつぶし＋エッジ表示モードに切り替えるコード
                    panel2.add(new Filled(filepath));
                    break;
                case "Flat Shading":
                    // フラットシェーディングモードに切り替えるコード
                    panel2.add(new FlatShading(filepath));
                    break;
                case "Smooth Shading":
                    // スムースシェーディングモードに切り替えるコード
                    panel2.add(new SmoothShading(filepath));
                    break;
            }
            panel2.revalidate();
            panel2.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI(args[0]).setVisible(true);
        });
    }
}
