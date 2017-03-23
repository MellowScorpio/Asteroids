package com.amilek.managers;

import com.badlogic.gdx.Gdx;

import java.io.*;

public class Save {

    public static GameData gd;

    public static void save() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("highscores.sav")
            );
            out.writeObject(gd);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static void load() {
        if (!saveFileExists()) {
            init();
            return;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("highscores.sav")
            );
            try {
                gd = (GameData) in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Gdx.app.exit();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static boolean saveFileExists() {
        File f = new File("highscores.sav");
        return f.exists();
    }

    private static void init() {
        gd = new GameData();
        gd.init();
        save();
    }
}
