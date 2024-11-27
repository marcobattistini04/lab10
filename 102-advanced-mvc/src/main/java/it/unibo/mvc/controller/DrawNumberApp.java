package it.unibo.mvc.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import it.unibo.mvc.model.Configuration;
import it.unibo.mvc.model.DrawNumber;
import it.unibo.mvc.model.DrawNumberImpl;
import it.unibo.mvc.model.DrawResult;
import it.unibo.mvc.view.DrawNumberView;
import it.unibo.mvc.view.DrawNumberViewImpl;
import it.unibo.mvc.view.PrintStreamView;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private DrawNumber model;
    private final List<DrawNumberView> views;
    

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final String file_name, final DrawNumberView... views) 
    throws FileNotFoundException, 
    IOException{
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        Configuration.Builder configBuilder = new Configuration.Builder();
        String line;
        final BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file_name)));
        line = bf.readLine();
        while (line != null) {
            String [] lineElements = line.split(": ");
            if (lineElements.length == 2) {
                int value = Integer.parseInt(lineElements[1]);
                if (lineElements[0].contains("minimum")) {
                    configBuilder.setMin(value);
                } else if (lineElements[0].contains("maximum")) {
                    configBuilder.setMax(value);
                } else if (lineElements[0].contains("attempts")) {
                    configBuilder.setAttempts(value);
                }
            } 
            else {
                for(final var view : views) {
                    view.displayError("Cannot understad line: " + line);
                }
            }
            line = bf.readLine();
        }
        bf.close();

        Configuration config = configBuilder.build();
        if (config.isConsistent()) {
            this.model = new DrawNumberImpl(config);
        } else {
            for(final var view : views) {
                view.displayError("Configuration given is not consistent. A default configuration will be setted");
                this.model = new DrawNumberImpl(new Configuration.Builder().build());
            }
        }
        
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) 
    throws FileNotFoundException,
    IOException {
        final String sep = File.separator;
        final String file_name = "src" + sep + "main" + sep + "resources" + sep + "config.yml";
        new DrawNumberApp(file_name, new DrawNumberViewImpl(),
                            new DrawNumberViewImpl(),
                            new PrintStreamView(System.out),
                            new PrintStreamView("output.txt"));
    }
}
