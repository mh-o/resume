import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Pot {
  private static int the_pot;

  public Pot() {
    this.reset();
  }

  public static void addToPot(int val) {
    the_pot += val;
  }

  public static int getPot() {
    return the_pot;
  }

  public void reset() {
    the_pot = 0;
  }

  public static JPanel getPotJPanel() {
    JPanel the_panel = new JPanel(new FlowLayout());

    String temp = "POT: $" + Integer.toString(the_pot);
    JLabel pot_label = new JLabel(temp);
    pot_label.setForeground(Color.white);
    the_panel.setBackground(new Color(5, 81, 2));

    the_panel.add(pot_label);

    return the_panel;
  }
}
