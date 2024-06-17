
package com.mycompany.mavenproject1;

import view.TresEnRayaUI;
import controller.DatabaseManager;

public class App {
    public static void main(String[] args) {
        DatabaseManager.createNewDatabase();
        DatabaseManager.createNewTable();
        new TresEnRayaUI();
    }
}

