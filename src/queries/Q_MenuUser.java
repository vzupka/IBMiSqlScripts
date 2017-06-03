package queries;

import javax.swing.UIManager;

/**
 * User menu for the application calls Q_Menu with "false" parameter.
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_MenuUser {
    /**
     * Main method to create Q_Menu with "false" parameter
     * in order to omit unnecessary functions of it.
     * 
     * @param strings not used
     */
    public static void main(String... strings) {
        Q_Menu mnu = new Q_Menu();
        try {
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
           exc.printStackTrace();
        }
        mnu.createQ_Menu(false);
    }
}
