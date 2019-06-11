import javax.swing.*;

class guiComp {
  public static void main(String[] args) {
    gui myGUI = new gui();
    myGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    myGUI.setSize(1000, 500); // 90% fullscreen resolution
    myGUI.setVisible(true);
  }
}
