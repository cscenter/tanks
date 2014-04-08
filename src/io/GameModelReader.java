package io;

import java.util.*;
import java.io.*;
import model.*;

public class GameModelReader {
    public static void parse(GameModel model, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
		{
			String sCurrentLine;
            sCurrentLine = br.readLine();
            String[] dimensions = sCurrentLine.split(" ");
            
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            model.rebuild(width, height);
            
            char letter;
            for (int i = 0; i < height; ++i) {
			    sCurrentLine = br.readLine();
			    String[] tags = sCurrentLine.split(" ");
			    
			    for (int j = 0; j < width; ++j)	{
			        GameObjectDescription d;
			        switch (tags[j].charAt(0)) {
			        case 'W':
			            d = GameObjectDescription.WATER;
			            break;
			        case 'O':
			            d = GameObjectDescription.WOODENWALL;
			            break;
			        case 'S':
			            d = GameObjectDescription.STONEWALL;
			            break;
			        case 'A':
			            d = GameObjectDescription.ASPHALT;
			            break;
			        default: ////// ERROR
			            d = GameObjectDescription.ASPHALT;
			            break;
			        }

			        if (!d.equals(GameObjectDescription.ASPHALT)) {
    			        model.addImmovableObject(i, j, d);
			        }
			    }
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
