package io;

import java.io.*;
import model.*;

public class GameModelReader {
    public static void parse(GameModel model, BufferedReader reader) throws MapIOException {
        try
        {
            String sCurrentLine;
            sCurrentLine = reader.readLine();
            String[] dimensions = sCurrentLine.split(" ");
            
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            model.rebuild(width, height);
            
            for (int i = 0; i < height; ++i) {
                sCurrentLine = reader.readLine();
                String[] tags = sCurrentLine.split(" ");
                
                for (int j = 0; j < width; ++j)    {
                    GameObjectDescription d;
                    d = GameObjectDescription.getDescription(tags[j].charAt(0));
                    if (d == null) {
                        throw new MapIOException("Invalid chracter at " + (new Vector2D(i, j)).toString() );
                    }

                    model.addImmovableObject(i, j, d);
                }
            }
 
        } catch (IOException e) {
            throw new MapIOException("Cannot read map file.");
        } catch (ModelException e) {
			throw new MapIOException(e.getMessage());
		}
    }
}
