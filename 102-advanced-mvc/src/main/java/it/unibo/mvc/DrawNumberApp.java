package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private DrawNumber model;
    private final List<DrawNumberView> views;
    

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) 
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
        final BufferedReader bf = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("config.yml")));
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
        new DrawNumberApp(new DrawNumberViewImpl());
    }
}
